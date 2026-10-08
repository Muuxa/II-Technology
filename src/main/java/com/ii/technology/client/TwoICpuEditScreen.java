package com.ii.technology.client;

import com.ii.technology.cpu.IITechnologyCpuPayloads.UpdateCpuPayload;
import com.ii.technology.cpu.TwoICpuBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

/** Direct-input editor for the 2i CPU's storage pool. Parallel is fixed at 1. */
public final class TwoICpuEditScreen extends Screen {
    private final BlockPos pos;
    private final long initialStorageBytes;

    private EditBox storageBox;

    public TwoICpuEditScreen(BlockPos pos, long storageBytes) {
        super(Component.literal("2i CPU 编辑"));
        this.pos = pos;
        this.initialStorageBytes = storageBytes;
    }

    @Override
    protected void init() {
        int left = width / 2 - 110;
        storageBox = new EditBox(font, left, 88, 220, 20, Component.literal("合成存储容量"));
        storageBox.setValue(Long.toString(initialStorageBytes));
        storageBox.setMaxLength(19);
        addRenderableWidget(storageBox);
        addRenderableWidget(Button.builder(Component.literal("保存"), button -> save())
                .bounds(left, 126, 105, 20)
                .build());
        addRenderableWidget(Button.builder(Component.literal("取消"), button -> onClose())
                .bounds(left + 115, 126, 105, 20)
                .build());
    }

    private void save() {
        try {
            long storageBytes = Long.parseLong(storageBox.getValue().trim());
            if (storageBytes < TwoICpuBlockEntity.MIN_STORAGE_BYTES
                    || storageBytes > TwoICpuBlockEntity.MAX_STORAGE_BYTES) {
                error("存储容量必须在 " + TwoICpuBlockEntity.MIN_STORAGE_BYTES + " B 到 "
                        + TwoICpuBlockEntity.MAX_STORAGE_BYTES + " B 之间");
                return;
            }

            PacketDistributor.sendToServer(new UpdateCpuPayload(pos, storageBytes));
            onClose();
        } catch (NumberFormatException exception) {
            error("请输入整数");
        }
    }

    private static void error(String message) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(Component.literal(message), false);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        int left = width / 2 - 110;
        graphics.fill(left - 12, 10, left + 232, 162, 0xE0000000);
        graphics.fill(left - 11, 11, left + 231, 161, 0xFF202020);
        graphics.drawString(font, title.getString(), width / 2 - font.width(title) / 2, 24, 0xFFFFFFFF, false);
        graphics.drawString(font, "并行: 1 (固定)", left, 50, 0xFFD0D0D0, false);
        graphics.drawString(font, "存储容量 (B, 最小 1)", left, 74, 0xFFD0D0D0, false);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
