package dev.lazurite.lattice.impl.client.mixin.network;

import dev.lazurite.lattice.api.LatticePlayer;
import dev.lazurite.lattice.api.Viewable;
import dev.lazurite.toolbox.api.network.PacketRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin to handle Lattice's clientbound packets.
 */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    static {
        PacketRegistry.registerClientbound(new ResourceLocation("lattice", "set_viewable"), clientboundContext -> {
            final var player = Minecraft.getInstance().player;

            if (clientboundContext.byteBuf().readBoolean()) { // TODO can only be an entity for now (true in first friendlyByteBuf slot)
                final var entity = (Viewable) player.getLevel().getEntity(clientboundContext.byteBuf().readVarInt());
                ((LatticePlayer) player).setViewable(entity == null ? (Viewable) player : entity);
            }
        });
    }

}
