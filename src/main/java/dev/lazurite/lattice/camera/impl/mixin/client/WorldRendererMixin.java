package dev.lazurite.lattice.camera.impl.mixin.client;

import dev.lazurite.lattice.camera.api.Viewable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow @Final private MinecraftClient client;

    // region setupTerrain

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D"
            )
    )
    private double setupTerrain_getX(ClientPlayerEntity player) {
        return this.client.getCameraEntity().getX();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getY()D"
            )
    )
    private double setupTerrain_getY(ClientPlayerEntity player) {
        return this.client.getCameraEntity().getY();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D"
            )
    )
    private double setupTerrain_getZ(ClientPlayerEntity player) {
        return this.client.getCameraEntity().getZ();
    }

    // endregion setupTerrain

    // region render

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;isThirdPerson()Z"
            )
    )
    public boolean render_isThirdPerson(Camera camera) {
        return camera.isThirdPerson() || (camera.getFocusedEntity() instanceof Viewable viewable && viewable.shouldRenderSelf());
    }

    // for production environment
    @Redirect(
            method = "render",
            at = @At(
                    value = "CONSTANT",
                    args = "classValue=net/minecraft/class_746"
            ),
            require = 0
    )
    public boolean render_instanceof_prod(Object entity, Class<?> clazz, MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera) {
        return clazz.isInstance(entity) && camera.getFocusedEntity() instanceof Viewable viewable && !viewable.shouldRenderPlayer();
    }

    // for development environment
    @Redirect(
            method = "render",
            at = @At(
                    value = "CONSTANT",
                    args = "classValue=net/minecraft/client/network/ClientPlayerEntity"
            ),
            require = 0
    )
    public boolean render_instanceof_dev(Object entity, Class<?> clazz, MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera) {
        return clazz.isInstance(entity) && camera.getFocusedEntity() instanceof Viewable viewable && !viewable.shouldRenderPlayer();
    }

    // endregion render

}
