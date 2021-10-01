package dev.lazurite.lattice.core.impl.common.util;

import dev.lazurite.lattice.core.api.Viewable;
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
                ChunkPosUtil.posToChunkCoord(entity.getY())
        );
    }

    public static ChunkPos of(Viewable viewable) {
        return new ChunkPos(
                ChunkPosUtil.posToChunkCoord(viewable.getX()),
                ChunkPosUtil.posToChunkCoord(viewable.getY())
        );
    }

}
