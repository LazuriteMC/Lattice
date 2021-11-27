package dev.lazurite.lattice.impl.common.mixin.chunk;

import dev.lazurite.lattice.api.LatticePlayer;
import dev.lazurite.lattice.impl.common.iapi.ILatticePlayer;
import dev.lazurite.lattice.impl.common.util.ChunkPosUtil;
import dev.lazurite.lattice.impl.common.util.SectionPosUtil;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

// TODO: This needs a cleanup
@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {

    private SectionPos lastLastViewableSectionPos = SectionPos.of(0, 0, 0);
    private SectionPos lastLastSectionPos = SectionPos.of(0, 0, 0);

    /**
     * @see ChunkMapMixin#updatePlayerStatus_HEAD(ServerPlayer, boolean, CallbackInfo)
     * @see ChunkMapMixin#updatePlayerStatus_updateChunkTracking(ChunkMap, ServerPlayer, ChunkPos, MutableObject, boolean, boolean)
     * @see ChunkMapMixin#updatePlayerStatus_TAIL(ServerPlayer, boolean, CallbackInfo)
     */
    private final List<ChunkPos> queuedChunks = new ArrayList<>();

    private final List<ChunkPos> addedChunks = new ArrayList<>();
    private final List<ChunkPos> removedChunks = new ArrayList<>();
    private final List<ChunkPos> inRangeChunks = new ArrayList<>();

    @Shadow int viewDistance;
    @Shadow @Final private ChunkMap.DistanceManager distanceManager;
    @Shadow public static boolean isChunkInEuclideanRange(int i, int j, int k, int l, int m) { return false; }
    @Shadow protected abstract void updateChunkTracking(ServerPlayer serverPlayer, ChunkPos chunkPos, MutableObject<ClientboundLevelChunkWithLightPacket> mutableObject, boolean bl, boolean bl2);
    @Shadow private static boolean isChunkInEuclideanRange(ChunkPos chunkPos, int i, int j, int k) { return false; }
    @Shadow private static boolean isChunkOnEuclideanBorder(int i, int j, int k, int l, int m) { return false; }

    private static boolean isChunkInEuclideanRangeViewable(ChunkPos chunkPos, ServerPlayer serverPlayer, boolean bl, int i) {
        final var player = ((ILatticePlayer) serverPlayer);
        final var viewable = player.getViewable();

        final int j;
        final int k;

        if (bl) {
            SectionPos sectionPos = player.getLastViewableSectionPos();

            j = sectionPos.x();
            k = sectionPos.z();
        } else {
            // use posToSectionCoord since viewable doesn't have a BlockPos
            j = SectionPos.posToSectionCoord(viewable.getViewableX());
            k = SectionPos.posToSectionCoord(viewable.getViewableZ());
        }

        return isChunkInEuclideanRange(chunkPos, j, k, i);
    }

    private static boolean isChunkOnEuclideanBorderViewable(ChunkPos chunkPos, ServerPlayer serverPlayer, boolean bl, int i) {
        final var player = ((ILatticePlayer) serverPlayer);
        final var viewable = player.getViewable();

        final int j;
        final int k;

        if (bl) {
            SectionPos sectionPos = player.getLastViewableSectionPos();

            j = sectionPos.x();
            k = sectionPos.z();
        } else {
            // use posToSectionCoord since viewable doesn't have a BlockPos
            j = SectionPos.posToSectionCoord(viewable.getViewableX());
            k = SectionPos.posToSectionCoord(viewable.getViewableZ());
        }

        return isChunkOnEuclideanBorder(chunkPos.x, chunkPos.z, j, k, i);
    }

    // region euclideanDistanceSquared
    /*
    Used in comparison with a constant (< 16384.0D).
    Returning the smallest number increases the odds that the comparison will pass.
    Same as || in boolean logic.
     */

    @ModifyVariable(
            method = "euclideanDistanceSquared",
            at = @At("STORE"),
            ordinal = 2
    )
    private static double euclideanDistanceSquared_STORE2(double f, ChunkPos chunkPos, Entity entity) {
        double d = SectionPos.sectionToBlockCoord(chunkPos.x, 8);
        return Math.min(f, d - ((ILatticePlayer) entity).getViewable().getViewableX());
    }

    @ModifyVariable(
            method = "euclideanDistanceSquared",
            at = @At("STORE"),
            ordinal = 3
    )
    private static double euclideanDistanceSquared_STORE3(double g, ChunkPos chunkPos, Entity entity) {
        double e = SectionPos.sectionToBlockCoord(chunkPos.z, 8);
        return Math.min(g, e - ((ILatticePlayer) entity).getViewable().getViewableZ());
    }

    // endregion euclideanDistanceSquared

    // region isChunkInEuclideanRange

    @Inject(
            method = "isChunkInEuclideanRange(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/server/level/ServerPlayer;ZI)Z",
            at = @At("TAIL"),
            cancellable = true
    )
    private static void isChunkInEuclideanRange_RETURN(ChunkPos chunkPos, ServerPlayer serverPlayer, boolean bl, int i, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValueZ() || isChunkInEuclideanRangeViewable(chunkPos, serverPlayer, bl, i));
    }

    // endregion isChunkInEuclideanRange

    // region isChunkOnEuclideanBorder

    @Inject(
            method = "isChunkOnEuclideanBorder(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/server/level/ServerPlayer;ZI)Z",
            at = @At("TAIL"),
            cancellable = true
    )
    private static void isChunkOnEuclideanBorder_RETURN(ChunkPos chunkPos, ServerPlayer serverPlayer, boolean bl, int i, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValueZ() || isChunkOnEuclideanBorderViewable(chunkPos, serverPlayer, bl, i));
    }

    // endregion isChunkOnEuclideanBorder

    // region updatePlayerStatus
    /*
    Both playerMap#addPlayer and playerMap#removePlayer take a long (ChunkPos) but it isn't used.
    This may change in the future and is the reason for this comment.
     */

    @Inject(
            method = "updatePlayerStatus",
            at = @At("HEAD")
    )
    void updatePlayerStatus_HEAD(ServerPlayer serverPlayer, boolean bl, CallbackInfo ci) {
        this.queuedChunks.clear();

        this.lastLastSectionPos = serverPlayer.getLastSectionPos();
        this.lastLastViewableSectionPos = ((ILatticePlayer) serverPlayer).getLastViewableSectionPos();
    }

    @Inject(
            method = "updatePlayerStatus",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap$DistanceManager;addPlayer(Lnet/minecraft/core/SectionPos;Lnet/minecraft/server/level/ServerPlayer;)V",
                    shift = At.Shift.AFTER
            )
    )
    void updatePlayerStatus_addPlayer(ServerPlayer serverPlayer, boolean bl, CallbackInfo ci) {
        final var player = ((ILatticePlayer) serverPlayer);

        final var viewableChunkPos = ChunkPosUtil.of(player.getViewable());
        final var serverPlayerChunkPos = ChunkPosUtil.of(serverPlayer);

        if (!viewableChunkPos.equals(serverPlayerChunkPos)) {
            this.distanceManager.addPlayer(SectionPosUtil.of(player.getViewable()), serverPlayer);
        }
    }

    @Inject(
            method = "updatePlayerStatus",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap$DistanceManager;removePlayer(Lnet/minecraft/core/SectionPos;Lnet/minecraft/server/level/ServerPlayer;)V",
                    shift = At.Shift.AFTER
            )
    )
    void updatePlayerStatus_removePlayer(ServerPlayer serverPlayer, boolean bl, CallbackInfo ci) {
        if (!this.lastLastViewableSectionPos.equals(this.lastLastSectionPos)) {
            this.distanceManager.removePlayer(this.lastLastViewableSectionPos, serverPlayer);
        }
    }

    @Redirect(
            method = "updatePlayerStatus",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap;updateChunkTracking(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/ChunkPos;Lorg/apache/commons/lang3/mutable/MutableObject;ZZ)V"
            )
    )
    void updatePlayerStatus_updateChunkTracking(ChunkMap chunkMap, ServerPlayer serverPlayer, ChunkPos chunkPos, MutableObject<ClientboundLevelChunkWithLightPacket> mutableObject, boolean bl, boolean bl2) {
        this.queuedChunks.add(chunkPos);
    }

    @Inject(
            method = "updatePlayerStatus",
            at = @At("TAIL")
    )
    void updatePlayerStatus_TAIL(ServerPlayer serverPlayer, boolean bl, CallbackInfo ci) {
        final var viewable = ((LatticePlayer) serverPlayer).getViewable();

        final var viewableSectionPosX = SectionPosUtil.posToSectionCoord(viewable.getViewableX());
        final var viewableSectionPosZ = SectionPosUtil.posToSectionCoord(viewable.getViewableZ());

        if (!ChunkPosUtil.of(viewable).equals(ChunkPosUtil.of(serverPlayer))) {
            for (var x = viewableSectionPosX - this.viewDistance; x <= viewableSectionPosX + this.viewDistance; ++x) {
                for (var z = viewableSectionPosZ - this.viewDistance; z <= viewableSectionPosZ + this.viewDistance; ++z) {
                    final var chunkPos = new ChunkPos(x, z);

                    if (isChunkInEuclideanRange(x, z, viewableSectionPosX, viewableSectionPosZ, this.viewDistance) && !this.queuedChunks.contains(chunkPos)) {
                        this.queuedChunks.add(chunkPos);
                    }
                }
            }
        }

        this.queuedChunks.forEach(chunkPos -> this.updateChunkTracking(serverPlayer, chunkPos, new MutableObject<>(), !bl, bl));
    }

    // endregion updatePlayerStatus

    // region updatePlayerPos
    /*
    The updatePlayerPos method returns a SectionPos that is never used.
    This may change in the future and is the reason for this comment.
     */

    @Inject(
            method = "updatePlayerPos",
            at = @At("HEAD")
    )
    private void updatePlayerPos_HEAD(ServerPlayer serverPlayer, CallbackInfoReturnable<SectionPos> cir) {
        final var player = ((ILatticePlayer) serverPlayer);

        this.lastLastSectionPos = serverPlayer.getLastSectionPos();
        this.lastLastViewableSectionPos = player.getLastViewableSectionPos(); // preserve for later comparison

        player.setLastViewableSectionPos(SectionPosUtil.of(player.getViewable()));
    }

    @Redirect(
            method = "updatePlayerPos",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/SectionPos;x()I"
            )
    )
    private int updatePlayerPos_x(SectionPos sectionPos, ServerPlayer serverPlayer) {
        return SectionPosUtil.posToSectionCoord(((LatticePlayer) serverPlayer).getViewable().getViewableX());
    }

    @Redirect(
            method = "updatePlayerPos",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/SectionPos;z()I"
            )
    )
    private int updatePlayerPos_z(SectionPos sectionPos, ServerPlayer serverPlayer) {
        return SectionPosUtil.posToSectionCoord(((LatticePlayer) serverPlayer).getViewable().getViewableZ());
    }

    // endregion updatePlayerPosition

    // region move
    /*
    The move method calls playerMap.updatePlayer which has an empty body.
    This may change in the future and is the reason for this comment.
     */

    @Inject(
            method = "move",
            at = @At("HEAD")
    )
    public void move_HEAD(ServerPlayer serverPlayer, CallbackInfo ci) {
        this.addedChunks.clear();
        this.removedChunks.clear();
        this.inRangeChunks.clear();
    }

    @ModifyVariable(
            method = "move",
            at = @At("LOAD"),
            ordinal = 2
    )
    public boolean move_LOAD(boolean bl3, ServerPlayer serverPlayer) {
        final var player = ((ILatticePlayer) serverPlayer);
        return bl3 || SectionPosUtil.of(player.getViewable()).asLong() != player.getLastViewableSectionPos().asLong();
    }

    @Inject(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap$DistanceManager;removePlayer(Lnet/minecraft/core/SectionPos;Lnet/minecraft/server/level/ServerPlayer;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void move_removePlayer(ServerPlayer serverPlayer, CallbackInfo ci) {
        final var lastViewableSectionPos = this.lastLastViewableSectionPos;
        final var lastServerPlayerSectionPos = this.lastLastSectionPos;

        if (!lastViewableSectionPos.chunk().equals(lastServerPlayerSectionPos.chunk())) {
            this.distanceManager.removePlayer(lastViewableSectionPos, serverPlayer);
        }
    }

    @Inject(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap$DistanceManager;addPlayer(Lnet/minecraft/core/SectionPos;Lnet/minecraft/server/level/ServerPlayer;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void move_addPlayer(ServerPlayer serverPlayer, CallbackInfo ci) {
        final var viewableSectionPos = SectionPosUtil.of(((LatticePlayer) serverPlayer).getViewable());
        final var serverPlayerSectionPos = SectionPos.of(serverPlayer);

        if (!viewableSectionPos.chunk().equals(serverPlayerSectionPos.chunk())) {
            this.distanceManager.addPlayer(viewableSectionPos, serverPlayer);
        }
    }

    @Redirect(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap;updateChunkTracking(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/ChunkPos;Lorg/apache/commons/lang3/mutable/MutableObject;ZZ)V"
            )
    )
    public void move_updateChunkTracking(ChunkMap chunkMap, ServerPlayer serverPlayer, ChunkPos chunkPos, MutableObject<ClientboundLevelChunkWithLightPacket> mutableObject, boolean bl, boolean bl2) {
//        if (!bl && bl2 && !this.addedChunks.contains(chunkPos)) {
//            this.addedChunks.add(chunkPos);
//        } else if (bl && !bl2 && !this.removedChunks.contains(chunkPos)) {
//            this.removedChunks.add(chunkPos);
//        } else if (bl && bl2 && !this.inRangeChunks.contains(chunkPos)) {
//            this.inRangeChunks.add(chunkPos);
//        }

        if (bl && bl2) {
            this.inRangeChunks.add(chunkPos);
        } else if (!bl && bl2) {
            this.addedChunks.add(chunkPos);
        } else {
            this.removedChunks.add(chunkPos);
        }
    }

    @Inject(
            method = "move",
            at = @At("TAIL")
    )
    public void move_TAIL(ServerPlayer serverPlayer, CallbackInfo ci) {
        final var player = ((ILatticePlayer) serverPlayer);

        final var viewableSectionPos = SectionPosUtil.of(player.getViewable());
        final var serverPlayerSectionPos = SectionPos.of(serverPlayer);

        final var lastViewableSectionPos = this.lastLastViewableSectionPos;
        final var lastServerPlayerSectionPos = this.lastLastSectionPos;

        // if check not necessary I don't think but more efficient
        if (!viewableSectionPos.chunk().equals(serverPlayerSectionPos.chunk()) || !lastViewableSectionPos.chunk().equals(lastServerPlayerSectionPos.chunk())) {

            if (Math.abs(lastViewableSectionPos.getX() - viewableSectionPos.getX()) <= this.viewDistance * 2 && Math.abs(lastViewableSectionPos.getZ() - viewableSectionPos.getZ()) <= this.viewDistance * 2) {
                for (var x = Math.min(viewableSectionPos.getX(), lastViewableSectionPos.getX()) - this.viewDistance; x <= Math.max(viewableSectionPos.getX(), lastViewableSectionPos.getX()) + this.viewDistance; ++x) {
                    for (var z = Math.min(viewableSectionPos.getZ(), lastViewableSectionPos.getZ()) - this.viewDistance; z <= Math.max(viewableSectionPos.getZ(), lastViewableSectionPos.getZ()) + this.viewDistance; ++z) {
                        ChunkPos chunkPos = new ChunkPos(x, z);

                        boolean bl = isChunkInEuclideanRange(chunkPos, lastViewableSectionPos.getX(), lastViewableSectionPos.getZ(), this.viewDistance);
                        boolean bl2 = isChunkInEuclideanRange(chunkPos, viewableSectionPos.getX(), viewableSectionPos.getZ(), this.viewDistance);

                        if (bl && bl2) {
                            this.inRangeChunks.add(chunkPos);
                        } else if (!bl && bl2) {
                            this.addedChunks.add(chunkPos);
                        } else {
                            this.removedChunks.add(chunkPos);
                        }
                    }
                }
            } else {
                for (var x = lastViewableSectionPos.getX() - this.viewDistance; x <= lastViewableSectionPos.getX() + this.viewDistance; ++x) {
                    for (var z = lastViewableSectionPos.getZ() - this.viewDistance; z <= lastViewableSectionPos.getZ() + this.viewDistance; ++z) {
                        final var chunkPos = new ChunkPos(x, z);

                        if (isChunkInEuclideanRange(x, z, lastViewableSectionPos.getX(), lastViewableSectionPos.getZ(), this.viewDistance) && !this.removedChunks.contains(chunkPos)) {
                            this.removedChunks.add(chunkPos);
                        }
                    }
                }

                for (var x = viewableSectionPos.getX() - this.viewDistance; x <= viewableSectionPos.getX() + this.viewDistance; ++x) {
                    for (var z = viewableSectionPos.getZ() - this.viewDistance; z <= viewableSectionPos.getZ() + this.viewDistance; ++z) {
                        final var chunkPos = new ChunkPos(x, z);

                        if (isChunkOnEuclideanBorder(x, z, viewableSectionPos.getX(), viewableSectionPos.getZ(), this.viewDistance) && !this.addedChunks.contains(chunkPos)) {
                            this.addedChunks.add(chunkPos);
                        }
                    }
                }
            }

            this.addedChunks.removeIf(this.inRangeChunks::contains);

            this.removedChunks.removeIf(this.addedChunks::contains);
            this.removedChunks.removeIf(this.inRangeChunks::contains);
        }

        this.addedChunks.forEach(chunkPos -> this.updateChunkTracking(serverPlayer, chunkPos, new MutableObject<>(), false, true));
        this.removedChunks.forEach(chunkPos -> this.updateChunkTracking(serverPlayer, chunkPos, new MutableObject<>(), true, false));
    }

    // endregion move

}
