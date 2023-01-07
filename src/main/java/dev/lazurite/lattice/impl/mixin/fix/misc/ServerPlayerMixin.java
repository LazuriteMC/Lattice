package dev.lazurite.lattice.impl.mixin.fix.misc;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    /**
     * Cancels {@link ServerPlayer#absMoveTo(double, double, double, float, float)} in {@link ServerPlayer#tick()}.
     */
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;absMoveTo(DDDFF)V"
            )
    )
    public void absMoveTo(ServerPlayer serverPlayer, double d, double e, double f, float g, float h) { }

    /**
     * Cancels {@link ServerPlayer#teleportTo(double, double, double)} in {@link ServerPlayer#setCamera(Entity)}.
     */
    @Redirect(
            method = "setCamera",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;teleport(DDDFF)V"
            )
    )
    public void teleportTo(ServerGamePacketListenerImpl instance, double d, double e, double f, float g, float h) { }

}
