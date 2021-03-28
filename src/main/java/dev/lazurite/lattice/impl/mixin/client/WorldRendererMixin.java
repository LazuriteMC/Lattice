package dev.lazurite.lattice.impl.mixin.client;

import dev.lazurite.lattice.api.entity.Viewable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements SynchronousResourceReloadListener, AutoCloseable {

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract void processGlobalEvent(int eventId, BlockPos pos, int i);

    private void test() {
        /*

        // original

        do {
            do {
                do {

                } while(!this.entityRenderDispatcher.shouldRender(entity, frustum2, d, e, f) && !entity.hasPassengerDeep(this.client.player));
            } while(entity == camera.getFocusedEntity() && !camera.isThirdPerson() && (!(camera.getFocusedEntity() instanceof LivingEntity) || !((LivingEntity)camera.getFocusedEntity()).isSleeping()));
        } while(entity instanceof ClientPlayerEntity && camera.getFocusedEntity() != entity);

        // desired

        do {
            do {
                do {

                } while(!this.entityRenderDispatcher.shouldRender(entity, frustum2, d, e, f) && !entity.hasPassengerDeep(this.client.player));
            } while(entity == camera.getFocusedEntity() && !camera.isThirdPerson() && (!(camera.getFocusedEntity() instanceof LivingEntity) || !((LivingEntity)camera.getFocusedEntity()).isSleeping()));
        } while(entity instanceof ClientPlayerEntity && (camera.getFocusedEntity() != entity || camera.getFocusedEntity() instanceof RenderableEntity &&  ((RenderableEntity) camera.getFocusedEntity()).shouldRenderPlayer());

        */
    }

    @Unique
    private double getSafeCameraEntityPosX() {
        return this.client.getCameraEntity() != null ? this.client.getCameraEntity().getX() : this.client.player.getX();
    }

    @Unique
    private double getSafeCameraEntityPosY() {
        return this.client.getCameraEntity() != null ? this.client.getCameraEntity().getY() : this.client.player.getY();
    }

    @Unique
    private double getSafeCameraEntityPosZ() {
        return this.client.getCameraEntity() != null ? this.client.getCameraEntity().getZ() : this.client.player.getZ();
    }

    @Unique
    private int getSafeCameraChunkPosX() {
        return this.client.getCameraEntity() != null ? this.client.getCameraEntity().chunkX : this.client.player.chunkX;
    }

    @Unique
    private int getSafeCameraChunkPosY() {
        return this.client.getCameraEntity() != null ? this.client.getCameraEntity().chunkY : this.client.player.chunkY;
    }

    @Unique
    private int getSafeCameraChunkPosZ() {
        return this.client.getCameraEntity() != null ? this.client.getCameraEntity().chunkZ : this.client.player.chunkZ;
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D",
                    ordinal = 0
            )
    )
    private double setupTerrain_getX0(ClientPlayerEntity ignored) {
        return this.getSafeCameraEntityPosX();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getY()D",
                    ordinal = 0
            )
    )
    private double setupTerrain_getY0(ClientPlayerEntity ignored) {
        return this.getSafeCameraEntityPosY();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D",
                    ordinal = 0
            )
    )
    private double setupTerrain_getZ0(ClientPlayerEntity ignored) {
        return this.getSafeCameraEntityPosZ();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkX:I",
                    ordinal = 0
            )
    )
    private int setupTerrain_chunkX0(ClientPlayerEntity ignored) {
        return this.getSafeCameraChunkPosX();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkY:I",
                    ordinal = 0
            )
    )
    private int setupTerrain_chunkY0(ClientPlayerEntity ignored) {
        return this.getSafeCameraChunkPosY();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkZ:I",
                    ordinal = 0
            )
    )
    private int setupTerrain_chunkZ0(ClientPlayerEntity ignored) {
        return this.getSafeCameraChunkPosZ();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D",
                    ordinal = 1
            )
    )
    private double setupTerrain_getX1(ClientPlayerEntity ignored) {
        return this.getSafeCameraEntityPosX();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getY()D",
                    ordinal = 1
            )
    )
    private double setupTerrain_getY1(ClientPlayerEntity ignored) {
        return this.getSafeCameraEntityPosY();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D",
                    ordinal = 1
            )
    )
    private double setupTerrain_getZ1(ClientPlayerEntity ignored) {
        return this.getSafeCameraEntityPosZ();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkX:I",
                    ordinal = 1
            )
    )
    private int setupTerrain_chunkX1(ClientPlayerEntity ignored) {
        return this.getSafeCameraChunkPosX();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkY:I",
                    ordinal = 1
            )
    )
    private int setupTerrain_chunkY1(ClientPlayerEntity ignored) {
        return this.getSafeCameraChunkPosY();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;chunkZ:I",
                    ordinal = 1
            )
    )
    private int setupTerrain_chunkZ1(ClientPlayerEntity ignored) {
        return this.getSafeCameraChunkPosZ();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D",
                    ordinal = 2
            )
    )
    private double setupTerrain_getX2(ClientPlayerEntity ignored) {
        return this.getSafeCameraEntityPosX();
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D",
                    ordinal = 2
            )
    )
    private double setupTerrain_getZ2(ClientPlayerEntity ignored) {
        return this.getSafeCameraEntityPosZ();
    }

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

}
