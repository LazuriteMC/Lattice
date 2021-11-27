package dev.lazurite.lattice.impl.common.util;

import dev.lazurite.lattice.api.viewable.Viewable;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;

public final class SectionPosUtil {

    private SectionPosUtil() { }

    public static int posToSectionCoord(double coord) {
        return SectionPosUtil.blockToSectionCoord(BlockPosUtil.posToBlockCoord(coord));
    }

    public static int blockToSectionCoord(int coord) {
        return coord >> 4;
    }

    public static SectionPos of(Entity entity) {
        return SectionPos.of(
                SectionPosUtil.posToSectionCoord(entity.getX()),
                SectionPosUtil.posToSectionCoord(entity.getY()),
                SectionPosUtil.posToSectionCoord(entity.getZ())
        );
    }

    public static SectionPos of(Viewable viewable) {
        return SectionPos.of(
                SectionPosUtil.posToSectionCoord(viewable.getViewableX()),
                SectionPosUtil.posToSectionCoord(viewable.getViewableY()),
                SectionPosUtil.posToSectionCoord(viewable.getViewableZ())
        );
    }

}
