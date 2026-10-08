package com.ii.technology.item;

import com.ii.technology.compat.lightning.HeldLightning;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * The "2i (3)" material, with the animated electric-sweep overlay (model layer1) plus
 * overload-crystal-like held lightning routed through AE2 Lightning Tech (only when AE2LT is
 * installed).
 */
public class TwoI3Item extends Item {

    public TwoI3Item(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (entity instanceof Player player) {
            HeldLightning.tickHeld(stack, level, player, isSelected);
        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        HeldLightning.tickDropped(stack, entity);
        return super.onEntityItemUpdate(stack, entity);
    }
}
