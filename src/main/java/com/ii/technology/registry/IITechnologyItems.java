package com.ii.technology.registry;

import com.ii.technology.IITechnology;
import com.ii.technology.infinite.InfiniteCellInventory;
import com.ii.technology.infinite.InfiniteCellItem;
import com.ii.technology.infinite.InfiniteKinds;
import com.ii.technology.item.TwoI2Item;
import com.ii.technology.item.TwoI3Item;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The "2i" material chain plus the infinite ii cell. Registered under the {@code iitechnology}
 * namespace ({@link IITechnology#ID}).
 */
public final class IITechnologyItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(IITechnology.ID);

    /** Material "2i" (gaseous SiO2 infusing input). */
    public static final DeferredItem<Item> TWO_I = ITEMS.register("2i",
            () -> new Item(new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON)));

    /** Output of infusing 2i with silica; shares the "2i" display name but has its own id. */
    public static final DeferredItem<Item> TWO_I_2 = ITEMS.register("2i_2",
            () -> new TwoI2Item(new Item.Properties().fireResistant().rarity(Rarity.RARE)));

    /** Additional "2i"-named material with its own id; carriable lightning source (AE2LT-gated). */
    public static final DeferredItem<Item> TWO_I_3 = ITEMS.register("2i_3",
            () -> new TwoI3Item(new Item.Properties().fireResistant().rarity(Rarity.RARE)));

    /** Fourth "2i"-named material; assembled from 2i_3. */
    public static final DeferredItem<Item> TWO_I_4 = ITEMS.register("2i_4",
            () -> new Item(new Item.Properties().fireResistant().rarity(Rarity.RARE)));

    /** The infinite ii cell: infinite 2i + sand + the 16 concrete powders. */
    public static final DeferredItem<InfiniteCellItem> INFINITE_2I = ITEMS.register("infinite_2i_cell",
            () -> new InfiniteCellItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE)
                    .component(DataComponents.CUSTOM_DATA, CustomData.of(kindTag(InfiniteKinds.TWO_I_KIND)))));

    private IITechnologyItems() {}

    private static CompoundTag kindTag(String kindId) {
        CompoundTag tag = new CompoundTag();
        tag.putString(InfiniteCellInventory.KIND_TAG, kindId);
        return tag;
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}