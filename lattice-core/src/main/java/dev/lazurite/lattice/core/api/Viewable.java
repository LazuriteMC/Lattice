package dev.lazurite.lattice.core.api;

import org.joml.Quaternionf;
import org.joml.Vector3d;

public interface Viewable {
    Vector3d getPosition();
    Vector3d getPosition(final double delta);

    Quaternionf getRotation();
    Quaternionf getRotation(final float delta);

    boolean shouldRenderPlayer();
}
