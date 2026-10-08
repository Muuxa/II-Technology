package com.ii.technology.client;

import appeng.api.client.StorageCellModels;
import com.ii.technology.registry.IITechnologyItems;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

/** Registers the "cell in a drive" model for the infinite ii cell. */
public final class InfiniteCellModels {
    private InfiniteCellModels() {}

    public static final ResourceLocation INFINITE_2I_DRIVE_MODEL =
            ResourceLocation.fromNamespaceAndPath("iitechnology", "block/drive/cells/infinite_2i_cell");

    private static boolean registered;

    @SubscribeEvent
    public static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        if (registered) return;
        StorageCellModels.registerModel(IITechnologyItems.INFINITE_2I.get(), INFINITE_2I_DRIVE_MODEL);
        registered = true;
    }
}