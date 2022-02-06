package dev.lazurite.lattice.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * Represents a {@link Player} with methods for interacting with its {@link Viewable}.
 */
public interface LatticePlayer {
    void setViewable(final Viewable viewable);
    Viewable getViewable();
    void removeViewable();
    boolean isViewableSelf();

    /**
     * Set's the {@link ServerPlayer#camera} to {@code entity}.
     * Doesn't prefer {@code entity}'s {@link Viewable}.
     */
    void setCameraWithoutViewable(final Entity entity);
}
