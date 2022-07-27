package dev.lazurite.lattice.impl.api.player;

import dev.lazurite.lattice.api.player.LatticeServerPlayer;
import dev.lazurite.lattice.impl.api.ChunkPosSupplierWrapper;
import net.minecraft.world.level.ChunkPos;

public interface InternalLatticeServerPlayer extends LatticeServerPlayer {
    ChunkPosSupplierWrapper getChunkPosSupplierWrapper();

    void setLastLastChunkPos(final ChunkPos chunkPos);
    ChunkPos getLastLastChunkPos();
}
