package dev.lazurite.lattice.impl;

import dev.lazurite.lattice.api.point.ViewPoint;
import dev.lazurite.toolbox.api.network.ServerNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class Networking {

    public static final ResourceLocation SET_VIEWPOINT_PACKET_IDENTIFIER = new ResourceLocation("lattice", "set_viewable");

    public static void sendSetViewPointPacket(final ServerPlayer serverPlayer, final ViewPoint viewPoint) {
        ServerNetworking.send(serverPlayer, Networking.SET_VIEWPOINT_PACKET_IDENTIFIER, friendlyByteBuf -> {
            final var isEntity = viewPoint instanceof Entity;
            friendlyByteBuf.writeBoolean(isEntity);

            if (isEntity) {
                friendlyByteBuf.writeVarInt(((Entity) viewPoint).getId());
            }

            // TODO handle non-entity viewPoints
        });
    }

    private Networking() { }

}
