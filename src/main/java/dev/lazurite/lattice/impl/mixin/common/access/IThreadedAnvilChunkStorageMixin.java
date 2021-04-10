package dev.lazurite.lattice.impl.mixin.common.access;

import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ThreadedAnvilChunkStorage.class)
public interface IThreadedAnvilChunkStorageMixin {

    @Invoker
    static int callGetChebyshevDistance(ChunkPos pos, int x, int z) { return 0; }

    @Accessor
    int getWatchDistance();

}
