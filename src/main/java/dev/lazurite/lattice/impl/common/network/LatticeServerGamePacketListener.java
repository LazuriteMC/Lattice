package dev.lazurite.lattice.impl.common.network;

import dev.lazurite.lattice.api.viewable.Viewable;
import dev.lazurite.lattice.impl.common.LatticeCommon;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public final class LatticeServerGamePacketListener {

    public static boolean handle(ServerboundCustomPayloadPacket serverboundCustomPayloadPacket) {
        if (serverboundCustomPayloadPacket.getIdentifier().getNamespace().equals(LatticeCommon.MOD_ID)) {
            return switch (serverboundCustomPayloadPacket.getIdentifier().getPath()) {
                case "set_viewable" -> handleSetViewable(serverboundCustomPayloadPacket);
                case "another_case" -> handleAnotherCase(serverboundCustomPayloadPacket);
                default -> false;
            };
        } else {
            return false;
        }
    }

    private static ResourceLocation createIdentifier(String path) {
        return new ResourceLocation(LatticeCommon.MOD_ID, path);
    }

    // TODO This is the wrong class for this
    public static ClientboundCustomPayloadPacket createSetViewable(Viewable viewable) {
        final var friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());

        friendlyByteBuf.writeInt(((Entity) viewable).getId());

        return new ClientboundCustomPayloadPacket(createIdentifier("set_viewable"), friendlyByteBuf);
    }

    private static boolean handleSetViewable(ServerboundCustomPayloadPacket serverboundCustomPayloadPacket) {

        return true;
    }

    private static boolean handleAnotherCase(ServerboundCustomPayloadPacket serverboundCustomPayloadPacket) {

        return true;
    }

    private LatticeServerGamePacketListener() { }

}
