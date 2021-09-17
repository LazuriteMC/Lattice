package dev.lazurite.lattice.camera.api;

/**
 * When implemented, allows for the object to render itself and/or the player.
 */
public interface Viewable {

    /**
     * Allows rendering of self.
     */
    boolean shouldRenderSelf();

    /**
     * Allows rendering of player.
     */
    boolean shouldRenderPlayer();
}
