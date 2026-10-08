package com.ii.technology.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** Renders {@link InfiniteIconsTooltip}: a row of item icons, each labelled {@code ∞}. */
public final class ClientInfiniteIconsTooltip implements ClientTooltipComponent {
    private static final int SPACING = 18;
    private final List<ItemStack> icons;

    public ClientInfiniteIconsTooltip(InfiniteIconsTooltip data) {
        List<ItemStack> resolved = new ArrayList<>();
        for (String id : data.itemIds()) {
            ItemStack icon = resolveIcon(id);
            if (icon != null && !icon.isEmpty()) resolved.add(icon);
        }
        this.icons = resolved;
    }

    private static ItemStack resolveIcon(String token) {
        if (token == null) return null;
        ResourceLocation loc = ResourceLocation.tryParse(token);
        if (loc == null || !BuiltInRegistries.ITEM.containsKey(loc)) return null;
        return new ItemStack(BuiltInRegistries.ITEM.get(loc));
    }

    @Override
    public int getHeight() {
        return SPACING;
    }

    @Override
    public int getWidth(Font font) {
        return Math.max(0, icons.size()) * SPACING;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        int cx = x + 1;
        for (ItemStack icon : icons) {
            graphics.renderItem(icon, cx, y + 1);
            graphics.renderItemDecorations(font, icon, cx, y + 1, "∞");
            cx += SPACING;
        }
    }
}