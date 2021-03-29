package dev.lazurite.lattice.impl.mixin.common;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Redirect(
            method = "sendToAround",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getX()D"
            )
    )
    public double sendToAround_getX(ServerPlayerEntity serverPlayerEntity) {
        return serverPlayerEntity.getCameraEntity().getX();
    }

    @Redirect(
            method = "sendToAround",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getY()D"
            )
    )
    public double sendToAround_getY(ServerPlayerEntity serverPlayerEntity) {
        return serverPlayerEntity.getCameraEntity().getY();
    }

    @Redirect(
            method = "sendToAround",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getZ()D"
            )
    )
    public double sendToAround_getZ(ServerPlayerEntity serverPlayerEntity) {
        return serverPlayerEntity.getCameraEntity().getZ();
    }

}
