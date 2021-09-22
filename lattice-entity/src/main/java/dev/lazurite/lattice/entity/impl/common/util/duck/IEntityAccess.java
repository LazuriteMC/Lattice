package dev.lazurite.lattice.entity.impl.common.util.duck;

import org.joml.Vector3d;

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
