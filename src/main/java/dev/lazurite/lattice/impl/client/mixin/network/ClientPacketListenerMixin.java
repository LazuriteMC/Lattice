package dev.lazurite.lattice.impl.client.mixin.network;

import dev.lazurite.lattice.api.LatticePlayer;
import dev.lazurite.lattice.api.Viewable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Inject(
            method = "handleCustomPayload",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    public void handleCustomPayload(ClientboundCustomPayloadPacket clientboundCustomPayloadPacket, CallbackInfo ci) {
        if (clientboundCustomPayloadPacket.getIdentifier().getNamespace().equals("lattice")) { // I'll have a constant in the future
            if (clientboundCustomPayloadPacket.getIdentifier().getPath().equals("set_viewable")) { // only 1 packet so far
                final var friendlyByteBuf = clientboundCustomPayloadPacket.getData();

                final var player = Minecraft.getInstance().player;
                final var latticePlayer = (LatticePlayer) player;

                if (friendlyByteBuf.readBoolean()) { // can only be an entity (true in first friendlyByteBuf slot)
                    latticePlayer.setViewable((Viewable) player.getLevel().getEntity(friendlyByteBuf.readInt()));

                    clientboundCustomPayloadPacket.getData().release();
                    ci.cancel();
                }
            }
        }
    }

}
