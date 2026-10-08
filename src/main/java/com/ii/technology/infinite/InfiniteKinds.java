package com.ii.technology.infinite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry of "infinite" cell kinds. Small and code-only — this mod has no KubeJS support, so kinds
 * are declared in Java ({@link #registerDefaults()}).
 */
public final class InfiniteKinds {
    /** Kind id stamped on the bundled infinite ii cell. */
    public static final String TWO_I_KIND = "iitechnology:2i_cell";

    /** One declared kind: an id, a display title, and the tokens served infinitely. */
    public record Kind(String id, String title, List<String> items) {}

    /** The 16 vanilla concrete colours, in the standard dye order. */
    private static final String[] CONCRETE_COLORS = {
            "white", "orange", "magenta", "light_blue", "yellow", "lime",
            "pink", "gray", "light_gray", "cyan", "purple", "blue",
            "brown", "green", "red", "black"
    };

    private static final Map<String, Kind> KINDS = new LinkedHashMap<>();

    private InfiniteKinds() {}

    public static synchronized void register(String id, String title, List<String> items) {
        KINDS.put(id, new Kind(id, title, List.copyOf(items)));
    }

    public static Kind get(String id) {
        return id == null ? null : KINDS.get(id);
    }

    public static synchronized Collection<Kind> all() {
        return List.copyOf(KINDS.values());
    }

    /** Declares the bundled cell: un-numbered 2i, sand, then the 16 concrete powders (in order). */
    public static synchronized void registerDefaults() {
        if (KINDS.containsKey(TWO_I_KIND)) return;
        List<String> items = new ArrayList<>();
        items.add(IITechnologyItemIds.TWO_I);
        items.add("minecraft:sand");
        for (String colour : CONCRETE_COLORS) {
            items.add("minecraft:" + colour + "_concrete_powder");
        }
        register(TWO_I_KIND, "无限ii元件", items);
    }
}