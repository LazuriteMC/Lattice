package dev.lazurite.lattice.impl.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow @Nullable public Entity cameraEntity;

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getBlockX()I"
            )
    )
    public int tick_getBlockX(ClientPlayerEntity player) {
        return this.cameraEntity.getBlockX();
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getBlockY()I"
            )
    )
    public int tick_getBlockY(ClientPlayerEntity player) {
        return this.cameraEntity.getBlockY();
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getBlockZ()I"
            )
    )
    public int tick_getBlockZ(ClientPlayerEntity player) {
        return this.cameraEntity.getBlockZ();
    }

}
