package dev.lazurite.lattice.impl.common.duck;

import dev.lazurite.lattice.api.LatticePlayer;
import dev.lazurite.lattice.impl.common.mixin.chunk.ChunkMapMixin;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;

/**
 * Internal version of {@link LatticePlayer} implemented by {@link ServerPlayer}. Used entirely by {@link ChunkMapMixin}.
 */
public interface InternalLatticeServerPlayer extends LatticePlayer {
    void setLastViewableSectionPos(final SectionPos sectionPos);
    SectionPos getLastViewableSectionPos();

    void updateLastLastSectionPos();

    void updateLastLastViewableSectionPos();
    SectionPos getLastLastViewableSectionPos();

    boolean isViewableInSameChunk();

    /**
     * Only call after {@link ChunkMap#updatePlayerPos(ServerPlayer)}.
     */
    boolean wasViewableInSameChunk(final boolean useLastLast);
}
