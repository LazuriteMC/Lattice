package dev.lazurite.lattice.impl.mixin.common;

import dev.lazurite.lattice.impl.duck.IServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkSectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements IServerPlayerEntity {

    @Unique private ChunkSectionPos prevCamPos = ChunkSectionPos.from(0, 0, 0);

    @Unique
    @Override
    public void setPrevCamPos(ChunkSectionPos prevCamPos) {
        this.prevCamPos = prevCamPos;
    }

    @Unique
    @Override
    public ChunkSectionPos getPrevCamPos() {
        return this.prevCamPos;
    }

    @Redirect(
            method = "setCameraEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;requestTeleport(DDD)V"
            )
    )
    public void requestTeleport(ServerPlayerEntity serverPlayerEntity, double destX, double destY, double destZ) { }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;updatePositionAndAngles(DDDFF)V"
            )
    )
    public void updatePositionAndAngles(ServerPlayerEntity serverPlayerEntity, double x, double y, double z, float yaw, float pitch) { }

}
