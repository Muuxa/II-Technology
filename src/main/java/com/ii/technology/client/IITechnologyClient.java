package com.ii.technology.client;

import com.ii.technology.IITechnology;
import com.ii.technology.cpu.IITechnologyCpuPayloads.OpenCpuEditorPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Dedicated-client entry point; keeps the server free of GUI classes. */
@Mod(value = IITechnology.ID, dist = Dist.CLIENT)
public final class IITechnologyClient {
    public IITechnologyClient(ModContainer container, IEventBus modBus) {
        // Game-bus tooltip hook (infinite cell decoration).
        NeoForge.EVENT_BUS.register(InfiniteCellTooltip.class);

        // Mod-bus listeners (manual registration avoids the deprecated @EventBusSubscriber bus=).
        modBus.addListener(IITechnologyClient::registerTooltipFactories);
        modBus.addListener(IITechnologyClient::registerPayloads);
        modBus.addListener(InfiniteCellModels::onRegisterAdditional);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(
                OpenCpuEditorPayload.TYPE,
                OpenCpuEditorPayload.STREAM_CODEC,
                IITechnologyCpuClient::handleOpenEditor);
    }

    private static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(InfiniteIconsTooltip.class, ClientInfiniteIconsTooltip::new);
    }
}
