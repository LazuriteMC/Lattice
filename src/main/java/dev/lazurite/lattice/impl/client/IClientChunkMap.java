package dev.lazurite.lattice.impl.client;

import net.minecraft.world.chunk.WorldChunk;

public interface IClientChunkMap {
    void setPlayerChunk(WorldChunk playerChunk);
    WorldChunk getPlayerChunk();
}
