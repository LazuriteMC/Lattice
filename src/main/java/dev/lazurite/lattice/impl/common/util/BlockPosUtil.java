package dev.lazurite.lattice.impl.common.util;

import dev.lazurite.lattice.api.viewable.Viewable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public final class BlockPosUtil {

    private BlockPosUtil() { }

    public static int posToBlockCoord(double dCoord) {
        final var iCoord = (int) dCoord;
        return dCoord < (double) iCoord ? iCoord - 1 : iCoord;
    }

    public static BlockPos of(Entity entity) {
        return new BlockPos(
                BlockPosUtil.posToBlockCoord(entity.getX()),
                BlockPosUtil.posToBlockCoord(entity.getY()),
                BlockPosUtil.posToBlockCoord(entity.getZ())
        );
    }

    public static BlockPos of(Viewable viewable) {
        return new BlockPos(
                BlockPosUtil.posToBlockCoord(viewable.getViewableX()),
                BlockPosUtil.posToBlockCoord(viewable.getViewableY()),
                BlockPosUtil.posToBlockCoord(viewable.getViewableZ())
        );
    }

}
