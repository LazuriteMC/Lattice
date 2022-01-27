package dev.lazurite.lattice.api;

import net.minecraft.world.entity.player.Player;

/**
 * Represents a {@link Player} with methods for interacting with its {@link Viewable}.
 */
public interface LatticePlayer {
    void setViewable(final Viewable viewable);
    Viewable getViewable();
    void removeViewable();
    boolean isViewableSelf();
}
