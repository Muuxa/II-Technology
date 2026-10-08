package com.ii.technology.client;

import com.ii.technology.cpu.IITechnologyCpuPayloads.OpenCpuEditorPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Client-only payload handling for the 2i CPU editor. */
public final class IITechnologyCpuClient {
    private IITechnologyCpuClient() {
    }

    public static void handleOpenEditor(OpenCpuEditorPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> Minecraft.getInstance().setScreen(
                new TwoICpuEditScreen(payload.pos(), payload.storageBytes())));
    }
}
