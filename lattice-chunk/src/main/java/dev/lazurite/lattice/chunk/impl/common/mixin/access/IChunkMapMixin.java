package dev.lazurite.lattice.chunk.impl.common.mixin.access;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkMap.class)
public interface IChunkMapMixin {

//    @Accessor
//    static boolean callIsChunkInEuclideanRange(ChunkPos chunkPos, ServerPlayer serverPlayer, boolean bl, int i) {
//        return false;
//    }

}
