package dev.lazurite.lattice.impl.mixin.common;

import dev.lazurite.lattice.impl.mixin.common.access.IEntityTrackerEntry;
import dev.lazurite.lattice.impl.util.ChebyshevDistance;
import dev.lazurite.lattice.impl.mixin.common.access.IThreadedAnvilChunkStorageMixin;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(targets = "net.minecraft.server.world.ThreadedAnvilChunkStorage$EntityTracker")
public abstract class EntityTrackerMixin {

    @Shadow @Final EntityTrackerEntry entry;
    @Shadow @Final Entity entity;
    @Shadow protected abstract int getMaxTrackDistance();

//    @ModifyVariable(
//            method = "updateCameraPosition(Lnet/minecraft/server/network/ServerPlayerEntity;)V",
//            at = @At(
//                    value = "STORE",
//                    ordinal = 0
//            ),
//            ordinal = 0
//    )
//    public boolean updateCameraPosition_STORE0(boolean bl, ServerPlayerEntity player) {
//        Vec3d vec3d = player.getCameraEntity().getPos().subtract(this.entry.getLastPos());
//        int i = Math.min(this.getMaxTrackDistance(), (((IThreadedAnvilChunkStorageMixin) ((IEntityTrackerEntry) this.entry).getWorld().getChunkManager().threadedAnvilChunkStorage).getWatchDistance() - 1) * 16);
//        return bl || vec3d.x >= (double)(-i) && vec3d.x <= (double)i && vec3d.z >= (double)(-i) && vec3d.z <= (double)i && this.entity.canBeSpectated(player);
//    }

//    @ModifyVariable(
//            method = "updateCameraPosition(Lnet/minecraft/server/network/ServerPlayerEntity;)V",
//            at = @At(
//                    value = "STORE",
//                    ordinal = 1
//            ),
//            index = 5 // can't use ordinal (bug?)
//    )
//    public boolean updateCameraPosition_STORE1(boolean bl2, ServerPlayerEntity player) {
//        return bl2 || ChebyshevDistance.fromCameraEntity(new ChunkPos(this.entity.chunkX, this.entity.chunkZ), player, false) <= ((IThreadedAnvilChunkStorageMixin) ((IEntityTrackerEntry) this.entry).getWorld().getChunkManager().threadedAnvilChunkStorage).getWatchDistance();
//    }

}
