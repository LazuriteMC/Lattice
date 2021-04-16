package dev.lazurite.lattice.impl.mixin.client.compat.sodium;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.world.SodiumChunkManager", remap = false)
public abstract class SodiumChunkManagerMixin {

    @Shadow private int centerX;
    @Shadow private int centerZ;

    @Redirect(
            method = "updateLoadDistance",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Math;abs(I)I",
                    ordinal = 0
            )
    )
    public int updateLoadDistance_abs0(int x) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        return Math.min(x, x + this.centerX - player.chunkX);
    }

    @Redirect(
            method = "updateLoadDistance",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Math;abs(I)I",
                    ordinal = 1
            )
    )
    public int updateLoadDistance_abs1(int z) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        return Math.min(z, z + this.centerZ - player.chunkZ);
    }

    @Inject(
            method = "getChunkMapSize",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void getChunkMapSize(int radius, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValue() * 2);
    }

}
