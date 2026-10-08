package com.ii.technology.compat.dataenergistics.mixin;

import com.fish_dan_.data_energistics.entity.explosive.AbstractFlatteningTntPrimedEntity;
import com.ii.technology.compat.tnt.TwoIDropHook;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Data_Energistics TNT support: the configurable TNT clears a region with
 * {@code Level.removeBlock(pos, false)} (which never drops the block). We intercept each such
 * removal and, for plain sand only, give a 25% chance to leave a 2i behind.
 *
 * <p>Runs only when Data_Energistics is present (the config's {@code requiredMods} + plugin
 * guard this). {@code remap = false} because the target is mod code compiled against official
 * names; {@code require = 0} so a mapping drift degrades to "no drops" instead of a crash.</p>
 */
@Mixin(value = AbstractFlatteningTntPrimedEntity.class, remap = false)
public abstract class DEConfigurableTntMixin {

    @Redirect(
            method = "explode",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"),
            require = 0)
    private boolean iitechnology$drop2iOnSand(Level level, BlockPos pos, boolean moving) {
        if (level instanceof ServerLevel serverLevel && TwoIDropHook.isSand(level.getBlockState(pos))) {
            // The mixin is applied to a PrimedTnt subclass, so 'this' carries the placing player.
            LivingEntity owner = ((net.minecraft.world.entity.item.PrimedTnt) (Object) this).getOwner();
            TwoIDropHook.rollLimited(serverLevel, pos, owner);
        }
        return level.removeBlock(pos, moving);
    }
}