package dev.lazurite.lattice.api.viewable;

import net.minecraft.world.phys.Vec3;

public interface Viewable {
    Vec3 getViewablePosition();
    Vec3 getViewablePosition(final float delta);

    default Vec3 getViewableViewPosition() {
        return this.getViewablePosition();
    }

    default Vec3 getViewableViewPosition(final float delta) {
        return this.getViewablePosition(delta);
    }

    double getViewableX();
    double getViewableY();
    double getViewableZ();

    float getViewableXRot();
    float getViewableXRot(final float delta);

    float getViewableYRot();
    float getViewableYRot(final float delta);

    default boolean shouldRenderPlayer() {
        return false;
    }

}
