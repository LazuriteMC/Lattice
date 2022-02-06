package dev.lazurite.lattice.impl.client.duck;

import dev.lazurite.lattice.api.LatticePlayer;

public interface InternalLatticeLocalPlayer extends LatticePlayer {
    void setViewableEntityId(final int id);
    int getViewableEntityId();
}
