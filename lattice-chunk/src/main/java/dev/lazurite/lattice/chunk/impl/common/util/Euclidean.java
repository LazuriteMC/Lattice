package dev.lazurite.lattice.chunk.impl.common.util;

import dev.lazurite.lattice.core.api.Viewable;
import dev.lazurite.lattice.core.impl.iapi.duck.IPlayer;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;

public final class Euclidean {

    private Euclidean() { }

    public static double euclideanDistanceSquared(ChunkPos chunkPos, Viewable viewable) {
        final var x = SectionPos.sectionToBlockCoord(chunkPos.x, 8) - viewable.getX();
        final var z = SectionPos.sectionToBlockCoord(chunkPos.z, 8) - viewable.getZ();

        return x * x + z * z;
    }

    public static boolean isChunkInEuclideanRange(ChunkPos chunkPos, ServerPlayer serverPlayer, boolean bl, int i) {
        final var player = ((IPlayer) serverPlayer);

        if (bl) {
            final var viewableSectionPos = player.getLastViewableSectionPos();
            return Euclidean.isChunkInEuclideanRange(chunkPos, viewableSectionPos.x(), viewableSectionPos.z(), i);
        } else {
            final var viewable = player.getViewable();
            return Euclidean.isChunkInEuclideanRange(chunkPos, SectionPos.blockToSectionCoord(Mth.floor(viewable.getX())), SectionPos.blockToSectionCoord(Mth.floor(viewable.getZ())), i);
        }
    }

    public static boolean isChunkInEuclideanRange(ChunkPos chunkPos, int i, int j, int k) {
        return Euclidean.isChunkInEuclideanRange(chunkPos.x, chunkPos.z, i, j, k);
    }

    public static boolean isChunkInEuclideanRange(int i, int j, int k, int l, int m) {
        int n = i - k;
        int o = j - l;
        return n * n + o * o <= m * m * m;
    }

}
