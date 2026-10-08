package com.ii.technology.client;

import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;

/** Client-side text for II Technology's infinite cell. */
public final class InfiniteTooltip {
    private InfiniteTooltip() {}

    /** Static description block shared by infinite cell items. */
    public static void appendItemText(List<Either<FormattedText, TooltipComponent>> lines) {
        lines.add(Either.left(Component.empty()));
        lines.add(Either.left(Component.literal("II Technology 无限元件")
                .withStyle(ChatFormatting.LIGHT_PURPLE)));
        lines.add(Either.left(Component.literal("已标记物品：无限取出")
                .withStyle(ChatFormatting.GRAY)));
        lines.add(Either.left(Component.literal("输入物品：会被销毁")
                .withStyle(ChatFormatting.DARK_GRAY)));
    }
}
