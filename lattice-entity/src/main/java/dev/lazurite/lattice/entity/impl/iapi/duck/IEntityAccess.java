package dev.lazurite.lattice.entity.impl.iapi.duck;

import org.joml.Vector3d;

/**
 * Used to access data from {@link net.minecraft.world.entity.Entity} internally.
 */
public interface IEntityAccess {
    Vector3d getPosition();
    Vector3d getPreviousPosition();

    double getX();
    double getPreviousX();

    double getY();
    double getPreviousY();

    double getZ();
    double getPreviousZ();

    float getXRotation();
    float getPreviousXRotation();
}
