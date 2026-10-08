package com.ii.technology.client;

import com.mojang.datafixers.util.Either;
import com.ii.technology.infinite.InfiniteCellInventory;
import com.ii.technology.infinite.InfiniteCellItem;
import com.ii.technology.infinite.InfiniteKinds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

/**
 * Adds the static description and the "icon + ∞" row to infinite cell tooltips. Registered manually
 * from {@link IITechnologyClient} on the game event bus.
 */
public final class InfiniteCellTooltip {
    private InfiniteCellTooltip() {}

    @SubscribeEvent
    public static void onGather(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof InfiniteCellItem)) return;

        InfiniteKinds.Kind kind = InfiniteKinds.get(InfiniteCellInventory.kindId(stack));
        if (kind == null) {
            event.getTooltipElements().add(Either.left(
                    Component.literal("未指定无限内容").withStyle(ChatFormatting.DARK_GRAY)));
            return;
        }

        event.getTooltipElements().add(Either.left(Component.literal("无限内容：")
                .append(Component.literal(kind.items().size() + " 项").withStyle(ChatFormatting.WHITE))));
        event.getTooltipElements().add(Either.left(Component.literal("可无限取出：")));
        event.getTooltipElements().add(Either.right(new InfiniteIconsTooltip(kind.items())));
        InfiniteTooltip.appendItemText(event.getTooltipElements());
    }
}
