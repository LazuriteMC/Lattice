package dev.lazurite.lattice.api;

import net.minecraft.util.math.Vec3d;

/**
 * When implemented, allows for the object to load chunks.
 */
public interface Positionable {

    /**
     * @return The position that chunk generation will occur.
     */
    Vec3d getPos();
}
