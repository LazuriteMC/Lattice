package dev.lazurite.lattice.core.impl.common.mixin;

import com.mojang.authlib.GameProfile;
import dev.lazurite.lattice.core.impl.iapi.duck.IPlayer;
import dev.lazurite.lattice.core.api.Viewable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Stack;

@Mixin(Player.class)
public abstract class PlayerMixin implements IPlayer {

    @Unique
    private final List<Viewable> viewables = new Stack<>();

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    public void init(Level level, BlockPos blockPos, float f, GameProfile gameProfile, CallbackInfo ci) {
        this.viewables.add((Viewable) this);
    }

    @Override
    public void setViewable(Viewable viewable) {
        this.viewables.add(viewable);
    }

    @Override
    public Viewable getViewable() {
        return this.viewables.get(this.viewables.size() - 1);
    }

}
