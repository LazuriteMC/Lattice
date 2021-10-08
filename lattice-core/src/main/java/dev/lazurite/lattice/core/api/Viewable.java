package dev.lazurite.lattice.core.api;

import net.minecraft.world.phys.Vec3;

public interface Viewable {
    Vec3 latGetPosition();
    Vec3 latGetPosition(final float delta);
    Vec3 latGetPosition(final double delta);

    double latGetX();
    double latGetX(final float delta);
    double latGetX(final double delta);

    double latGetY();
    double latGetY(final float delta);
    double latGetY(final double delta);

    double latGetZ();
    double latGetZ(final float delta);
    double latGetZ(final double delta);

    float latGetPitch();
    float latGetPitch(final float delta);

    float latGetYaw();
    float latGetYaw(final float delta);

    boolean shouldRenderPlayer();
}
