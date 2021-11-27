package dev.lazurite.lattice.impl.common.mixin.network;

import dev.lazurite.lattice.impl.common.network.LatticeServerGamePacketListener;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow public ServerPlayer player;

    @Inject(
            method = "handleCustomPayload",
            at = @At("HEAD"),
            cancellable = true
    )
    public void handleCustomPayload(ServerboundCustomPayloadPacket serverboundCustomPayloadPacket, CallbackInfo ci) {
        // handleCustomPayload is a no-op by default so this isn't present
        PacketUtils.ensureRunningOnSameThread(serverboundCustomPayloadPacket, (ServerGamePacketListenerImpl) (Object) this, this.player.getLevel());

        if (LatticeServerGamePacketListener.handle(serverboundCustomPayloadPacket)) {
            serverboundCustomPayloadPacket.getData().release();
            ci.cancel();
        }
    }

}
