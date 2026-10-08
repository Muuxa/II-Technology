package com.ii.technology.client.mixin;

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;
import java.util.Set;

/**
 * Compatibility gate for the AE2 terminal {@code ∞} mixin.
 *
 * <p>AURELIUM 0.8.22 still ships its own {@code MEStorageScreenInfinityMixin} for the same AE2
 * calls. Two redirects on the same instruction make Mixin abort the second one, which breaks mod
 * construction. When AURELIUM is present we leave the rendering to AURELIUM and instead register
 * our cell kind into its infinite-key registry from the client initializer.</p>
 */
public final class IIClientMixinPlugin implements IMixinConfigPlugin {
    private static final String AURELIUM_CLIENT_KEYS =
            "com/muuxa/aurelium/client/ClientInfiniteKeys.class";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return getClass().getClassLoader().getResource(AURELIUM_CLIENT_KEYS) == null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String target, ClassNode node, String mixin, IMixinInfo info) {
    }

    @Override
    public void postApply(String target, ClassNode node, String mixin, IMixinInfo info) {
    }
}
