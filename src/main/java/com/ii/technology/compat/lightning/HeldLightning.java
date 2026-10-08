package com.ii.technology.compat.lightning;

import java.lang.reflect.Method;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;

/**
 * Held/dropped lightning for 2i materials, routed through
 * <b>AE2 Lightning Tech</b>: it calls AE2LT's own
 * {@code com.moakiee.ae2lt.event.ArtificialLightningHandler#spawnArtificialLightning(...)}, the
 * exact method AE2LT's overload crystal uses.
 *
 * <p><b>Only active when AE2LT is installed.</b> Without it nothing happens (the item stays an
 * ordinary material). The call is made via reflection so this class never links against an AE2LT
 * type: II Technology still loads fine when AE2LT is absent, and a future AE2LT signature change
 * degrades to "no lightning" instead of a crash.</p>
 *
 * <p>Stateless: the interval is driven by the holder/entity tick counter ({@code % interval}), so
 * no per-tick item NBT is read or written.</p>
 */
public final class HeldLightning {

    /** Ticks between held-lightning strikes (main/off hand). */
    public static final int HELD_INTERVAL_TICKS = 200;
    /** Ticks between strikes for a dropped item. */
    public static final int DROPPED_INTERVAL_TICKS = 80;

    private static volatile boolean resolved = false;
    private static volatile Method spawnMethod = null;

    private HeldLightning() {
    }

    private static boolean ae2ltLoaded() {
        ModList list = ModList.get();
        return list != null && list.isLoaded("ae2lt");
    }

    /** Resolves (once) AE2LT's artificial-lightning entry point; null when unavailable. */
    private static Method spawnMethod() {
        if (!resolved) {
            synchronized (HeldLightning.class) {
                if (!resolved) {
                    Method found = null;
                    try {
                        Class<?> handler = Class.forName("com.moakiee.ae2lt.event.ArtificialLightningHandler");
                        found = handler.getMethod("spawnArtificialLightning",
                                ServerLevel.class, Vec3.class, ServerPlayer.class);
                    } catch (Throwable ignored) {
                        // AE2LT absent or method renamed -> stay null (no lightning).
                    }
                    spawnMethod = found;
                    resolved = true;
                }
            }
        }
        return spawnMethod;
    }

    /** Called from {@link net.minecraft.world.item.Item#inventoryTick}. */
    public static void tickHeld(ItemStack stack, Level level, Player player, boolean isSelected) {
        if (level.isClientSide) {
            return;
        }
        if (!(isSelected || player.getOffhandItem() == stack)) {
            return;
        }
        if (player.tickCount % HELD_INTERVAL_TICKS != 0) {
            return;
        }
        ServerPlayer cause = player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
        strike(level, player.position(), cause);
    }

    /** Called from {@link net.minecraft.world.item.Item#onEntityItemUpdate}. */
    public static boolean tickDropped(ItemStack stack, ItemEntity entity) {
        Level level = entity.level();
        if (!level.isClientSide && entity.tickCount % DROPPED_INTERVAL_TICKS == 0) {
            strike(level, entity.position(), null);
        }
        return false;
    }

    /** Routes a strike through AE2LT, but only when AE2LT is actually present. */
    private static void strike(Level level, Vec3 pos, ServerPlayer cause) {
        if (!(level instanceof ServerLevel serverLevel) || !ae2ltLoaded()) {
            return;
        }
        Method method = spawnMethod();
        if (method == null) {
            return;
        }
        try {
            method.invoke(null, serverLevel, pos, cause);
        } catch (Throwable ignored) {
            // Present but incompatible: quietly skip rather than spam logs every interval.
        }
    }
}
