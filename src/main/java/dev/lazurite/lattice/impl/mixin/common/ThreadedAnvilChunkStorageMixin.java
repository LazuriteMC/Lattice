package dev.lazurite.lattice.impl.mixin.common;

import com.mojang.datafixers.DataFixer;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import java.io.File;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin extends VersionedChunkStorage implements ChunkHolder.PlayersWatchingChunkProvider {

    public ThreadedAnvilChunkStorageMixin(File file, DataFixer dataFixer, boolean bl) {
        super(file, dataFixer, bl);
    }

    // region getChebyshevDistance

    @Redirect(
            method = "getChebyshevDistance(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/server/network/ServerPlayerEntity;Z)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getX()D"
            )
    )
    private static double getChebyshevDistance_getX(ServerPlayerEntity player) {
        return player.getCameraEntity().getX();
    }

    @Redirect(
            method = "getChebyshevDistance(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/server/network/ServerPlayerEntity;Z)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getZ()D"
            )
    )
    private static double getChebyshevDistance_getZ(ServerPlayerEntity player) {
        return player.getCameraEntity().getZ();
    }

    // endregion getChebyshevDistance

    // region method_20726

    @ModifyArg(
            method = "method_20726",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/ChunkSectionPos;from(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/ChunkSectionPos;"
            )
    )
    private Entity method_20726_from(Entity serverPlayerEntity) {
        return ((ServerPlayerEntity) serverPlayerEntity).getCameraEntity();
    }

    // endregion method_20726

    // region handlePlayerAddedOrRemoved

    @Redirect(
            method = "handlePlayerAddedOrRemoved",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getX()D"
            )
    )
    double handlePlayerAddedOrRemoved_getX(ServerPlayerEntity player) {
        return player.getCameraEntity().getX();
    }

    @Redirect(
            method = "handlePlayerAddedOrRemoved",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getZ()D"
            )
    )
    double handlePlayerAddedOrRemoved_getZ(ServerPlayerEntity player) {
        return player.getCameraEntity().getZ();
    }

    @ModifyArg(
            method = "handlePlayerAddedOrRemoved",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/ChunkSectionPos;from(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/ChunkSectionPos;"
            )
    )
    Entity handlePlayerAddedOrRemoved_from(Entity player) {
        return ((ServerPlayerEntity) player).getCameraEntity();
    }

    // endregion handlePlayerAddedOrRemoved

    // region updateCameraPosition

    @Redirect(
            method = "updateCameraPosition",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getX()D"
            )
    )
    public double updateCameraPosition_getX(ServerPlayerEntity player) {
        return player.getCameraEntity().getX();
    }

    @Redirect(
            method = "updateCameraPosition",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getZ()D"
            )
    )
    public double updateCameraPosition_getZ(ServerPlayerEntity player) {
        return player.getCameraEntity().getZ();
    }

    @ModifyArg(
            method = "updateCameraPosition",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/ChunkSectionPos;from(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/ChunkSectionPos;"
            )
    )
    public Entity updateCameraPosition_from(Entity player) {
        return ((ServerPlayerEntity) player).getCameraEntity();
    }

    // endregion updateCameraPosition

}
