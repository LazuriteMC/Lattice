package dev.lazurite.lattice.core.api;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;

public interface Viewable {
    Vector3d latGetPosition();
    Vector3d latGetPosition(float delta);
    Vector3d latGetPosition(double delta);

    Quaternion latGetRotation();
    Quaternion latGetRotation(float delta);
    Quaternion latGetRotation(double delta);

    boolean latShouldRenderPlayer();
}
