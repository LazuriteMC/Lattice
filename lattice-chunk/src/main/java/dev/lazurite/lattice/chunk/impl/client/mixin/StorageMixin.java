package dev.lazurite.lattice.chunk.impl.client.mixin;

import dev.lazurite.lattice.core.impl.common.util.ChunkPosUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicReferenceArray;

@Mixin(targets = "net.minecraft.client.multiplayer.ClientChunkCache$Storage")
public abstract class StorageMixin {

    @Shadow @Final int chunkRadius;
    @Shadow @Final AtomicReferenceArray<LevelChunk> chunks;

    /**
     * Returns {@code true} if the {@link ChunkPos} is within the range of the {@link LocalPlayer}.
     * @see StorageMixin#inRange(int, int, CallbackInfoReturnable)
     */
    @Unique
    private boolean isInPlayerRadius(int x, int z) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        ChunkPos chunkPos = ChunkPosUtil.of(localPlayer);

        return Math.abs(x - chunkPos.x) <= this.chunkRadius && Math.abs(z - chunkPos.z) <= this.chunkRadius;
    }

    /**
     * Doubles the size of the {@link net.minecraft.client.multiplayer.ClientChunkCache.Storage#chunks} {@link AtomicReferenceArray}.
     * Allows for the storing of two chunk "regions."
     */
    @ModifyArg(
            method = "Lnet/minecraft/client/multiplayer/ClientChunkCache$Storage;<init>(I)V",
//            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/concurrent/atomic/AtomicReferenceArray;<init>(I)V"
            )
    )
    int init_init(int i) {
        return i * 2;
    }

    /**
     * Allows getting indicies for the second half of the {@link net.minecraft.client.multiplayer.ClientChunkCache.Storage#chunks} {@link AtomicReferenceArray}.
     * @see StorageMixin#init_init(int)
     */
    @Inject(
            method = "Lnet/minecraft/client/multiplayer/ClientChunkCache$Storage;getIndex(II)I",
//            method = "getIndex",
            at = @At("RETURN"),
            cancellable = true
    )
    void getIndex_RETURN(int i, int j, CallbackInfoReturnable<Integer> cir) {
        if (!this.isInPlayerRadius(i, j)) {
            cir.setReturnValue(cir.getReturnValueI() + this.chunks.length() / 2);
        }
    }

    /**
     * Returns true if the {@link ChunkPos} is within range of the view center or the {@link LocalPlayer}.
     * @see StorageMixin#isInPlayerRadius(int, int)
     */
    @Inject(
            method = "Lnet/minecraft/client/multiplayer/ClientChunkCache$Storage;inRange(II)Z",
//            method = "inRange",
            at = @At("RETURN"),
            cancellable = true
    )
    void inRange(int i, int j, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValueZ() || this.isInPlayerRadius(i, j));
    }

}
