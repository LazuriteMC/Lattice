package dev.lazurite.lattice.impl.util;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.*;

public class LazuriteMixinConfigPlugin implements IMixinConfigPlugin {

    protected List<ModCompatibility> modCompatibilities;

    @Override
    public void onLoad(String mixinPackage) {
        this.modCompatibilities = new ArrayList<>();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }


    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        for (ModCompatibility modCompatibility : this.modCompatibilities) {
            if (modCompatibility.isPresent() && modCompatibility.isIncompatibleMixin(mixinClassName)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        List<String> mixins = new ArrayList<>();

        this.modCompatibilities.forEach(
                modCompatibility -> {
                    if (modCompatibility.isPresent()) {
                        mixins.addAll(modCompatibility.getAdditionalMixins());
                    }
                }
        );

        return mixins;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    protected static class ModCompatibility {
        private final String id;

        private final Set<String> incompatibleMixins;
        private final Set<String> additionalMixins;

        public ModCompatibility(String id) {
            this.id = id;

            this.incompatibleMixins = new HashSet<>();
            this.additionalMixins = new HashSet<>();
        }

        public ModCompatibility addIncompatibleMixin(String mixin) {
            this.incompatibleMixins.add(mixin);
            return this;
        }

        public ModCompatibility addAdditionalMixin(String additionalMixin) {
            this.additionalMixins.add(additionalMixin);
            return this;
        }

        public boolean isPresent() {
            return FabricLoader.getInstance().isModLoaded(this.id);
        }

        public boolean isIncompatibleMixin(String mixin) {
            return this.incompatibleMixins.contains(mixin);
        }

        public Set<String> getIncompatibleMixins() {
            return this.incompatibleMixins;
        }

        public Set<String> getAdditionalMixins() {
            return this.additionalMixins;
        }

    }

}
