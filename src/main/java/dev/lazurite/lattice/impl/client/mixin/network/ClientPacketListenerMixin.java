package dev.lazurite.lattice.impl.client.mixin.network;

import dev.lazurite.lattice.api.Viewable;
import dev.lazurite.lattice.impl.client.duck.InternalLatticeLocalPlayer;
import dev.lazurite.toolbox.api.event.ClientEvents;
import dev.lazurite.toolbox.api.network.PacketRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin to handle Lattice's clientbound packets.
 */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    static {
        ClientEvents.Tick.START_LEVEL_TICK.register(clientLevel -> {
            final var player = Minecraft.getInstance().player;
            final var latticePlayer = (InternalLatticeLocalPlayer) player;

            if (((Entity) latticePlayer.getViewable()).getId() != latticePlayer.getViewableEntityId()) { // TODO
                final var entity = player.getLevel().getEntity(latticePlayer.getViewableEntityId());

                if (entity != null) {
                    Minecraft.getInstance().setCameraEntity(entity);
                    latticePlayer.setViewable((Viewable) entity);
                }
            }
        });

        PacketRegistry.registerClientbound(new ResourceLocation("lattice", "set_viewable"), clientboundContext -> {
            final var player = Minecraft.getInstance().player;
            final var latticePlayer = (InternalLatticeLocalPlayer) player;

            if (clientboundContext.byteBuf().readBoolean()) { // TODO can only be an entity for now (true in first friendlyByteBuf slot)
                final var entityId = clientboundContext.byteBuf().readVarInt();
                latticePlayer.setViewableEntityId(entityId);

                final var entity = player.getLevel().getEntity(entityId);

                if (entity != null) {
                    latticePlayer.setViewable((Viewable) entity);
                }
            }
        });
    }

}
