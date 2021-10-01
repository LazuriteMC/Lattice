package dev.lazurite.lattice.core.impl.common.util;

import dev.lazurite.lattice.core.api.Viewable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public final class BlockPosUtil {

    private BlockPosUtil() { }

    public static int posToBlockCoord(double dCoord) {
        final var iCoord = (int) dCoord;
        return iCoord < dCoord ? iCoord : iCoord - 1;
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
                BlockPosUtil.posToBlockCoord(viewable.getX()),
                BlockPosUtil.posToBlockCoord(viewable.getY()),
                BlockPosUtil.posToBlockCoord(viewable.getZ())
        );
    }

}
