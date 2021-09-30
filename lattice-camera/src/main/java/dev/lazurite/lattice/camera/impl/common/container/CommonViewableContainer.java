package dev.lazurite.lattice.camera.impl.common.container;

import dev.lazurite.lattice.camera.impl.iapi.ViewableContainer;
import dev.lazurite.lattice.core.api.Viewable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class CommonViewableContainer implements ViewableContainer {

    private static final CommonViewableContainer INSTANCE = new CommonViewableContainer();

    private final List<Viewable> viewables;

    private CommonViewableContainer() {
        this.viewables = new LinkedList<>();
    }

    public static CommonViewableContainer getInstance() {
        return CommonViewableContainer.INSTANCE;
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
