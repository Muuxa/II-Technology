package com.ii.technology.cpu;

import appeng.block.crafting.AbstractCraftingUnitBlock;
import com.ii.technology.registry.IITechnologyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** AE2 crafting CPU block whose storage and parallel values can be edited in-world. */
public final class TwoICpuBlock extends AbstractCraftingUnitBlock<TwoICpuBlockEntity> {
    public TwoICpuBlock(Properties properties) {
        super(properties, TwoICpuType.INSTANCE);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TwoICpuBlockEntity(IITechnologyBlockEntities.TWO_I_CPU.get(), pos, state);
    }

    @Override
    public BlockEntityType<TwoICpuBlockEntity> getBlockEntityType() {
        return IITechnologyBlockEntities.TWO_I_CPU.get();
    }

    @Override
    public TwoICpuBlockEntity getBlockEntity(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof TwoICpuBlockEntity cpu ? cpu : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            openEditor(level, pos, player);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            openEditor(level, pos, player);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    private static void openEditor(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof TwoICpuBlockEntity cpu) {
            cpu.openEditor(player);
        }
    }
}
