package dev.lazurite.lattice.impl.common.mixin;

import dev.lazurite.lattice.api.LatticePlayer;
import dev.lazurite.lattice.api.viewable.Viewable;
import dev.lazurite.lattice.impl.common.LatticeCommon;
import dev.lazurite.lattice.impl.common.network.LatticeServerGamePacketListener;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Shadow public abstract Entity getCamera();
    @Shadow public ServerGamePacketListenerImpl connection;

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

    @Inject(
            method = "setCamera",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;teleportTo(DDD)V"
            )
    )
    public void setCamera(Entity entity, CallbackInfo ci) {
        final var player = (LatticePlayer) this;

        player.setViewable((Viewable) this.getCamera());
        this.connection.send(LatticeServerGamePacketListener.createSetViewable((Viewable) this.getCamera()));
    }

}
