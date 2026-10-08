package com.ii.technology.compat.tnt;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionEvent;

import java.util.List;

/**
 * Vanilla TNT support: when a TNT explosion destroys plain sand, there is a 25% chance it
 * leaves a 2i behind. Bounded to TNT sources so creepers/beds/etc. don't trigger it. The
 * per-block roll is funnelled through {@link TwoIDropHook#rollLimited} so even a huge blast
 * cannot spawn an unbounded number of item entities in one tick.
 */
@EventBusSubscriber(modid = "iitechnology")
public final class VanillaTntExplosionHandler {

    private VanillaTntExplosionHandler() {
    }

    @SubscribeEvent
    public static void onDetonate(ExplosionEvent.Detonate event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        // Only TNT (not creepers, beds, respawn anchors, ...).
        if (!(event.getExplosion().getDirectSourceEntity() instanceof PrimedTnt tnt)) {
            return;
        }
        @org.jetbrains.annotations.Nullable LivingEntity owner = tnt.getOwner();
        List<BlockPos> toBlow = event.getExplosion().getToBlow();
        for (BlockPos pos : toBlow) {
            if (TwoIDropHook.isSand(level.getBlockState(pos))) {
                TwoIDropHook.rollLimited(level, pos, owner);
            }
        }
    }
}