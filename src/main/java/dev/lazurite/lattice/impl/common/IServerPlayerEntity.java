package dev.lazurite.lattice.impl.common;

import net.minecraft.util.math.ChunkSectionPos;

public interface IServerPlayerEntity {
    void setPrevCameraChunkSectionPos(ChunkSectionPos prevCameraChunkSectionPos);
    ChunkSectionPos getPrevCameraChunkSectionPos();
}
