package dev.lazurite.lattice.impl.client.mixin.render;

import dev.lazurite.lattice.api.LatticePlayer;
import dev.lazurite.lattice.api.Viewable;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

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
