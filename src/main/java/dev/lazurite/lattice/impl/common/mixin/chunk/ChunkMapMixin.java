package dev.lazurite.lattice.impl.common.mixin.chunk;

import com.google.common.collect.ImmutableList;
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
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {

    /**
     * @see #updatePlayerPos_HEAD
     * @see #updatePlayerStatus_removePlayer
     * @see #move_removePlayer
     * @see #move_TAIL
     */
    private SectionPos
            lastLastSectionPos = SectionPos.of(0, 0, 0),
            lastLastViewableSectionPos = SectionPos.of(0, 0, 0);
    /**
     * @see #getPlayers_getLastSectionPos
     * @see #getPlayers_isChunkOnRangeBorder
     * @see #getPlayers_isChunkInRange
     */
    private ServerPlayer serverPlayer;

    /**
     * @see #updatePlayerStatus_HEAD
     * @see #updatePlayerStatus_updateChunkTracking
     * @see #updatePlayerStatus_TAIL
     */
    private final List<ChunkPos>
            queuedChunks = new ArrayList<>(),
            addedChunks = new ArrayList<>(),
            removedChunks = new ArrayList<>(),
            inRangeChunks = new ArrayList<>();

    @Shadow int viewDistance;
    @Shadow @Final private ChunkMap.DistanceManager distanceManager;
    @Shadow public static boolean isChunkInRange(int i, int j, int k, int l, int m) { return false; }
    @Shadow private static boolean isChunkOnRangeBorder(int i, int j, int k, int l, int m) { return false; }
    @Shadow protected abstract void updateChunkTracking(ServerPlayer serverPlayer, ChunkPos chunkPos, MutableObject<ClientboundLevelChunkWithLightPacket> mutableObject, boolean bl, boolean bl2);

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
        return Math.min(f, d - ((LatticePlayer) entity).getViewable().getViewableX());
    }

    @ModifyVariable(
            method = "euclideanDistanceSquared",
            at = @At("STORE"),
            ordinal = 3
    )
    private static double euclideanDistanceSquared_STORE3(double g, ChunkPos chunkPos, Entity entity) {
        double e = SectionPos.sectionToBlockCoord(chunkPos.z, 8);
        return Math.min(g, e - ((LatticePlayer) entity).getViewable().getViewableZ());
    }

    // endregion euclideanDistanceSquared

    // region setViewDistance

    @ModifyVariable(
            method = { "lambda$setViewDistance$41", "method_17219" }, // lambda refmap not generated
            at = @At("STORE"),
            ordinal = 0,
            require = 1
    )
    protected boolean lambda$setViewDistance$41_STORE0(boolean bl, ChunkPos chunkPos, int k, MutableObject<ClientboundLevelChunkWithLightPacket> mutableObject, ServerPlayer serverPlayer) {
        final var lastViewableSectionPos = ((ILatticePlayer) serverPlayer).getLastViewableSectionPos();
        return bl || isChunkInRange(chunkPos.x, chunkPos.z, lastViewableSectionPos.x(), lastViewableSectionPos.z(), k);
    }

    @ModifyVariable(
            method = { "lambda$setViewDistance$41", "method_17219" }, // lambda refmap not generated
            at = @At("STORE"),
            ordinal = 1,
            require = 1
    )
    protected boolean lambda$setViewDistance$41_STORE1(boolean bl, ChunkPos chunkPos, int k, MutableObject<ClientboundLevelChunkWithLightPacket> mutableObject, ServerPlayer serverPlayer) {
        final var lastViewableSectionPos = ((ILatticePlayer) serverPlayer).getLastViewableSectionPos();
        return bl || isChunkInRange(chunkPos.x, chunkPos.z, lastViewableSectionPos.x(), lastViewableSectionPos.z(), this.viewDistance);
    }

    // endregion setViewDistance

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
        final var viewable = ((LatticePlayer) serverPlayer).getViewable();

        if (!ChunkPosUtil.of(viewable).equals(ChunkPosUtil.of(serverPlayer))) {
            this.distanceManager.addPlayer(SectionPosUtil.of(viewable), serverPlayer);
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
        if (!this.lastLastViewableSectionPos.chunk().equals(this.lastLastSectionPos.chunk())) {
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
            for (var x = viewableSectionPosX - this.viewDistance - 1; x <= viewableSectionPosX + this.viewDistance + 1; ++x) {
                for (var z = viewableSectionPosZ - this.viewDistance - 1; z <= viewableSectionPosZ + this.viewDistance + 1; ++z) {
                    final var chunkPos = new ChunkPos(x, z);

                    if (isChunkInRange(x, z, viewableSectionPosX, viewableSectionPosZ, this.viewDistance) && !this.queuedChunks.contains(chunkPos)) {
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
        this.lastLastViewableSectionPos = player.getLastViewableSectionPos();

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

    // bl3 is no longer used after this point
    // if it were, I would have to set it back to its original value
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
        if (!this.lastLastViewableSectionPos.chunk().equals(this.lastLastSectionPos.chunk())) {
            this.distanceManager.removePlayer(this.lastLastViewableSectionPos, serverPlayer);
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
        final var viewable = ((LatticePlayer) serverPlayer).getViewable();

        if (!ChunkPosUtil.of(viewable).equals(ChunkPosUtil.of(serverPlayer))) {
            this.distanceManager.addPlayer(SectionPosUtil.of(viewable), serverPlayer);
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
        final var viewableSectionPos = SectionPosUtil.of(((LatticePlayer) serverPlayer).getViewable());
        final var serverPlayerSectionPos = SectionPos.of(serverPlayer);

        final var lastViewableSectionPos = this.lastLastViewableSectionPos;
        final var lastServerPlayerSectionPos = this.lastLastSectionPos;

        // if check not necessary I don't think but more efficient
        if (!viewableSectionPos.chunk().equals(serverPlayerSectionPos.chunk()) || !lastViewableSectionPos.chunk().equals(lastServerPlayerSectionPos.chunk())) {

            if (Math.abs(lastViewableSectionPos.getX() - viewableSectionPos.getX()) <= this.viewDistance * 2 && Math.abs(lastViewableSectionPos.getZ() - viewableSectionPos.getZ()) <= this.viewDistance * 2) {
                for (var x = Math.min(viewableSectionPos.getX(), lastViewableSectionPos.getX()) - this.viewDistance - 1; x <= Math.max(viewableSectionPos.getX(), lastViewableSectionPos.getX()) + this.viewDistance + 1; ++x) {
                    for (var z = Math.min(viewableSectionPos.getZ(), lastViewableSectionPos.getZ()) - this.viewDistance - 1; z <= Math.max(viewableSectionPos.getZ(), lastViewableSectionPos.getZ()) + this.viewDistance + 1; ++z) {
                        boolean bl = isChunkInRange(x, z, lastViewableSectionPos.x(), lastViewableSectionPos.z(), this.viewDistance);
                        boolean bl2 = isChunkInRange(x, z, viewableSectionPos.x(), viewableSectionPos.z(), this.viewDistance);

                        ChunkPos chunkPos = new ChunkPos(x, z);

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
                for (var x = lastViewableSectionPos.getX() - this.viewDistance - 1; x <= lastViewableSectionPos.getX() + this.viewDistance + 1; ++x) {
                    for (var z = lastViewableSectionPos.getZ() - this.viewDistance - 1; z <= lastViewableSectionPos.getZ() + this.viewDistance + 1; ++z) {
                        final var chunkPos = new ChunkPos(x, z);

                        if (isChunkInRange(x, z, lastViewableSectionPos.getX(), lastViewableSectionPos.getZ(), this.viewDistance) && !this.removedChunks.contains(chunkPos)) {
                            this.removedChunks.add(chunkPos);
                        }
                    }
                }

                for (var x = viewableSectionPos.getX() - this.viewDistance - 1; x <= viewableSectionPos.getX() + this.viewDistance + 1; ++x) {
                    for (var z = viewableSectionPos.getZ() - this.viewDistance - 1; z <= viewableSectionPos.getZ() + this.viewDistance + 1; ++z) {
                        final var chunkPos = new ChunkPos(x, z);

                        if (isChunkOnRangeBorder(x, z, viewableSectionPos.getX(), viewableSectionPos.getZ(), this.viewDistance) && !this.addedChunks.contains(chunkPos)) {
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

    // region getPlayers

    @Inject(
            method = "getPlayers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;getLastSectionPos()Lnet/minecraft/core/SectionPos;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void getPlayers_getLastSectionPos(ChunkPos chunkPos, boolean bl, CallbackInfoReturnable<List<ServerPlayer>> cir, Set<ServerPlayer> set, ImmutableList.Builder<ServerPlayer> builder, Iterator<ServerPlayer> var5, ServerPlayer serverPlayer) {
        this.serverPlayer = serverPlayer;
    }

    @Redirect(
            method = "getPlayers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap;isChunkOnRangeBorder(IIIII)Z"
            )
    )
    public boolean getPlayers_isChunkOnRangeBorder(int i, int j, int k, int l, int m) {
        final var lastViewableSectionPos = ((ILatticePlayer) this.serverPlayer).getLastViewableSectionPos();
        return isChunkOnRangeBorder(i, j, k, l, m) || isChunkOnRangeBorder(i, j, lastViewableSectionPos.x(), lastViewableSectionPos.z(), m);
    }

    @Redirect(
            method = "getPlayers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap;isChunkInRange(IIIII)Z"
            )
    )
    public boolean getPlayers_isChunkInRange(int i, int j, int k, int l, int m) {
        final var lastViewableSectionPos = ((ILatticePlayer) this.serverPlayer).getLastViewableSectionPos();
        return isChunkInRange(i, j, k, l, m) || isChunkInRange(i, j, lastViewableSectionPos.x(), lastViewableSectionPos.z(), m);
    }

    // endregion getPlayers

}
