package dev.lazurite.lattice.api;

import net.minecraft.world.phys.Vec3;

/**
 * Represents a position and rotation that can load chunks and be viewed.
 */
public interface Viewable {
    Vec3 getPosition();

    default double getX() {
        return this.getPosition().x();
    }

    default double getY() {
        return this.getPosition().y();
    }

    default double getZ() {
        return this.getPosition().z();
    }

    float getXRot();
    float getYRot();

    default boolean shouldRenderPlayer() {
        return false;
    }
}
