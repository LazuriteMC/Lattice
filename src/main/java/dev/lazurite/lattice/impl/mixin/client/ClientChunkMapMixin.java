package dev.lazurite.lattice.impl.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/client/world/ClientChunkManager$ClientChunkMap")
public abstract class ClientChunkMapMixin {

    @Shadow @Final private int radius;
    @Shadow @Final private int diameter;

    @Unique
    private boolean isInPlayerRadius(int chunkX, int chunkZ) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        return Math.abs(chunkX - (MathHelper.floor(player.getX()) >> 4)) <= this.radius && Math.abs(chunkZ - (MathHelper.floor(player.getZ()) >> 4)) <= this.radius;
    }

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/concurrent/atomic/AtomicReferenceArray;<init>(I)V"
            )
    )
    private int init_init(int diameterSquared) {
        return diameterSquared * 2;
    }

    @Inject(
            method = "getIndex",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getIndex_RETURN(int chunkX, int chunkZ, CallbackInfoReturnable<Integer> cir) {
        if (!this.isInPlayerRadius(chunkX, chunkZ)) {
            cir.setReturnValue(cir.getReturnValue() + this.diameter * this.diameter);
        }
    }

    @Inject(
            method = "isInRadius",
            at = @At("RETURN"),
            cancellable = true
    )
    private void isInRadius_RETURN(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || this.isInPlayerRadius(chunkX, chunkZ));
    }

}
