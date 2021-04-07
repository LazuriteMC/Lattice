package dev.lazurite.lattice.impl.mixin.client;

import dev.lazurite.lattice.api.entity.Viewable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.SynchronousResourceReloadListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements SynchronousResourceReloadListener, AutoCloseable {

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

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkX:I"
            )
    )
    private int setupTerrain_chunkX(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkX;
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkY:I"
            )
    )
    private int setupTerrain_chunkY(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkY;
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkZ:I"
            )
    )
    private int setupTerrain_chunkZ(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkZ;
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
        return camera.isThirdPerson() || (camera.getFocusedEntity() instanceof Viewable && ((Viewable) camera.getFocusedEntity()).shouldRenderSelf());
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "CONSTANT",
                    args = "classValue=net/minecraft/client/network/ClientPlayerEntity"
            )
    )
    public boolean render_instanceof(Object entity, Class<?> clazz,
                                     MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera) {
        return clazz.isInstance(entity) && camera.getFocusedEntity() instanceof Viewable && !((Viewable) camera.getFocusedEntity()).shouldRenderPlayer();
    }

    // endregion render

}
