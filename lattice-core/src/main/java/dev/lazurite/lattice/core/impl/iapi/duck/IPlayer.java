package dev.lazurite.lattice.core.impl.iapi.duck;

import dev.lazurite.lattice.core.api.Viewable;
import net.minecraft.core.SectionPos;

public interface IPlayer {
    void setViewable(Viewable viewable);
    Viewable getViewable();

    void setLastViewableSectionPos(SectionPos sectionPos);
    SectionPos getLastViewableSectionPos();
}
