package dev.lazurite.lattice.impl.common.util;

import dev.lazurite.lattice.impl.common.duck.IServerPlayerEntity;
import dev.lazurite.lattice.impl.mixin.common.access.IThreadedAnvilChunkStorageMixin;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;

public final class ChebyshevDistance {

    private ChebyshevDistance() { }

    public static int fromServerPlayerEntity(ChunkPos pos, ServerPlayerEntity player, boolean useCameraPosition) {
        int k;
        int l;
        if (useCameraPosition) {
            ChunkSectionPos chunkSectionPos = player.getCameraPosition();
            k = chunkSectionPos.getSectionX();
            l = chunkSectionPos.getSectionZ();
        } else {
            k = MathHelper.floor(player.getX() / 16.0D);
            l = MathHelper.floor(player.getZ() / 16.0D);
        }

        return IThreadedAnvilChunkStorageMixin.callGetChebyshevDistance(pos, k, l);
    }

    public static int fromCameraEntity(ChunkPos pos, ServerPlayerEntity player, boolean useCameraPosition) {
        int k;
        int l;
        if (useCameraPosition) {
            ChunkSectionPos chunkSectionPos = ((IServerPlayerEntity) player).getPrevCamPos();
            k = chunkSectionPos.getSectionX();
            l = chunkSectionPos.getSectionZ();
        } else {
            k = MathHelper.floor(player.getCameraEntity().getX() / 16.0D);
            l = MathHelper.floor(player.getCameraEntity().getZ() / 16.0D);
        }

        return IThreadedAnvilChunkStorageMixin.callGetChebyshevDistance(pos, k, l);
    }

}
