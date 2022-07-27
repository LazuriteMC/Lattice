package dev.lazurite.lattice.impl;

import dev.lazurite.lattice.api.supplier.ChunkPosSupplier;
import dev.lazurite.lattice.impl.api.ChunkPosSupplierWrapper;
import dev.lazurite.lattice.impl.api.player.InternalLatticeServerPlayer;
import dev.lazurite.lattice.impl.mixin.access.IChunkMapMixin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;

public final class ChunkPosSupplierWrapperImpl implements ChunkPosSupplierWrapper {

    private final ChunkPosSupplier chunkPosSupplier;
    private final ServerLevel serverLevel;

    private ChunkPos lastChunkPos;
    private ChunkPos lastLastChunkPos;

    public ChunkPosSupplierWrapperImpl(final ChunkPosSupplier chunkPosSupplier, final ServerLevel serverLevel) {
        this.chunkPosSupplier = chunkPosSupplier;
        this.serverLevel = serverLevel;
    }

    @Override
    public ChunkPos getChunkPos() {
        return this.getChunkPosSupplier().getChunkPos();
    }

    // TODO
    @Override
    public int getDistance() {
        return Mth.clamp(this.getChunkPosSupplier().getDistance(), 0, ((IChunkMapMixin) this.getServerLevel().getChunkSource().chunkMap).getViewDistance());
    }

    @Override
    public ChunkPosSupplier getChunkPosSupplier() {
        return this.chunkPosSupplier;
    }

    @Override
    public ServerLevel getServerLevel() {
        return this.serverLevel;
    }

    @Override
    public void setLastChunkPos(final ChunkPos lastChunkPos) {
        this.lastChunkPos = lastChunkPos;
    }

    @Override
    public ChunkPos getLastChunkPos() {
        return this.lastChunkPos;
    }

    @Override
    public void setLastLastChunkPos(final ChunkPos lastLastChunkPos) {
        this.lastLastChunkPos = lastLastChunkPos;
    }

    @Override
    public ChunkPos getLastLastChunkPos() {
        return this.lastLastChunkPos;
    }

    @Override
    public boolean isInSameChunk(final ServerPlayer serverPlayer) {
        return this.getChunkPosSupplier().getChunkPos().equals(serverPlayer.chunkPosition());
    }

    @Override
    public boolean wasInSameChunk(final ServerPlayer serverPlayer, final boolean useLastLast) {
        if (useLastLast) {
            final var lastLastChunkPos = this.getLastLastChunkPos();
            final var serverPlayerLastLastChunkPos = ((InternalLatticeServerPlayer) serverPlayer).getLastLastChunkPos();

            if (lastLastChunkPos == null || serverPlayerLastLastChunkPos == null) {
                return true;
            }

            return lastLastChunkPos.equals(serverPlayerLastLastChunkPos);
        }

        return this.getLastChunkPos().equals(serverPlayer.getLastSectionPos().chunk());
    }

    @Override
    public int hashCode() {
        return this.getChunkPosSupplier().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ChunkPosSupplier chunkPosSupplier) {
            if (chunkPosSupplier instanceof ChunkPosSupplierWrapper chunkPosSupplierWrapper) {
                return this.getChunkPosSupplier().equals(chunkPosSupplierWrapper.getChunkPosSupplier());
            }

            return this.getChunkPosSupplier().equals(chunkPosSupplier);
        }

        return false;
    }

}
