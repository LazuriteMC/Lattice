package dev.lazurite.lattice.camera.impl.client;

import dev.lazurite.lattice.core.api.Viewable;
import net.fabricmc.api.ClientModInitializer;

import java.util.Stack;

public final class LatticeCameraClient implements ClientModInitializer {

    private static final Stack<Viewable> CLIENT_CAMERAS = new Stack<>();

    private LatticeCameraClient() { }

    @Override
    public void onInitializeClient() {

    }

}
