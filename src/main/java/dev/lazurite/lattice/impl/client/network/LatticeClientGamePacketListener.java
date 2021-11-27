package dev.lazurite.lattice.impl.client.network;

import dev.lazurite.lattice.api.LatticePlayer;
import dev.lazurite.lattice.api.viewable.Viewable;
import dev.lazurite.lattice.impl.common.LatticeCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;

public final class LatticeClientGamePacketListener {

    public static boolean handle(ClientboundCustomPayloadPacket clientboundCustomPayloadPacket) {
        if (clientboundCustomPayloadPacket.getIdentifier().getNamespace().equals(LatticeCommon.MOD_ID)) {
            return switch (clientboundCustomPayloadPacket.getIdentifier().getPath()) {
                case "set_viewable" -> handleSetViewable(clientboundCustomPayloadPacket);
                case "another_case" -> handleAnotherCase(clientboundCustomPayloadPacket);
                default -> false;
            };
        } else {
            return false;
        }
    }

    private static boolean handleSetViewable(ClientboundCustomPayloadPacket clientboundCustomPayloadPacket) {
        final var minecraft = Minecraft.getInstance();

        final var viewableId = clientboundCustomPayloadPacket.getData().readInt();

        ((LatticePlayer) minecraft.player).setViewable((Viewable) minecraft.level.getEntity(viewableId));

        System.out.println("Set viewable on client");

        return true;
    }

    private static boolean handleAnotherCase(ClientboundCustomPayloadPacket clientboundCustomPayloadPacket) {

        return true;
    }

    private LatticeClientGamePacketListener() { }

}
