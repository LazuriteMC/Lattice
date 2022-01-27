package dev.lazurite.lattice.impl.client.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lazurite.lattice.api.LatticePlayer;
import dev.lazurite.lattice.api.Viewable;
import net.minecraft.client.Camera;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Fixes some hard-coded {@link LocalPlayer} positions to use {@link Viewable} instead.
 */
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    /**
     * Uses the {@link Viewable}'s position instead of the {@link LocalPlayer}'s position.
     */
    @Redirect(
            method = "setupRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getX()D"
            )
    )
    public final double setupRender_getX(LocalPlayer player) {
        return ((LatticePlayer) player).getViewable().getX();
    }

    /**
     * Uses the {@link Viewable}'s position instead of the {@link LocalPlayer}'s position.
     */
    @Redirect(
            method = "setupRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getY()D"
            )
    )
    public final double setupRender_getY(LocalPlayer player) {
        return ((LatticePlayer) player).getViewable().getY();
    }

    /**
     * Uses the {@link Viewable}'s position instead of the {@link LocalPlayer}'s position.
     */
    @Redirect(
            method = "setupRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getZ()D"
            )
    )
    public final double setupRender_getZ(LocalPlayer player) {
        return ((LatticePlayer) player).getViewable().getZ();
    }

    /**
     * Allows for the rendering of the {@link LocalPlayer}. For development.
     */
    @Redirect(
            method = "renderLevel",
            at = @At(
                    value = "CONSTANT",
                    args = "classValue=net/minecraft/client/player/LocalPlayer"
            ),
            require = 0
    )
    public boolean renderLevel_CONSTANT_dev(Object entity, Class<?> clazz, PoseStack poseStack, float f, long l, boolean bl, Camera camera) {
        return clazz.isInstance(entity) && camera.getEntity() instanceof Viewable viewable && !viewable.shouldRenderPlayer(); // the ! isn't a mistake
    }

    /**
     * Allows for the rendering of the {@link LocalPlayer}. For production.
     */
    @Redirect(
            method = "renderLevel",
            at = @At(
                    value = "CONSTANT",
                    args = "classValue=net/minecraft/class_746"
            ),
            require = 0
    )
    public boolean renderLevel_CONSTANT_prod(Object entity, Class<?> clazz, PoseStack poseStack, float f, long l, boolean bl, Camera camera) {
        return clazz.isInstance(entity) && camera.getEntity() instanceof Viewable viewable && !viewable.shouldRenderPlayer(); // the ! isn't a mistake
    }

}
