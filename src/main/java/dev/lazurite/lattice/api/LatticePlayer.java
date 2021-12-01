package dev.lazurite.lattice.api;

public interface LatticePlayer {
    void setViewable(Viewable viewable);
    Viewable getViewable();
    boolean isViewableSelf();
    void removeViewable();
}
