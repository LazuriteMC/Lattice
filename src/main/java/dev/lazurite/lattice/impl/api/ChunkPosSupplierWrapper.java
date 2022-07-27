package dev.lazurite.lattice.impl.api;

import dev.lazurite.lattice.api.supplier.ChunkPosSupplier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public interface ChunkPosSupplierWrapper extends ChunkPosSupplier {
    ChunkPosSupplier getChunkPosSupplier();
    ServerLevel getServerLevel();

    void setLastChunkPos(final ChunkPos chunkPos);
    ChunkPos getLastChunkPos();

    void setLastLastChunkPos(final ChunkPos chunkPos);
    ChunkPos getLastLastChunkPos();

    boolean isInSameChunk(final ServerPlayer serverPlayer);
    boolean wasInSameChunk(final ServerPlayer serverPlayer, final boolean useLastLast);
}
