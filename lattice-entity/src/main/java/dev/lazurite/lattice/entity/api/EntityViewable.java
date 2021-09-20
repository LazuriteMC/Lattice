package dev.lazurite.lattice.entity.api;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;
import dev.lazurite.lattice.core.api.Viewable;

public interface EntityViewable extends Viewable {
    Vector3d latGetEyePosition();
    Vector3d latGetEyePosition(float delta);
    Vector3d latGetEyePosition(double delta);

    Quaternion latGetHeadRotation();
    Quaternion latGetHeadRotation(float delta);
    Quaternion latGetHeadRotation(double delta);

    boolean latShouldRenderSelf();
}
