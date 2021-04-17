package dev.lazurite.lattice.impl.util.duck;

import net.minecraft.util.math.ChunkSectionPos;

public interface IServerPlayerEntity {
    void setPrevCamPos(ChunkSectionPos prevCamPos);
    ChunkSectionPos getPrevCamPos();
}
