package com.ii.technology.infinite;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The infinite cell item. All behaviour lives in {@link InfiniteCellInventory}; this class only
 * supplies the item id and the display name (a kind-declared title, else the default name).
 */
public final class InfiniteCellItem extends Item {
    public InfiniteCellItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        Component custom = stack.get(DataComponents.CUSTOM_NAME);
        if (custom != null) return custom;
        InfiniteKinds.Kind kind = InfiniteKinds.get(InfiniteCellInventory.kindId(stack));
        if (kind != null && kind.title() != null && !kind.title().isBlank()) {
            return Component.literal(kind.title());
        }
        return super.getName(stack);
    }
}