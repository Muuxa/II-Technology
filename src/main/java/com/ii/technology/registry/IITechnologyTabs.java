package com.ii.technology.registry;

import com.ii.technology.IITechnology;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The mod's own creative tab: the 2i material chain, the infinite ii cell and the 2i crafting CPU. */
public final class IITechnologyTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IITechnology.ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
            TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.iitechnology"))
                    .icon(() -> new ItemStack(IITechnologyItems.TWO_I_4.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(new ItemStack(IITechnologyItems.TWO_I.get()));
                        output.accept(new ItemStack(IITechnologyItems.TWO_I_2.get()));
                        output.accept(new ItemStack(IITechnologyItems.TWO_I_3.get()));
                        output.accept(new ItemStack(IITechnologyItems.TWO_I_4.get()));
                        output.accept(new ItemStack(IITechnologyItems.INFINITE_2I.get()));
                        output.accept(new ItemStack(IITechnologyBlocks.TWO_I_CPU_ITEM.get()));
                    })
                    .build());

    private IITechnologyTabs() {}

    public static void register(IEventBus modBus) {
        TABS.register(modBus);
    }
}
