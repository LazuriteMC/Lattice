package dev.lazurite.lattice.impl.common.util;

import dev.lazurite.lattice.api.viewable.Viewable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

public final class ChunkPosUtil {

    private ChunkPosUtil() { }

    public static int posToChunkCoord(double coord) {
        return ChunkPosUtil.blockToChunkCoord(BlockPosUtil.posToBlockCoord(coord));
    }

    public static int blockToChunkCoord(int coord) {
        return coord >> 4;
    }

    public static ChunkPos of(Entity entity) {
        return new ChunkPos(
                ChunkPosUtil.posToChunkCoord(entity.getX()),
                ChunkPosUtil.posToChunkCoord(entity.getZ())
        );
    }

    public static ChunkPos of(Viewable viewable) {
        return new ChunkPos(
                ChunkPosUtil.posToChunkCoord(viewable.getViewableX()),
                ChunkPosUtil.posToChunkCoord(viewable.getViewableZ())
        );
    }

}
