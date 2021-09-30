package dev.lazurite.lattice.camera.impl.iapi;

import dev.lazurite.lattice.core.api.Viewable;

import java.util.List;

public interface ViewableContainer {
    void addViewable(Viewable viewable);
    List<Viewable> getViewables();
}
