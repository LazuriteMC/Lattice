package dev.lazurite.lattice.impl.mixin.common;

import com.mojang.datafixers.DataFixer;
import dev.lazurite.lattice.impl.common.IServerPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.LinkedHashSet;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin extends VersionedChunkStorage implements ChunkHolder.PlayersWatchingChunkProvider {

    @Unique private ChunkSectionPos currPlayerPos;
    @Unique private ChunkSectionPos prevPlayerPos;

    @Unique private ChunkSectionPos currCamPos;
    @Unique private ChunkSectionPos prevCamPos;

    @Unique private LinkedHashSet<ChunkPos> chunks;

    @Unique private LinkedHashSet<ChunkPos> addedChunks;
    @Unique private LinkedHashSet<ChunkPos> persistentChunks;
    @Unique private LinkedHashSet<ChunkPos> removedChunks;

    @Shadow @Final private ThreadedAnvilChunkStorage.TicketManager ticketManager;
    @Shadow private int watchDistance;
    @Shadow private static int getChebyshevDistance(ChunkPos pos, int x, int z) { return 0; }
    @Shadow protected abstract void sendWatchPackets(ServerPlayerEntity player, ChunkPos pos, Packet<?>[] packets, boolean withinMaxWatchDistance, boolean withinViewDistance);

    public ThreadedAnvilChunkStorageMixin(File file, DataFixer dataFixer, boolean bl) {
        super(file, dataFixer, bl);
    }

    // region getChebyshevDistance

    // TODO sus when useCameraPosition = true... look into

    @Redirect(
            method = "getChebyshevDistance(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/server/network/ServerPlayerEntity;Z)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getCameraPosition()Lnet/minecraft/util/math/ChunkSectionPos;"
            )
    )
    private static ChunkSectionPos getChebyshevDistance_getCameraPosition(ServerPlayerEntity player) {
        return ((IServerPlayerEntity) player).getPrevCameraChunkSectionPos();
    }

    @Redirect(
            method = "getChebyshevDistance(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/server/network/ServerPlayerEntity;Z)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getX()D"
            )
    )
    private static double getChebyshevDistance_getX(ServerPlayerEntity player) {
        return player.getCameraEntity().getX();
    }

    @Redirect(
            method = "getChebyshevDistance(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/server/network/ServerPlayerEntity;Z)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getZ()D"
            )
    )
    private static double getChebyshevDistance_getZ(ServerPlayerEntity player) {
        return player.getCameraEntity().getZ();
    }

    // endregion getChebyshevDistance

    // region method_20726

    @Inject(
            method = "method_20726",
            at = @At("HEAD")
    )
    private void method_20726_HEAD(ServerPlayerEntity serverPlayerEntity, CallbackInfoReturnable<ChunkSectionPos> cir) {
        ((IServerPlayerEntity) serverPlayerEntity).setPrevCameraChunkSectionPos(ChunkSectionPos.from(serverPlayerEntity.getCameraEntity()));
    }

    @Redirect(
            method = "method_20726",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/ChunkSectionPos;getSectionX()I"
            )
    )
    private int method_20726_getSectionX(ChunkSectionPos chunkSectionPos, ServerPlayerEntity serverPlayerEntity) {
        return ((IServerPlayerEntity) serverPlayerEntity).getPrevCameraChunkSectionPos().getX();
    }

    @Redirect(
            method = "method_20726",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/ChunkSectionPos;getSectionZ()I"
            )
    )
    private int method_20726_getSectionZ(ChunkSectionPos chunkSectionPos, ServerPlayerEntity serverPlayerEntity) {
        return ((IServerPlayerEntity) serverPlayerEntity).getPrevCameraChunkSectionPos().getZ();
    }

    // endregion method_20726

    // region handlePlayerAddedOrRemoved

    @Inject(
            method = "handlePlayerAddedOrRemoved",
            at = @At("HEAD")
    )
    void handlePlayerAddedOrRemoved_HEAD(ServerPlayerEntity player, boolean added, CallbackInfo ci) {
        this.currPlayerPos = ChunkSectionPos.from(player);
        this.prevPlayerPos = player.getCameraPosition();

        this.currCamPos = ChunkSectionPos.from(player.getCameraEntity());
        this.prevCamPos = ((IServerPlayerEntity) player).getPrevCameraChunkSectionPos();

        this.chunks = new LinkedHashSet<>();
    }

    @Redirect(
            method = "handlePlayerAddedOrRemoved",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage$TicketManager;handleChunkEnter(Lnet/minecraft/util/math/ChunkSectionPos;Lnet/minecraft/server/network/ServerPlayerEntity;)V"
            )
    )
    void handlePlayerAddedOrRemoved_handleChunkEnter(ThreadedAnvilChunkStorage.TicketManager ticketManager, ChunkSectionPos pos, ServerPlayerEntity player) {
        this.ticketManager.handleChunkEnter(pos, player);

        if (!this.currCamPos.equals(pos)) {
            this.ticketManager.handleChunkEnter(this.currCamPos, player);
        }
    }

    @Redirect(
            method = "handlePlayerAddedOrRemoved",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage$TicketManager;handleChunkLeave(Lnet/minecraft/util/math/ChunkSectionPos;Lnet/minecraft/server/network/ServerPlayerEntity;)V"
            )
    )
    void handlePlayerAddedOrRemoved_handleChunkLeave(ThreadedAnvilChunkStorage.TicketManager ticketManager, ChunkSectionPos pos, ServerPlayerEntity player) {
        this.ticketManager.handleChunkLeave(pos, player);

        if (!this.prevCamPos.equals(pos)) {
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
        this.chunks.add(pos);
    }

    @Inject(
            method = "handlePlayerAddedOrRemoved",
            at = @At("TAIL")
    )
    void handlePlayerAddedOrRemoved_TAIL(ServerPlayerEntity player, boolean added, CallbackInfo ci) {
        if (!this.currCamPos.equals(this.currPlayerPos)) {
            for (int x = this.currCamPos.getX() - this.watchDistance; x <= this.currCamPos.getX() + this.watchDistance; ++x) {
                for (int z = this.currCamPos.getZ() - this.watchDistance; z <= this.currCamPos.getZ() + this.watchDistance; ++z) {
                    this.chunks.add(new ChunkPos(x, z));
                }
            }
        }

        this.chunks.forEach(chunkPos -> this.sendWatchPackets(player, chunkPos, new Packet[2], !added, added));
    }

    // endregion handlePlayerAddedOrRemoved

    // region updateCameraPosition

    @Inject(
            method = "updateCameraPosition",
            at = @At("HEAD")
    )
    public void updateCameraPosition_HEAD(ServerPlayerEntity player, CallbackInfo ci) {
        this.currPlayerPos = ChunkSectionPos.from(player);
        this.prevPlayerPos = player.getCameraPosition();

        this.currCamPos = ChunkSectionPos.from(player.getCameraEntity());
        this.prevCamPos = ((IServerPlayerEntity) player).getPrevCameraChunkSectionPos();

        this.addedChunks = new LinkedHashSet<>(); // false, true
        this.persistentChunks = new LinkedHashSet<>(); // true, true
        this.removedChunks = new LinkedHashSet<>(); // true, false
    }

    // TODO make better

    @ModifyVariable(
            method = "updateCameraPosition",
            at = @At(
                    value = "LOAD",
                    ordinal = 0
            ),
            ordinal = 2
    )
    public boolean updateCameraPosition_LOAD(boolean bl3) {
        return bl3 || this.prevCamPos.asLong() != this.currCamPos.asLong();
    }

    @Redirect(
            method = "updateCameraPosition",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage$TicketManager;handleChunkLeave(Lnet/minecraft/util/math/ChunkSectionPos;Lnet/minecraft/server/network/ServerPlayerEntity;)V"
            )
    )
    public void updateCameraPosition_handleChunkLeave(ThreadedAnvilChunkStorage.TicketManager ticketManager, ChunkSectionPos pos, ServerPlayerEntity player) {
        if (pos.asLong() != this.currPlayerPos.asLong()) { // bl3
            this.ticketManager.handleChunkLeave(pos, player);
        }

        if (this.prevCamPos.asLong() != this.currCamPos.asLong() && !this.prevCamPos.equals(pos)) {
            this.ticketManager.handleChunkLeave(this.prevCamPos, player);
        }
    }

    @Redirect(
            method = "updateCameraPosition",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage$TicketManager;handleChunkEnter(Lnet/minecraft/util/math/ChunkSectionPos;Lnet/minecraft/server/network/ServerPlayerEntity;)V"
            )
    )
    public void updateCameraPosition_handleChunkEnter(ThreadedAnvilChunkStorage.TicketManager ticketManager, ChunkSectionPos pos, ServerPlayerEntity player) {
        if (pos.asLong() != this.prevPlayerPos.asLong()) { // bl3
            this.ticketManager.handleChunkEnter(pos, player);
        }

        if (this.currCamPos.asLong() != this.prevCamPos.asLong() && !this.currCamPos.equals(pos)) {
            this.ticketManager.handleChunkEnter(this.currCamPos, player);
        }
    }

    @Redirect(
            method = "updateCameraPosition",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage;sendWatchPackets(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/math/ChunkPos;[Lnet/minecraft/network/Packet;ZZ)V"
            )
    )
    public void updateCameraPosition_sendWatchPackets(ThreadedAnvilChunkStorage threadedAnvilChunkStorage, ServerPlayerEntity player, ChunkPos pos, Packet<?>[] packets, boolean withinMaxWatchDistance, boolean withinViewDistance) {
        if (withinMaxWatchDistance && withinViewDistance) {
            this.persistentChunks.add(pos);
        } else if (!withinMaxWatchDistance && withinViewDistance) {
            this.addedChunks.add(pos);
        } else if (withinMaxWatchDistance) { // withinMaxWatchDistance && !withinViewDistance
            this.removedChunks.add(pos);
        }
    }

    @Inject(
            method = "updateCameraPosition",
            at = @At("TAIL")
    )
    public void updateCameraPosition_TAIL(ServerPlayerEntity player, CallbackInfo ci) {
        if (!this.prevCamPos.equals(this.prevPlayerPos) || !this.currCamPos.equals(this.currPlayerPos)) {
            if (Math.abs(this.prevCamPos.getX() - this.currCamPos.getX()) <= this.watchDistance * 2 && Math.abs(this.prevCamPos.getZ() - this.currCamPos.getZ()) <= this.watchDistance * 2) {
                for (int x = Math.min(this.prevCamPos.getX(), this.currCamPos.getX()) - this.watchDistance; x <= Math.max(this.prevCamPos.getX(), this.currCamPos.getX() + this.watchDistance); ++x) {
                    for (int z = Math.min(this.prevCamPos.getZ(), this.currCamPos.getZ()) - this.watchDistance; z <= Math.max(this.prevCamPos.getZ(), this.currCamPos.getZ()) + this.watchDistance; ++z) {
                        ChunkPos chunkPos = new ChunkPos(x, z);

                        boolean withinMaxWatchDistance = getChebyshevDistance(chunkPos, this.prevCamPos.getX(), this.prevCamPos.getZ()) <= this.watchDistance;
                        boolean withinViewDistance = getChebyshevDistance(chunkPos, this.currCamPos.getX(), this.currCamPos.getZ()) <= this.watchDistance;

                        if (withinMaxWatchDistance && withinViewDistance) {
                            this.persistentChunks.add(chunkPos);
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

            this.removedChunks.removeIf(persistentChunks::contains);
        }

        this.addedChunks.forEach(chunkPos -> this.sendWatchPackets(player, chunkPos, new Packet[2], false, true));
        this.removedChunks.forEach(chunkPos -> this.sendWatchPackets(player, chunkPos, new Packet[2], true, false));
    }

    // endregion updateCameraPosition

}
