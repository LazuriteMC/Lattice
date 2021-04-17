package dev.lazurite.lattice.impl.mixin.client.plugin;

import dev.lazurite.lattice.impl.util.plugin.CompatMixinPlugin;

public final class ClientMixinPlugin extends CompatMixinPlugin {

    @Override
    public void onLoad(String mixinPackage) {
        super.onLoad(mixinPackage);

        this.modCompatibilities.add(
                new ModCompatibility("sodium")

                // WorldRenderer
                .addIncompatibleMixin("dev.lazurite.lattice.impl.mixin.client.WorldRendererMixin")
                .addAdditionalMixin("client.compat.sodium.WorldRendererMixin")

                // ClientChunkManager
                .addIncompatibleMixin("dev.lazurite.lattice.imple.mixin.client.ClientChunkMapMixin")
                .addAdditionalMixin("client.compat.sodium.SodiumChunkManagerMixin")
        );
    }

}
