package dev.lazurite.lattice.entity.api;

import dev.lazurite.lattice.core.api.Viewable;

public interface EntityViewable extends Viewable {
    boolean shouldRenderSelf();
}
