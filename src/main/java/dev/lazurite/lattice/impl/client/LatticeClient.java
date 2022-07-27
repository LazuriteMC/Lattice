package dev.lazurite.lattice.impl.client;

import dev.lazurite.lattice.api.point.ViewPoint;
import dev.lazurite.lattice.impl.Networking;
import dev.lazurite.lattice.impl.api.player.InternalLatticeLocalPlayer;
import dev.lazurite.toolbox.api.event.ClientEvents;
import dev.lazurite.toolbox.api.network.PacketRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public final class LatticeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // region Events

        ClientEvents.Player.ADD.register(((abstractClientPlayer, isLocalPlayer) -> {
            if (isLocalPlayer) {
                ((InternalLatticeLocalPlayer) abstractClientPlayer).setViewPointEntityId(abstractClientPlayer.getId());
            }
        }));

        ClientEvents.Tick.START_LEVEL_TICK.register(clientLevel -> {
            final var localPlayer = Minecraft.getInstance().player;
            final var internalLatticeLocalPlayer = (InternalLatticeLocalPlayer) localPlayer;

            final var localPlayerId = localPlayer.getId();
            final var viewPointEntityId = internalLatticeLocalPlayer.getViewPointEntityId();

            if (viewPointEntityId != localPlayerId) {
                final var viewPoint = internalLatticeLocalPlayer.getViewPoint();

                if (viewPoint instanceof Entity entity) {
                    if (viewPointEntityId != entity.getId()) {
                        final var viewPointEntity = clientLevel.getEntity(viewPointEntityId);

                        if (viewPointEntity != null) {
                            Minecraft.getInstance().setCameraEntity(viewPointEntity);
                        }
                    }
                } else {
                    internalLatticeLocalPlayer.setViewPointEntityId(localPlayerId);
                }
            }
        });

        // endregion events

        // region networking

        PacketRegistry.registerClientbound(Networking.SET_VIEWPOINT_PACKET_IDENTIFIER, clientboundContext -> {
            final var localPlayer = Minecraft.getInstance().player;
            final var internalLatticeLocalPlayer = (InternalLatticeLocalPlayer) localPlayer;

            // isEntity
            if (clientboundContext.byteBuf().readBoolean()) {
                final var clientLevel = localPlayer.getLevel();

                final var entityId = clientboundContext.byteBuf().readVarInt();
                internalLatticeLocalPlayer.setViewPointEntityId(entityId);

                final var entity = clientLevel.getEntity(entityId);

                if (entity != null) {
                    internalLatticeLocalPlayer.setViewPoint((ViewPoint) entity);
                }
            } else {
                // TODO handle non-entity viewPoints
                internalLatticeLocalPlayer.setViewPointEntityId(localPlayer.getId());
            }
        });

        // endregion networking

    }

}
