package dev.lazurite.lattice.api.viewable;

import net.minecraft.world.phys.Vec3;

public interface ControllableViewable extends Viewable {
    void setViewablePosition(Vec3 position);
    void setViewableXRot(float xRot);
    void setViewableYRot(float yRot);
}
