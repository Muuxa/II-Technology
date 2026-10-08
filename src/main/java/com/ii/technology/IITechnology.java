package com.ii.technology;

import appeng.api.storage.StorageCells;
import com.ii.technology.cpu.IITechnologyCpuCommands;
import com.ii.technology.cpu.IITechnologyCpuPayloads;
import com.ii.technology.infinite.InfiniteCellHandler;
import com.ii.technology.infinite.InfiniteKinds;
import com.ii.technology.registry.IITechnologyBlockEntities;
import com.ii.technology.registry.IITechnologyBlocks;
import com.ii.technology.registry.IITechnologyItems;
import com.ii.technology.registry.IITechnologyTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforge.common.NeoForge;

/**
 * II Technology: the 2i material chain, optional Mekanism silica chemistry, AE2 infinite cell,
 * and a configurable 2i crafting CPU.
 *
 * <p>All registry ids use the {@code iitechnology} namespace.</p>
 */
@Mod(IITechnology.ID)
public final class IITechnology {
    /** The mod id (II Technology). */
    public static final String ID = "iitechnology";


    public IITechnology(IEventBus modBus, ModContainer container) {
        IITechnologyItems.register(modBus);
        IITechnologyTabs.register(modBus);
        IITechnologyBlocks.register(modBus);
        IITechnologyBlockEntities.register(modBus);
        modBus.addListener(IITechnologyBlocks::onCommonSetup);
        modBus.addListener(IITechnologyCpuPayloads::registerServerbound);
        NeoForge.EVENT_BUS.addListener(IITechnologyCpuCommands::register);

        // The "infinite ii cell" is a native AE2 storage cell implemented in this mod.
        StorageCells.addCellHandler(new InfiniteCellHandler());
        InfiniteKinds.registerDefaults();

        // Optional: the Mekanism silica chemical. Guarded so no Mekanism class loads without it.
        if (LoadingModList.get().getModFileById("mekanism") != null) {
            com.ii.technology.compat.mekanism.IITechnologyMekanism.register(modBus);
        }
    }
}
