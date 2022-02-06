package dev.lazurite.lattice.api;

import net.minecraft.world.phys.Vec3;

/**
 * Represents a position and rotation that can load chunks and be viewed.
 */
public interface Viewable {
    default Vec3 getPosition() {
        throw new AbstractMethodError();
    }

    default double getX() {
        return this.getPosition().x();
    }

    default double getY() {
        return this.getPosition().y();
    }

    default double getZ() {
        return this.getPosition().z();
    }

    default float getXRot() {
        throw new AbstractMethodError();
    }

    default float getYRot() {
        throw new AbstractMethodError();
    }

    default boolean shouldRenderPlayer() {
        return false;
    }
}
