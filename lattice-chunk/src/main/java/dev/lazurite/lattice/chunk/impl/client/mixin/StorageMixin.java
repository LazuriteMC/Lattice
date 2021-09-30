package dev.lazurite.lattice.chunk.impl.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.multiplayer.ClientChunkCache$Storage")
public abstract class StorageMixin {

    @Unique private int viewRangeSquared;

    @Shadow @Final int chunkRadius;

    @Unique
    private boolean isInPlayerRadius(int x, int z) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        ChunkPos chunkPos = localPlayer.chunkPosition();

        return Math.abs(x - chunkPos.x) <= this.chunkRadius && Math.abs(z - chunkPos.z) <= this.chunkRadius;
    }

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/concurrent/atomic/AtomicReferenceArray;<init>(I)V"
            )
    )
    int init_init(int i) {
        this.viewRangeSquared = i;
        return i * 2;
    }

    @Inject(
            method = "getIndex",
            at = @At("RETURN"),
            cancellable = true
    )
    void getIndex_RETURN(int i, int j, CallbackInfoReturnable<Integer> cir) {
        if (!this.isInPlayerRadius(i, j)) {
            cir.setReturnValue(cir.getReturnValueI() + this.viewRangeSquared);
        }
    }

    @Inject(
            method = "inRange",
            at = @At("RETURN"),
            cancellable = true
    )
    void inRange(int i, int j, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValueZ() || this.isInPlayerRadius(i, j));
    }

}
