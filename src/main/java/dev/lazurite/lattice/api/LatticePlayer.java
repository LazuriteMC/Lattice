package dev.lazurite.lattice.api;

import dev.lazurite.lattice.api.viewable.Viewable;

public interface LatticePlayer {
    void setViewable(Viewable viewable);
    Viewable getViewable();
    boolean isViewableSelf();
    void removeViewable();
}
