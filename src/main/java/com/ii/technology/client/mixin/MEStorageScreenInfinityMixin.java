package com.ii.technology.client.mixin;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AmountFormat;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.core.localization.ButtonToolTips;
import appeng.core.localization.Tooltips;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Renders {@code ∞} instead of a huge number in AE2 terminals for keys served by an infinite cell —
 * both the slot label and the hover tooltip amount line. A key counts as infinite only when its
 * reported amount reaches {@link com.ii.technology.infinite.InfiniteCellInventory#REPORTED_AMOUNT},
 * which ordinary cells never do.
 */
@Mixin(MEStorageScreen.class)
public abstract class MEStorageScreenInfinityMixin {
    @Redirect(
            method = "renderSlot",
            at = @At(value = "INVOKE",
                    target = "Lappeng/api/stacks/AEKey;formatAmount(JLappeng/api/stacks/AmountFormat;)Ljava/lang/String;"))
    private String iitech$infiniteAmount(AEKey key, long amount, AmountFormat format) {
        if (amount >= com.ii.technology.infinite.InfiniteCellInventory.REPORTED_AMOUNT) {
            return "∞";
        }
        return key.formatAmount(amount, format);
    }

    @Redirect(
            method = "renderGridInventoryEntryTooltip",
            at = @At(value = "INVOKE",
                    target = "Lappeng/core/localization/Tooltips;getAmountTooltip(Lappeng/core/localization/ButtonToolTips;Lappeng/api/stacks/AEKey;J)Lnet/minecraft/network/chat/Component;"))
    private Component iitech$infiniteTooltipAmount(ButtonToolTips base, AEKey what, long amount) {
        if (amount >= com.ii.technology.infinite.InfiniteCellInventory.REPORTED_AMOUNT) {
            return Component.literal("∞").withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE);
        }
        return Tooltips.getAmountTooltip(base, what, amount);
    }
}