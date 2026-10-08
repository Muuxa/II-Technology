package com.ii.technology.compat.mekanism;

import com.ii.technology.IITechnology;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalBuilder;
import mekanism.common.registration.impl.ChemicalDeferredRegister;
import mekanism.common.registration.impl.DeferredChemical;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;

/**
 * Optional Mekanism integration: the custom chemical {@code iitechnology:silica} (gaseous silicon
 * dioxide, SiO2). Mekanism is NOT a prerequisite; the guard lives in {@link IITechnology}, which
 * checks the loading list before touching this class. Its recipes are data-driven and gated behind
 * a {@code neoforge:mod_loaded} condition.
 */
public final class IITechnologyMekanism {

    public static final ChemicalDeferredRegister CHEMICALS = new ChemicalDeferredRegister(IITechnology.ID);

    /** Gaseous silicon dioxide (SiO2); icon is the animated {@code iitechnology:item/silica} sprite. */
    public static final DeferredChemical<Chemical> SILICA = CHEMICALS.register("silica",
            () -> new Chemical(ChemicalBuilder
                    .builder(ResourceLocation.fromNamespaceAndPath(IITechnology.ID, "item/silica"))
                    .tint(0xFFE8D6A0)));

    private IITechnologyMekanism() {
    }

    /** Registers this integration. Must only be called when Mekanism is present. */
    public static void register(IEventBus modBus) {
        CHEMICALS.register(modBus);
    }
}