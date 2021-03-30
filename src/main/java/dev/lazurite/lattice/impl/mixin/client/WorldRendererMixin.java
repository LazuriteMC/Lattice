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
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D",
                    ordinal = 0
            )
    )
    private double setupTerrain_getX0(ClientPlayerEntity player) {
        return this.client.getCameraEntity().getX();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getY()D",
                    ordinal = 0
            )
    )
    private double setupTerrain_getY0(ClientPlayerEntity player) {
        return this.client.getCameraEntity().getY();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D",
                    ordinal = 0
            )
    )
    private double setupTerrain_getZ0(ClientPlayerEntity player) {
        return this.client.getCameraEntity().getZ();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkX:I",
                    ordinal = 0
            )
    )
    private int setupTerrain_chunkX0(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkX;
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkY:I",
                    ordinal = 0
            )
    )
    private int setupTerrain_chunkY0(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkY;
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkZ:I",
                    ordinal = 0
            )
    )
    private int setupTerrain_chunkZ0(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkZ;
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D",
                    ordinal = 1
            )
    )
    private double setupTerrain_getX1(ClientPlayerEntity player) {
        return this.client.getCameraEntity().getX();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getY()D",
                    ordinal = 1
            )
    )
    private double setupTerrain_getY1(ClientPlayerEntity player) {
        return this.client.getCameraEntity().getY();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D",
                    ordinal = 1
            )
    )
    private double setupTerrain_getZ1(ClientPlayerEntity player) {
        return this.client.getCameraEntity().getZ();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkX:I",
                    ordinal = 1
            )
    )
    private int setupTerrain_chunkX1(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkX;
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkY:I",
                    ordinal = 1
            )
    )
    private int setupTerrain_chunkY1(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkY;
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkZ:I",
                    ordinal = 1
            )
    )
    private int setupTerrain_chunkZ1(ClientPlayerEntity player) {
        return this.client.getCameraEntity().chunkZ;
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D",
                    ordinal = 2
            )
    )
    private double setupTerrain_getX2(ClientPlayerEntity player) {
        return this.client.getCameraEntity().getX();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D",
                    ordinal = 2
            )
    )
    private double setupTerrain_getZ2(ClientPlayerEntity player) {
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
