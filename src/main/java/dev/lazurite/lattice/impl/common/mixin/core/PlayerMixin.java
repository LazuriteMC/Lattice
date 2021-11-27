package dev.lazurite.lattice.impl.common.mixin.core;

import dev.lazurite.lattice.api.viewable.Viewable;
import dev.lazurite.lattice.impl.common.iapi.ILatticePlayer;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerMixin implements ILatticePlayer {

    // TODO Stack? (Viewable) this?
    private Viewable viewable = (Viewable) this;
    private SectionPos lastViewableSectionPos = SectionPos.of(0, 0, 0);

    @Override
    public void setViewable(Viewable viewable) {
        this.viewable = viewable;
    }

    @Override
    public Viewable getViewable() {
        return this.viewable;
    }

    @Override
    public boolean isViewableSelf() {
        return this.getViewable() == this;
    }

    @Override
    public void removeViewable() {
        this.viewable = (Viewable) this;
    }

    @Override
    public void setLastViewableSectionPos(SectionPos sectionPos) {
        this.lastViewableSectionPos = sectionPos;
    }

    @Override
    public SectionPos getLastViewableSectionPos() {
        return this.lastViewableSectionPos;
    }

}
