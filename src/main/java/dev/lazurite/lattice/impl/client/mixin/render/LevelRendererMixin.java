package dev.lazurite.lattice.impl.client.mixin.render;

import dev.lazurite.lattice.api.LatticePlayer;
import dev.lazurite.lattice.api.viewable.Viewable;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow @Final private Map<BlockPos, SoundInstance> playingRecords;

    /**
     * Uses the {@link Viewable} position instead of the player's position.
     */
    @Redirect(
            method = "setupRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getX()D"
            )
    )
    public final double setupRender_getX(LocalPlayer player) {
        return ((LatticePlayer) player).getViewable().getViewableX();
    }

    /**
     * Uses the {@link Viewable} position instead of the player's position.
     */
    @Redirect(
            method = "setupRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getY()D"
            )
    )
    public final double setupRender_getY(LocalPlayer player) {
        return ((LatticePlayer) player).getViewable().getViewableY();
    }

    /**
     * Uses the {@link Viewable} position instead of the player's position.
     */
    @Redirect(
            method = "setupRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getZ()D"
            )
    )
    public final double setupRender_getZ(LocalPlayer player) {
        return ((LatticePlayer) player).getViewable().getViewableZ();
    }

}
