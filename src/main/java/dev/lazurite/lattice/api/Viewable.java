package dev.lazurite.lattice.api;

import net.minecraft.world.phys.Vec3;

public interface Viewable {
    Vec3 getViewablePosition();

    double getViewableX();
    double getViewableY();
    double getViewableZ();

    float getViewableXRot();
    float getViewableYRot();
}
