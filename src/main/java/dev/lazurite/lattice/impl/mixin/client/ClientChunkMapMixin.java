package dev.lazurite.lattice.impl.mixin.client;

import dev.lazurite.lattice.impl.client.IClientChunkMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/client/world/ClientChunkManager$ClientChunkMap")
public abstract class ClientChunkMapMixin {

    @Shadow @Final private int radius;

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "FIELD",
                    ordinal = 2
            )
    )
    public void init_FIELD(@Coerce IClientChunkMap clientChunkMap, int diameter) {
        clientChunkMap.setDiameter(diameter * 2);
    }

    @Inject(
            method = "isInRadius",
            at = @At("RETURN"),
            cancellable = true
    )
    private void isInRadius(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        cir.setReturnValue(cir.getReturnValue() || Math.abs(chunkX - player.chunkX) <= this.radius && Math.abs(chunkZ - player.chunkZ) <= this.radius);
    }

}
