package com.ii.technology.compat.ae2tnt.mixin;

import com.ii.technology.compat.tnt.TwoIDropHook;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * AE2 Tiny TNT support: plain sand destroyed by Tiny TNT has the usual 25% chance to leave an
 * a {@code 2i} behind (same behaviour as vanilla / Data_Energistics TNT).
 *
 * <p><b>Why a mixin is needed:</b> {@code appeng.entity.TinyTNTPrimedEntity} does not run a
 * vanilla {@code Explosion} over its blocks. Its {@code explode()} walks a manual {@code -2..2}
 * cube and calls {@code Level.setBlock(pos, AIR, 3)} itself, so
 * {@code ExplosionEvent.Detonate.getToBlow()} is <b>empty</b> and the ordinary event handler
 * never sees these blocks.</p>
 *
 * <p><b>Why {@code dropResources} is the injection point</b> (not {@code setBlock}): the two run
 * back-to-back with {@code dropResources} <b>first</b>. Intercepting {@code setBlock} would read
 * the block state after it had already been replaced by air, so the "is this sand?" test would
 * always fail. Hooking {@code dropResources} (whose 1st argument is the still-live
 * {@link BlockState}) sees the correct state at the correct time.</p>
 *
 * <p>{@code remap = false} (mod code compiled against official names); {@code require = 0} so a
 * mapping drift degrades to "no drops" rather than crashing.</p>
 */
@Mixin(value = appeng.entity.TinyTNTPrimedEntity.class, remap = false)
public abstract class AE2TinyTntMixin {

    @Redirect(
            method = "explode",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;dropResources("
                            + "Lnet/minecraft/world/level/block/state/BlockState;"
                            + "Lnet/minecraft/world/level/LevelAccessor;"
                            + "Lnet/minecraft/core/BlockPos;"
                            + "Lnet/minecraft/world/level/block/entity/BlockEntity;)V"),
            require = 0)
    private void iitechnology$drop2iOnSand(BlockState state, LevelAccessor level, BlockPos pos,
                                       BlockEntity blockEntity) {
        // The first argument is the block about to be destroyed - exactly what we need to test.
        if (level instanceof ServerLevel serverLevel && TwoIDropHook.isSand(state)) {
            LivingEntity owner = ((net.minecraft.world.entity.item.PrimedTnt) (Object) this).getOwner();
            TwoIDropHook.rollLimited(serverLevel, pos, owner);
        }
        Block.dropResources(state, level, pos, blockEntity);
    }
}
