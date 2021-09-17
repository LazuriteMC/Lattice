package dev.lazurite.lattice.camera.impl.mixin.common;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

   @Redirect(
           method = "sendToPlayerIfNearby",
           at = @At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/util/math/BlockPos;isWithinDistance(Lnet/minecraft/util/math/Position;D)Z"
           )
   )
   private boolean sendToPlayerIfNearby_isWithinDistance(BlockPos blockPos, Position pos, double distance, ServerPlayerEntity player) {
       return blockPos.isWithinDistance(pos, distance) || player.getCameraEntity().getBlockPos().isWithinDistance(pos, distance);
   }

}
