package com.ii.technology.client;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;

/**
 * Tooltip component carrying the item ids an infinite cell declares. Rendering draws one item icon
 * per id with {@code ∞} overlaid at the bottom-right corner.
 */
public record InfiniteIconsTooltip(List<String> itemIds) implements TooltipComponent {
    public InfiniteIconsTooltip {
        itemIds = List.copyOf(itemIds);
    }
}