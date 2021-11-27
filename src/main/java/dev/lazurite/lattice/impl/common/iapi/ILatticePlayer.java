package dev.lazurite.lattice.impl.common.iapi;

import dev.lazurite.lattice.api.LatticePlayer;
import net.minecraft.core.SectionPos;

public interface ILatticePlayer extends LatticePlayer {
    void setLastViewableSectionPos(SectionPos sectionPos);
    SectionPos getLastViewableSectionPos();
}
