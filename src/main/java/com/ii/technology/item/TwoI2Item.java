package com.ii.technology.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

/**
 * The "2i (2)" material doubles as an AE2 Lightning Tech interaction tool:
 * right-clicking a vanilla lightning rod with it summons an EXTREME-high-voltage lightning
 * bolt, exactly like AE2LT's debug lightning rod.
 *
 * <p>AE2LT classifies natural-weather lightning as its extreme tier
 * ({@code LightningCollectorBlockEntity.captureLightning(...)} maps the
 * {@code ae2lt.natural_weather_lightning} NBT flag to {@code LightningKey.Tier.EXTREME_HIGH_VOLTAGE}),
 * so we stamp that same flag on the spawned bolt. The flag string is inlined so this class never
 * references an AE2LT class - it loads safely whether or not AE2LT is installed.</p>
 *
 * <p>The special behaviour is only active when AE2LT is present (otherwise right-clicking a rod
 * does nothing).</p>
 */
public class TwoI2Item extends Item {

    /** AE2LT's NBT marker that promotes a bolt to the extreme-high-voltage tier. */
    private static final String NATURAL_WEATHER_LIGHTNING_TAG = "ae2lt.natural_weather_lightning";

    public TwoI2Item(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.getBlockState(pos).is(Blocks.LIGHTNING_ROD)) {
            return InteractionResult.PASS;
        }
        if (!ae2ltLoaded()) {
            // Without AE2LT this is an ordinary material; leave the rod alone.
            return InteractionResult.PASS;
        }
        if (level instanceof ServerLevel serverLevel) {
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (bolt == null) {
                return InteractionResult.FAIL;
            }
            Vec3 target = Vec3.atBottomCenterOf(pos.above());
            bolt.moveTo(target.x, target.y, target.z);
            if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
                bolt.setCause(serverPlayer);
            }
            bolt.getPersistentData().putBoolean(NATURAL_WEATHER_LIGHTNING_TAG, true);
            serverLevel.addFreshEntity(bolt);
        }
        Player player = context.getPlayer();
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static boolean ae2ltLoaded() {
        @Nullable ModList modList = ModList.get();
        return modList != null && modList.isLoaded("ae2lt");
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (entity instanceof Player player) {
            com.ii.technology.compat.lightning.HeldLightning.tickHeld(stack, level, player, isSelected);
        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        com.ii.technology.compat.lightning.HeldLightning.tickDropped(stack, entity);
        return super.onEntityItemUpdate(stack, entity);
    }
}
