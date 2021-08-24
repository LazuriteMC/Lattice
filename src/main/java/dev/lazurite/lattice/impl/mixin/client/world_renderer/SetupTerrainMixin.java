package dev.lazurite.lattice.impl.mixin.client.world_renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class SetupTerrainMixin {

    @Shadow @Final private MinecraftClient client;

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D"
            )
    )
    private double setupTerrain_getX(ClientPlayerEntity player) {
        return this.client.getCameraEntity().getX();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getY()D"
            )
    )
    private double setupTerrain_getY(ClientPlayerEntity player) {
        return this.client.getCameraEntity().getY();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D"
            )
    )
    private double setupTerrain_getZ(ClientPlayerEntity player) {
        return this.client.getCameraEntity().getZ();
    }

    /*
    // for production environment
    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/class_746;field_6024:I"
            ),
            require = 0
    )
    private int setupTerrain_chunkX_prod(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkX;
    }

    // for production environment
    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/class_746;field_5959:I"
            ),
            require = 0
    )
    private int setupTerrain_chunkY_prod(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkY;
    }

    // for production environment
    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/class_746;field_5980:I"
            ),
            require = 0
    )
    private int setupTerrain_chunkZ_prod(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkZ;
    }

    // for development environment
    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkX:I"
            ),
            require = 0
    )
    private int setupTerrain_chunkX_dev(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkX;
    }

    // for development environment
    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkY:I"
            ),
            require = 0
    )
    private int setupTerrain_chunkY_dev(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkY;
    }

    // for development environment
    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkZ:I"
            ),
            require = 0
    )
    private int setupTerrain_chunkZ_dev(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkZ;
    }
    */
}
