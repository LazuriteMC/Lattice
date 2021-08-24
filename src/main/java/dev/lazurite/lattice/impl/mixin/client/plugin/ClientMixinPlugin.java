package dev.lazurite.lattice.impl.mixin.client.plugin;

import dev.lazurite.lattice.impl.util.plugin.ModCompatMixinPlugin;

public final class ClientMixinPlugin extends ModCompatMixinPlugin {

    @Override
    public void onLoad(String mixinPackage) {
        super.onLoad(mixinPackage);

        this.modCompatibilities.add(
                new ModCompatibility("sodium")

                // WorldRenderer
                .addIncompatibleMixin("dev.lazurite.lattice.impl.mixin.client.world_renderer.SetupTerrainMixin")

                // ClientChunkManager
                .addIncompatibleMixin("dev.lazurite.lattice.imple.mixin.client.ClientChunkMapMixin")
//                .addAdditionalMixin("client.compat.sodium.SodiumChunkManagerMixin")
        );
    }

}
