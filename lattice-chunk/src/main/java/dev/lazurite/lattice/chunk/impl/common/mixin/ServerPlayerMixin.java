package dev.lazurite.lattice.chunk.impl.common.mixin;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;absMoveTo(DDDFF)V"
            )
    )
    public void absMoveTo(ServerPlayer serverPlayer, double d, double e, double f, float g, float h) { }

    @Redirect(
            method = "setCamera",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;teleportTo(DDD)V"
            )
    )
    public void teleportTo(ServerPlayer serverPlayer, double d, double e, double f) { }

}
