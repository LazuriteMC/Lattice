package dev.lazurite.lattice.camera.impl.common;

import dev.lazurite.lattice.core.api.Viewable;
import net.fabricmc.api.ModInitializer;

import java.util.LinkedList;

public final class LatticeCameraCommon implements ModInitializer {

    private static final LinkedList<Viewable> COMMON_CAMERAS = new LinkedList<>();

    private LatticeCameraCommon() { }

    @Override
    public void onInitialize() {

    }

}
