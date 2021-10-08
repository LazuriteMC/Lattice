package dev.lazurite.lattice.camera.impl.client.mixin;

import dev.lazurite.lattice.core.impl.iapi.duck.IPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow @Final private Minecraft minecraft;

    /**
     * Uses the {@link dev.lazurite.lattice.core.api.Viewable} position instead of the player's position.
     */
    @Redirect(
            method = "setupRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getX()D"
            )
    )
    public final double setupRender_getX(LocalPlayer localPlayer) {
        return ((IPlayer) this.minecraft.player).getViewable().latGetX();
    }

    /**
     * Uses the {@link dev.lazurite.lattice.core.api.Viewable} position instead of the player's position.
     */
    @Redirect(
            method = "setupRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getY()D"
            )
    )
    public final double setupRender_getY(LocalPlayer localPlayer) {
        return ((IPlayer) this.minecraft.player).getViewable().latGetY();
    }

    /**
     * Uses the {@link dev.lazurite.lattice.core.api.Viewable} position instead of the player's position.
     */
    @Redirect(
            method = "setupRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getZ()D"
            )
    )
    public final double setupRender_getZ(LocalPlayer localPlayer) {
        return ((IPlayer) this.minecraft.player).getViewable().latGetZ();
    }

}
