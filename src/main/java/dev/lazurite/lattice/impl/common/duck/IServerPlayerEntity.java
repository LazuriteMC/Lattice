package dev.lazurite.lattice.impl.common.duck;

import net.minecraft.util.math.ChunkSectionPos;

public interface IServerPlayerEntity {
    void setPrevCamPos(ChunkSectionPos prevCamPos);
    ChunkSectionPos getPrevCamPos();
}