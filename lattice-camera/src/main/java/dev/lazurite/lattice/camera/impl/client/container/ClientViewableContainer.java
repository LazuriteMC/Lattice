package dev.lazurite.lattice.camera.impl.client.container;

import dev.lazurite.lattice.camera.impl.iapi.ViewableContainer;
import dev.lazurite.lattice.core.api.Viewable;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public final class ClientViewableContainer implements ViewableContainer {

    private static final ClientViewableContainer INSTANCE = new ClientViewableContainer();

    private final List<Viewable> viewables;

    public ClientViewableContainer() {
        this.viewables = new Stack<>();
    }

    public static ClientViewableContainer getInstance() {
        return ClientViewableContainer.INSTANCE;
    }

    @Override
    public void addViewable(Viewable viewable) {
        this.viewables.add(viewable);
    }

    @Override
    public List<Viewable> getViewables() {
        return Collections.unmodifiableList(this.viewables);
    }

}
