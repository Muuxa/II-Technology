package com.ii.technology.compat.tnt;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shared "special TNT may leave a 2i behind when it destroys plain sand" logic, used
 * by the optional Mixin hooks (AE2 Lightning Tech's overload TNT and Data_Energistics' TNT) and
 * by the vanilla-TNT event handler.
 *
 * <p>Only plain sand ({@code minecraft:sand}) counts - red sand is deliberately excluded.</p>
 *
 * <p><b>Lag control:</b> each sand block rolls a 25% chance, but drops are additionally capped
 * to {@link #MAX_DROPS_PER_TICK} per game tick (a tiny static counter reset whenever the game
 * time changes). This keeps a single explosion's expected yield at 25% per block while making
 * the worst case - the huge, multi-chunk Data Nuke clearing millions of blocks - strictly
 * bounded instead of spawning a crippling entity storm.</p>
 *
 * <p><b>Advancement:</b> the first time a blast drops a 2i, the player who placed/primed the TNT
 * that caused the blast is awarded the "Nanliang" advancement. The owner is threaded in by each
 * caller (vanilla/DE expose {@code PrimedTnt.getOwner()}; the AE2LT overload TNT caller is
 * resolved from the entity). Only one advancement per blast is awarded, via a per-tick guard.</p>
 */
public final class TwoIDropHook {

    /** 25% = 1 in 4. Kept as a fraction so the roll is integer-only. */
    public static final int CHANCE_NUM = 1;
    public static final int CHANCE_DEN = 4;

    /** Hard ceiling on 2i ItemEntities spawned per game tick, regardless of blast size. */
    public static final int MAX_DROPS_PER_TICK = 32;

    private static final ResourceLocation NANLIANG =
            ResourceLocation.fromNamespaceAndPath("iitechnology", "nanliang");

    private static long budgetTick = Long.MIN_VALUE;
    private static int budgetUsed = 0;
    private static long lastAwardTick = Long.MIN_VALUE;

    private TwoIDropHook() {
    }

    /** True only for plain sand (never red sand). */
    public static boolean isSand(BlockState state) {
        return state.is(Blocks.SAND);
    }

    /**
     * Rolls 25% for one destroyed sand block, subject to the per-tick drop budget. On a hit, a
     * 2i is dropped and (the first time in this tick) {@code owner} is granted the advancement.
     *
     * @param owner the player responsible for the blast, or {@code null} if unknown.
     */
    public static void rollLimited(ServerLevel level, BlockPos pos, LivingEntity owner) {
        // Synchronised: blasts can be attributed from multiple sources/threads (event + blast
        // tasks), and the per-tick budget/advancement guards are plain static counters.
        synchronized (TwoIDropHook.class) {
            long now = level.getGameTime();
            if (now != budgetTick) {
                budgetTick = now;
                budgetUsed = 0;
            }
            if (budgetUsed >= MAX_DROPS_PER_TICK) {
                return;
            }
            if (level.random.nextInt(CHANCE_DEN) < CHANCE_NUM) {
                budgetUsed++;
                drop(level, pos);
                awardAdvancement(level, now, owner);
            }
        }
    }

    /** Spawns exactly one 2i at {@code pos}. No-op if the item is somehow unregistered. */
    public static void drop(ServerLevel level, BlockPos pos) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("iitechnology", "2i"));
        if (item == null) {
            return;
        }
        ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                new ItemStack(item));
        level.addFreshEntity(entity);
    }

    /**
     * Grants the "Nanliang" advancement to the responsible player, at most once per game tick
     * across all blasts (so multi-cell/entity blasts cannot spam it).
     */
    public static void awardAdvancement(ServerLevel level, long now, LivingEntity owner) {
        if (now == lastAwardTick) {
            return;
        }
        ServerPlayer player = asServerPlayer(owner);
        if (player == null) {
            return;
        }
        net.minecraft.server.MinecraftServer server = level.getServer();
        if (server == null) {
            return;
        }
        AdvancementHolder advancement = server.getAdvancements().get(NANLIANG);
        if (advancement == null) {
            return; // data pack disabled it, or not loaded
        }
        if (player.getAdvancements().award(advancement, "code")) {
            lastAwardTick = now;
        }
    }

    private static ServerPlayer asServerPlayer(LivingEntity owner) {
        if (owner instanceof ServerPlayer serverPlayer) {
            return serverPlayer;
        }
        if (owner instanceof Player) {
            // Shouldn't happen on the logical server, but never assume.
            return null;
        }
        return null;
    }
}
