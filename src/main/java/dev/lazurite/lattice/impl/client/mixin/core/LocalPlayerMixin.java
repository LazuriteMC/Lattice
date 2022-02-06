package dev.lazurite.lattice.impl.client.mixin.core;

import dev.lazurite.lattice.impl.client.duck.InternalLatticeLocalPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin implements InternalLatticeLocalPlayer {

    private int viewableEntityId = ((LocalPlayer) (Object) this).getId();

    @Override
    public void setViewableEntityId(int id) {
        this.viewableEntityId = id;
    }

    @Override
    public int getViewableEntityId() {
        return this.viewableEntityId;
    }

}
