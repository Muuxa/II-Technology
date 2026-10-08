package com.ii.technology.infinite;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * AE2 storage cell that makes a declared set of items infinitely available.
 *
 * <ul>
 *   <li><b>Extract</b> of a marked item always succeeds and never depletes.</li>
 *   <li><b>Insert</b> of a marked item is accepted then voided (items are destroyed on input).</li>
 *   <li>Unmarked items are neither stored nor served.</li>
 * </ul>
 *
 * <p>The configured kind is read from the cell's {@code CustomData} tag, so a single item id can
 * back many differently-configured infinite cells.</p>
 */
public final class InfiniteCellInventory implements StorageCell {
    /** NBT key holding the kind id, stored in the cell's CustomData. */
    public static final String KIND_TAG = "iitechnology_infinite";

    /**
     * Reported available amount per key. Deliberately NOT {@link Long#MAX_VALUE}: AE2 sums reported
     * amounts with a plain {@code long +}, so two cells reporting MAX would overflow negative. This
     * value is large enough to read as "infinite" in the UI yet safe to stack (~64 cells to MAX).
     */
    public static final long REPORTED_AMOUNT = Long.MAX_VALUE / 64;

    private final ItemStack stack;
    @SuppressWarnings("unused")
    private final ISaveProvider saveProvider;

    InfiniteCellInventory(ItemStack stack, ISaveProvider saveProvider) {
        this.stack = stack;
        this.saveProvider = saveProvider;
    }

    /** The declared kind id stored on this cell, or {@code null} when unset. */
    public static String kindId(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        CompoundTag tag = data(stack);
        return tag.contains(KIND_TAG) ? tag.getString(KIND_TAG) : null;
    }

    public static void setKindId(ItemStack stack, String kindId) {
        CompoundTag tag = data(stack);
        tag.putString(KIND_TAG, kindId);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static CompoundTag data(ItemStack stack) {
        CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        return custom == null ? new CompoundTag() : custom.copyTag();
    }

    /** The declaration tokens this cell serves (item ids), or empty when unset/unknown. */
    public Set<String> itemTokens() {
        InfiniteKinds.Kind kind = InfiniteKinds.get(kindId(stack));
        return kind == null ? Set.of() : new LinkedHashSet<>(kind.items());
    }

    /** Resolved live keys this cell serves infinitely. */
    public List<AEKey> infiniteKeys() {
        List<AEKey> keys = new ArrayList<>();
        for (String token : itemTokens()) {
            ResourceLocation loc = ResourceLocation.tryParse(token);
            if (loc != null && BuiltInRegistries.ITEM.containsKey(loc)) {
                keys.add(AEItemKey.of(BuiltInRegistries.ITEM.get(loc)));
            }
        }
        return keys;
    }

    private boolean isInfinite(AEKey key) {
        if (key == null) return false;
        for (AEKey served : infiniteKeys()) {
            if (served.equals(key)) return true;
        }
        return false;
    }

    /** Public query: is {@code key} one this cell serves infinitely? */
    public boolean servesInfinitely(AEKey key) {
        return isInfinite(key);
    }

    @Override
    public CellState getStatus() {
        return itemTokens().isEmpty() ? CellState.EMPTY : CellState.NOT_EMPTY;
    }

    @Override
    public double getIdleDrain() { return 0.0; }

    @Override
    public boolean canFitInsideCell() { return false; }

    @Override
    public void persist() { /* nothing to persist; the cell holds no real contents */ }

    @Override
    public long insert(AEKey key, long amount, Actionable mode, IActionSource source) {
        if (amount <= 0L || !isInfinite(key)) return 0L;
        return amount; // accept then void: marked items are destroyed on input
    }

    @Override
    public long extract(AEKey key, long amount, Actionable mode, IActionSource source) {
        if (amount <= 0L || !isInfinite(key)) return 0L;
        return amount; // never depletes
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        for (AEKey key : infiniteKeys()) {
            out.add(key, REPORTED_AMOUNT);
        }
    }

    @Override
    public Component getDescription() {
        InfiniteKinds.Kind kind = InfiniteKinds.get(kindId(stack));
        if (kind == null || kind.title() == null || kind.title().isBlank()) return stack.getHoverName();
        return Component.literal(kind.title());
    }
}