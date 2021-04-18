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

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkX:I"
            )
    )
    private int setupTerrain_chunkX(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkX;
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkY:I"
            )
    )
    private int setupTerrain_chunkY(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkY;
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkZ:I"
            )
    )
    private int setupTerrain_chunkZ(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkZ;
    }

}
