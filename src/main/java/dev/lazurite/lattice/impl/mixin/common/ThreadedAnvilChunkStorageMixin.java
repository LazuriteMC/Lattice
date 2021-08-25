package dev.lazurite.lattice.impl.mixin.common;

import dev.lazurite.lattice.impl.util.duck.IServerPlayerEntity;
import dev.lazurite.lattice.impl.util.ChebyshevDistance;
import dev.lazurite.lattice.impl.mixin.common.access.IThreadedAnvilChunkStorageMixin;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedHashSet;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin {

    @Unique private ChunkSectionPos prevPlayerPos;
    @Unique private ChunkSectionPos currPlayerPos;

    @Unique private ChunkSectionPos prevCamPos;
    @Unique private ChunkSectionPos currCamPos;

    @Unique private final LinkedHashSet<ChunkPos> addedChunks = new LinkedHashSet<>();
    @Unique private final LinkedHashSet<ChunkPos> removedChunks = new LinkedHashSet<>();
    @Unique private final LinkedHashSet<ChunkPos> otherChunks = new LinkedHashSet<>();

    @Shadow private int watchDistance;
    @Shadow @Final private ThreadedAnvilChunkStorage.TicketManager ticketManager;
    @Shadow protected abstract void sendWatchPackets(ServerPlayerEntity player, ChunkPos pos, Packet<?>[] packets, boolean withinMaxWatchDistance, boolean withinViewDistance);

    // region getSquaredDistance

    @ModifyVariable(
            method = "getSquaredDistance",
            at = @At("STORE"),
            ordinal = 2
    )
    private static double getSquaredDistance_STORE0(double f, ChunkPos pos, Entity entity) {
        if (entity instanceof ServerPlayerEntity) {
            double d = ChunkSectionPos.getOffsetPos(pos.x, 8);
            return Math.min(f, d - ((ServerPlayerEntity) entity).getCameraEntity().getX());
        }

        return f;
    }

    @ModifyVariable(
            method = "getSquaredDistance",
            at = @At("STORE"),
            ordinal = 3
    )
    private static double getSquaredDistance_STORE1(double g, ChunkPos pos, Entity entity) {
        if (entity instanceof ServerPlayerEntity) {
            double e = ChunkSectionPos.getOffsetPos(pos.z, 8);
            return Math.min(g, e - ((ServerPlayerEntity) entity).getCameraEntity().getZ());
        }

        return g;
    }

    // endregion getSquaredDistance

    // region setViewDistance

    @ModifyVariable(
            method = "method_17219",
            at = @At("STORE"),
            ordinal = 0,
            remap = false
    )
    private boolean method_17219_STORE0(boolean bl, ChunkPos chunkPos, int j, Packet<?>[] packets, ServerPlayerEntity serverPlayerEntity) {
        return bl || ChebyshevDistance.fromCameraEntity(chunkPos, serverPlayerEntity, true) <= j;
    }

    @ModifyVariable(
            method = "method_17219",
            at = @At("STORE"),
            ordinal = 1,
            remap = false
    )
    private boolean method_17219_STORE1(boolean bl2, ChunkPos chunkPos, int j, Packet<?>[] packets, ServerPlayerEntity serverPlayerEntity) {
        return bl2 || ChebyshevDistance.fromCameraEntity(chunkPos, serverPlayerEntity, true) <= this.watchDistance;
    }

    // endregion setViewDistance

    // region handlePlayerAddedOrRemoved

    @Inject(
            method = "handlePlayerAddedOrRemoved",
            at = @At("HEAD")
    )
    void handlePlayerAddedOrRemoved_HEAD(ServerPlayerEntity player, boolean added, CallbackInfo ci) {
        this.prevPlayerPos = player.getWatchedSection();
        this.currPlayerPos = ChunkSectionPos.from(player);

        this.prevCamPos = ((IServerPlayerEntity) player).getPrevCamPos();
        this.currCamPos = ChunkSectionPos.from(player.getCameraEntity());

        this.otherChunks.clear();
    }

    @Inject(
            method = "handlePlayerAddedOrRemoved",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage$TicketManager;handleChunkEnter(Lnet/minecraft/util/math/ChunkSectionPos;Lnet/minecraft/server/network/ServerPlayerEntity;)V",
                    shift = At.Shift.AFTER
            )
    )
    void handlePlayerAddedOrRemoved_handleChunkEnter(ServerPlayerEntity player, boolean added, CallbackInfo ci) {
        if (!this.currCamPos.toChunkPos().equals(this.currPlayerPos.toChunkPos())) {
            this.ticketManager.handleChunkEnter(this.currCamPos, player);
        }
    }

    @Inject(
            method = "handlePlayerAddedOrRemoved",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage$TicketManager;handleChunkLeave(Lnet/minecraft/util/math/ChunkSectionPos;Lnet/minecraft/server/network/ServerPlayerEntity;)V",
                    shift = At.Shift.AFTER
            )
    )
    void handlePlayerAddedOrRemoved_handleChunkLeave(ServerPlayerEntity player, boolean added, CallbackInfo ci) {
        if (!this.prevCamPos.toChunkPos().equals(this.prevPlayerPos.toChunkPos())) {
            this.ticketManager.handleChunkLeave(this.prevCamPos, player);
        }
    }


    @Redirect(
            method = "handlePlayerAddedOrRemoved",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage;sendWatchPackets(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/math/ChunkPos;[Lnet/minecraft/network/Packet;ZZ)V"
            )
    )
    void handlePlayerAddedOrRemoved_sendWatchPackets(ThreadedAnvilChunkStorage threadedAnvilChunkStorage, ServerPlayerEntity player, ChunkPos pos, Packet<?>[] packets, boolean withinMaxWatchDistance, boolean withinViewDistance) {
        this.otherChunks.add(pos);
    }

    @Inject(
            method = "handlePlayerAddedOrRemoved",
            at = @At("TAIL")
    )
    void handlePlayerAddedOrRemoved_TAIL(ServerPlayerEntity player, boolean added, CallbackInfo ci) {
        if (!this.currCamPos.toChunkPos().equals(this.currPlayerPos.toChunkPos())) {
            for (int x = this.currCamPos.getX() - this.watchDistance; x <= this.currCamPos.getX() + this.watchDistance; ++x) {
                for (int z = this.currCamPos.getZ() - this.watchDistance; z <= this.currCamPos.getZ() + this.watchDistance; ++z) {
                    this.otherChunks.add(new ChunkPos(x, z));
                }
            }
        }

        this.otherChunks.forEach(chunkPos -> this.sendWatchPackets(player, chunkPos, new Packet[2], !added, added));
    }

    // endregion handlePlayerAddedOrRemoved

    // region updateWatchedSection

    @Inject(
            method = "updateWatchedSection",
            at = @At("HEAD")
    )
    private void updateWatchedSection_HEAD(ServerPlayerEntity serverPlayerEntity, CallbackInfoReturnable<ChunkSectionPos> cir) {
        ((IServerPlayerEntity) serverPlayerEntity).setPrevCamPos(ChunkSectionPos.from(serverPlayerEntity.getCameraEntity()));
    }

    @Redirect(
            method = "updateWatchedSection",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/ChunkSectionPos;getSectionX()I"
            )
    )
    private int updateWatchedSection_getSectionX(ChunkSectionPos chunkSectionPos, ServerPlayerEntity serverPlayerEntity) {
        return ((IServerPlayerEntity) serverPlayerEntity).getPrevCamPos().getX();
    }

    @Redirect(
            method = "updateWatchedSection",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/ChunkSectionPos;getSectionZ()I"
            )
    )
    private int updateWatchedSection_getSectionZ(ChunkSectionPos chunkSectionPos, ServerPlayerEntity serverPlayerEntity) {
        return ((IServerPlayerEntity) serverPlayerEntity).getPrevCamPos().getZ();
    }

    // endregion updateWatchedSection

    // region updatePosition

    @Inject(
            method = "updatePosition",
            at = @At("HEAD")
    )
    public void updateCameraPosition_HEAD(ServerPlayerEntity player, CallbackInfo ci) {
        this.prevPlayerPos = player.getWatchedSection();
        this.currPlayerPos = ChunkSectionPos.from(player);

        this.prevCamPos = ((IServerPlayerEntity) player).getPrevCamPos();
        this.currCamPos = ChunkSectionPos.from(player.getCameraEntity());

        this.addedChunks.clear();
        this.removedChunks.clear();
        this.otherChunks.clear();
    }

    @ModifyVariable(
            method = "updatePosition",
            at = @At(
                    value = "LOAD",
                    ordinal = 0 // bug with this being applied to 2 different booleans despite using ordinal of 2; target first instruction
            ),
            ordinal = 2
    )
    public boolean updateCameraPosition_LOAD(boolean bl3) {
        return bl3 || this.prevCamPos.asLong() != this.currCamPos.asLong();
    }

    @Inject(

            method = "updatePosition",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage$TicketManager;handleChunkLeave(Lnet/minecraft/util/math/ChunkSectionPos;Lnet/minecraft/server/network/ServerPlayerEntity;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void updateCameraPosition_handleChunkLeave(ServerPlayerEntity player, CallbackInfo ci) {
        if (!this.prevCamPos.toChunkPos().equals(this.prevPlayerPos.toChunkPos())) {
            this.ticketManager.handleChunkLeave(this.prevCamPos, player);
        }
    }

    @Inject(
            method = "updatePosition",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage$TicketManager;handleChunkEnter(Lnet/minecraft/util/math/ChunkSectionPos;Lnet/minecraft/server/network/ServerPlayerEntity;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void updateCameraPosition_handleChunkEnter(ServerPlayerEntity player, CallbackInfo ci) {
        if (!this.currCamPos.toChunkPos().equals(this.currPlayerPos.toChunkPos())) {
            this.ticketManager.handleChunkEnter(this.currCamPos, player);
        }
    }

    @Redirect(
            method = "updatePosition",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage;sendWatchPackets(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/math/ChunkPos;[Lnet/minecraft/network/Packet;ZZ)V"
            )
    )
    public void updateCameraPosition_sendWatchPackets(ThreadedAnvilChunkStorage threadedAnvilChunkStorage, ServerPlayerEntity player, ChunkPos pos, Packet<?>[] packets, boolean withinMaxWatchDistance, boolean withinViewDistance) {
        if (withinMaxWatchDistance && withinViewDistance) {
            this.otherChunks.add(pos);
        } else if (!withinMaxWatchDistance && withinViewDistance) {
            this.addedChunks.add(pos);
        } else if (withinMaxWatchDistance) { // withinMaxWatchDistance && !withinViewDistance
            this.removedChunks.add(pos);
        }
    }

    @Inject(
            method = "updatePosition",
            at = @At("TAIL")
    )
    public void updateCameraPosition_TAIL(ServerPlayerEntity player, CallbackInfo ci) {
        if (!this.prevCamPos.toChunkPos().equals(this.prevPlayerPos.toChunkPos()) || !this.currCamPos.toChunkPos().equals(this.currPlayerPos.toChunkPos())) {
            if (Math.abs(this.prevCamPos.getX() - this.currCamPos.getX()) <= this.watchDistance * 2 && Math.abs(this.prevCamPos.getZ() - this.currCamPos.getZ()) <= this.watchDistance * 2) {
                for (int x = Math.min(this.prevCamPos.getX(), this.currCamPos.getX()) - this.watchDistance; x <= Math.max(this.prevCamPos.getX(), this.currCamPos.getX()) + this.watchDistance; ++x) {
                    for (int z = Math.min(this.prevCamPos.getZ(), this.currCamPos.getZ()) - this.watchDistance; z <= Math.max(this.prevCamPos.getZ(), this.currCamPos.getZ()) + this.watchDistance; ++z) {
                        ChunkPos chunkPos = new ChunkPos(x, z);

                        boolean withinMaxWatchDistance = IThreadedAnvilChunkStorageMixin.callGetChebyshevDistance(chunkPos, this.prevCamPos.getX(), this.prevCamPos.getZ()) <= this.watchDistance;
                        boolean withinViewDistance = IThreadedAnvilChunkStorageMixin.callGetChebyshevDistance(chunkPos, this.currCamPos.getX(), this.currCamPos.getZ()) <= this.watchDistance;

                        if (withinMaxWatchDistance && withinViewDistance) {
                            this.otherChunks.add(chunkPos);
                        } else if (!withinMaxWatchDistance && withinViewDistance) {
                            this.addedChunks.add(chunkPos);
                        } else if (withinMaxWatchDistance) { // withinMaxWatchDistance && !withinViewDistance
                            this.removedChunks.add(chunkPos);
                        }
                    }
                }
            } else {
                for (int x = this.prevCamPos.getX() - this.watchDistance; x <= this.prevCamPos.getX() + this.watchDistance; ++x) {
                    for (int z = this.prevCamPos.getZ() - this.watchDistance; z <= this.prevCamPos.getZ() + this.watchDistance; ++z) {
                        this.removedChunks.add(new ChunkPos(x, z));
                    }
                }

                for (int x = this.currCamPos.getX() - this.watchDistance; x <= this.currCamPos.getX() + this.watchDistance; ++x) {
                    for (int z = this.currCamPos.getZ() - this.watchDistance; z <= this.currCamPos.getZ() + this.watchDistance; ++z) {
                        this.addedChunks.add(new ChunkPos(x, z));
                    }
                }
            }

            this.addedChunks.removeIf(otherChunks::contains);
            this.removedChunks.removeIf(otherChunks::contains);
        }

        this.addedChunks.forEach(chunkPos -> this.sendWatchPackets(player, chunkPos, new Packet[2], false, true));
        this.removedChunks.forEach(chunkPos -> this.sendWatchPackets(player, chunkPos, new Packet[2], true, false));
    }

    // endregion updatePosition

    // region getPlayersWatchingChunk

    @Inject(
            method = "method_18707",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void method_18707(ChunkPos chunkPos, boolean onlyOnWatchDistanceEdge, ServerPlayerEntity serverPlayerEntity, CallbackInfoReturnable<Boolean> cir) {
        int i = ChebyshevDistance.fromServerPlayerEntity(chunkPos, serverPlayerEntity, true);
        int i2 = ChebyshevDistance.fromCameraEntity(chunkPos, serverPlayerEntity, true);

        if (i > this.watchDistance && i2 > this.watchDistance) {
            cir.setReturnValue(false);
        } else {
            cir.setReturnValue(!onlyOnWatchDistanceEdge || i == this.watchDistance || i2 == this.watchDistance);
        }
    }

    // endregion getPlayersWatchingChunk

}
