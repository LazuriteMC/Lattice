package dev.lazurite.lattice.impl.mixin.client.plugin;

import dev.lazurite.lattice.impl.util.LazuriteMixinConfigPlugin;

public final class ClientMixinConfigPlugin extends LazuriteMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
        super.onLoad(mixinPackage);

        this.modCompatibilities.add(
                new ModCompatibility("imm_ptl_core")
        );

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
