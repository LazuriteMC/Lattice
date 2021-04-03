package dev.lazurite.lattice.impl.mixin.common;

import com.mojang.datafixers.DataFixer;
import dev.lazurite.lattice.impl.common.IServerPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.io.File;
import java.util.LinkedHashSet;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin extends VersionedChunkStorage implements ChunkHolder.PlayersWatchingChunkProvider {

    @Unique private int cameraChunkSectionPosX;
    @Unique private int cameraCHunkSectionPosZ;

    @Unique private ChunkSectionPos currentPlayerChunkSectionPos;
    @Unique private ChunkSectionPos prevPlayerChunkSectionPos;

    @Unique private ChunkSectionPos currentCameraChunkSectionPos;
    @Unique private ChunkSectionPos prevCameraChunkSectionPos;

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
        return ((IServerPlayerEntity) serverPlayerEntity).getPrevCameraChunkSectionPos().getSectionX();
    }

    @Redirect(
            method = "method_20726",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/ChunkSectionPos;getSectionZ()I"
            )
    )
    private int method_20726_getSectionZ(ChunkSectionPos chunkSectionPos, ServerPlayerEntity serverPlayerEntity) {
        return ((IServerPlayerEntity) serverPlayerEntity).getPrevCameraChunkSectionPos().getSectionZ();
    }

    // endregion method_20726

    // region handlePlayerAddedOrRemoved

    /* should keep?

    @Redirect(
            method = "handlePlayerAddedOrRemoved",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getX()D"
            )
    )
    double handlePlayerAddedOrRemoved_getX(ServerPlayerEntity player) {
        return player.getCameraEntity().getX();
    }

    @Redirect(
            method = "handlePlayerAddedOrRemoved",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getZ()D"
            )
    )
    double handlePlayerAddedOrRemoved_getZ(ServerPlayerEntity player) {
        return player.getCameraEntity().getZ();
    }

    @ModifyArg(
            method = "handlePlayerAddedOrRemoved",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/ChunkSectionPos;from(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/ChunkSectionPos;"
            )
    )
    Entity handlePlayerAddedOrRemoved_from(Entity player) {
        return ((ServerPlayerEntity) player).getCameraEntity();
    }

    */

    // endregion handlePlayerAddedOrRemoved

    // region updateCameraPosition

    @Inject(
            method = "updateCameraPosition",
            at = @At("HEAD")
    )
    public void updateCameraPosition_HEAD(ServerPlayerEntity player, CallbackInfo ci) {
        this.cameraChunkSectionPosX = MathHelper.floor(player.getCameraEntity().getX()) >> 4;
        this.cameraCHunkSectionPosZ = MathHelper.floor(player.getCameraEntity().getZ()) >> 4;

        this.currentPlayerChunkSectionPos = ChunkSectionPos.from(player);
        this.prevPlayerChunkSectionPos = player.getCameraPosition();

        this.currentCameraChunkSectionPos = ChunkSectionPos.from(player.getCameraEntity());
        this.prevCameraChunkSectionPos = ((IServerPlayerEntity) player).getPrevCameraChunkSectionPos();

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
        return bl3 || this.prevCameraChunkSectionPos.asLong() != this.currentCameraChunkSectionPos.asLong();
    }

    @Redirect(
            method = "updateCameraPosition",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage$TicketManager;handleChunkLeave(Lnet/minecraft/util/math/ChunkSectionPos;Lnet/minecraft/server/network/ServerPlayerEntity;)V"
            )
    )
    public void updateCameraPosition_handleChunkLeave(ThreadedAnvilChunkStorage.TicketManager ticketManager, ChunkSectionPos pos, ServerPlayerEntity player) {
        if (pos.asLong() != this.currentPlayerChunkSectionPos.asLong()) {
            this.ticketManager.handleChunkLeave(pos, player);
        }

        if (this.prevCameraChunkSectionPos.asLong() != this.currentCameraChunkSectionPos.asLong() && !(this.prevCameraChunkSectionPos.equals(pos))) {
            this.ticketManager.handleChunkLeave(prevCameraChunkSectionPos, player);
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
        if (pos.asLong() != this.prevPlayerChunkSectionPos.asLong()) {
            this.ticketManager.handleChunkEnter(pos, player);
        }

        if (this.currentCameraChunkSectionPos.asLong() != this.prevCameraChunkSectionPos.asLong() && !(this.currentCameraChunkSectionPos.equals(pos))) {
            this.ticketManager.handleChunkEnter(this.currentCameraChunkSectionPos, player);
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

    // TODO clean up

    @Inject(
            method = "updateCameraPosition",
            at = @At("TAIL")
    )
    public void updateCameraPosition_TAIL(ServerPlayerEntity player, CallbackInfo ci) {
        if (this.prevCameraChunkSectionPos.asLong() != this.currentCameraChunkSectionPos.asLong() && (!this.prevCameraChunkSectionPos.equals(this.prevPlayerChunkSectionPos) || !this.currentCameraChunkSectionPos.equals(this.currentPlayerChunkSectionPos))) {
            int i = this.cameraChunkSectionPosX;
            int j = this.cameraCHunkSectionPosZ;
            int k = this.prevCameraChunkSectionPos.getSectionX();
            int n = this.prevCameraChunkSectionPos.getSectionZ();
            int w;
            int x;
            if (Math.abs(k - i) <= this.watchDistance * 2 && Math.abs(n - j) <= this.watchDistance * 2) {
                w = Math.min(i, k) - this.watchDistance;
                x = Math.min(j, n) - this.watchDistance;
                int q = Math.max(i, k) + this.watchDistance;
                int r = Math.max(j, n) + this.watchDistance;

                for(int s = w; s <= q; ++s) {
                    for(int t = x; t <= r; ++t) {
                        ChunkPos chunkPos = new ChunkPos(s, t);
                        boolean bl4 = getChebyshevDistance(chunkPos, k, n) <= this.watchDistance;
                        boolean bl5 = getChebyshevDistance(chunkPos, i, j) <= this.watchDistance;
                        if (bl4 && bl5) {
                            this.persistentChunks.add(chunkPos);
                        } else if (!bl4 && bl5) {
                            this.addedChunks.add(chunkPos);
                        } else if (bl4 && !bl5) {
                            this.removedChunks.add(chunkPos);
                        }
//                        this.sendWatchPackets(player, chunkPos, new Packet[2], bl4, bl5);
                    }
                }
            } else {
                ChunkPos chunkPos3;
                for(w = k - this.watchDistance; w <= k + this.watchDistance; ++w) {
                    for(x = n - this.watchDistance; x <= n + this.watchDistance; ++x) {
                        chunkPos3 = new ChunkPos(w, x);
                        this.removedChunks.add(chunkPos3);
//                        this.sendWatchPackets(player, chunkPos3, new Packet[2], true, false);
                    }
                }

                for(w = i - this.watchDistance; w <= i + this.watchDistance; ++w) {
                    for(x = j - this.watchDistance; x <= j + this.watchDistance; ++x) {
                        chunkPos3 = new ChunkPos(w, x);
                        this.addedChunks.add(chunkPos3);
//                        this.sendWatchPackets(player, chunkPos3, new Packet[2], false, true);
                    }
                }
            }

            removedChunks.removeIf(persistentChunks::contains);
        }

        addedChunks.forEach(chunkPos -> this.sendWatchPackets(player, chunkPos, new Packet[2], false, true));
        removedChunks.forEach(chunkPos -> this.sendWatchPackets(player, chunkPos, new Packet[2], true, false));
    }

    /*
    @Overwrite
    public void updateCameraPosition(ServerPlayerEntity player) {
        ObjectIterator<ThreadedAnvilChunkStorage.EntityTracker> var2 = this.entityTrackers.values().iterator();

        while (var2.hasNext()) {
            ThreadedAnvilChunkStorage.EntityTracker entityTracker = var2.next();
            if (((IThreadedAnvilChunkStorage.IEntityTracker) entityTracker).getEntity() == player) {
                entityTracker.updateCameraPosition(this.world.getPlayers());
            } else {
                entityTracker.updateCameraPosition(player);
            }
        }

        int i = MathHelper.floor(player.getX()) >> 4;
        int j = MathHelper.floor(player.getZ()) >> 4;

        int i2 = MathHelper.floor(player.getCameraEntity().getX()) >> 4;
        int j2 = MathHelper.floor(player.getCameraEntity().getZ()) >> 4;

        ChunkSectionPos chunkSectionPos = player.getCameraPosition();
        ChunkSectionPos chunkSectionPos2 = ChunkSectionPos.from(player);

        ChunkSectionPos prevCameraChunkSectionPos = ((IServerPlayerEntity) player).getPrevCameraChunkSectionPos();
        ChunkSectionPos currentCameraChunkSectionPos = ChunkSectionPos.from(player.getCameraEntity());

        boolean bl = this.playerChunkWatchingManager.isWatchDisabled(player);
        boolean bl2 = this.doesNotGenerateChunks(player);

        boolean bl3 = chunkSectionPos.asLong() != chunkSectionPos2.asLong();

        boolean cameraEntityMovedChunkSection = prevCameraChunkSectionPos.asLong() != currentCameraChunkSectionPos.asLong();

        LinkedHashSet<ChunkPos> addedChunks = new LinkedHashSet<>((int) Math.ceil(Math.pow(this.watchDistance, 2) + 1), 1.0f); // false, true
        LinkedHashSet<ChunkPos> removedChunks = new LinkedHashSet<>((int) Math.ceil(Math.pow(this.watchDistance, 2) + 1), 1.0f); // true, false

        if (bl3 || cameraEntityMovedChunkSection || bl != bl2) { // <- Redirect
            this.method_20726(player);

            if (!bl) {

                // Redirect

                if (bl3) {
                    this.ticketManager.handleChunkLeave(chunkSectionPos, player);
                }

                if (cameraEntityMovedChunkSection && !prevCameraChunkSectionPos.equals(chunkSectionPos)) {
                    this.ticketManager.handleChunkLeave(prevCameraChunkSectionPos, player);
                }
            }

            if (!bl2) {
                // Redirect

                if (bl3) {
                    this.ticketManager.handleChunkEnter(chunkSectionPos2, player);
                }

                if (cameraEntityMovedChunkSection && !currentCameraChunkSectionPos.equals(chunkSectionPos2)) {
                    this.ticketManager.handleChunkEnter(currentCameraChunkSectionPos, player);
                }
            }

            if (!bl && bl2) {
                this.playerChunkWatchingManager.disableWatch(player);
            }

            if (bl && !bl2) {
                this.playerChunkWatchingManager.enableWatch(player);
            }
        }

        if (true) {
            int k = chunkSectionPos.getSectionX();
            int n = chunkSectionPos.getSectionZ();
            int w;
            int x;
            if (Math.abs(k - i) <= this.watchDistance * 2 && Math.abs(n - j) <= this.watchDistance * 2) {
                w = Math.min(i, k) - this.watchDistance;
                x = Math.min(j, n) - this.watchDistance;
                int q = Math.max(i, k) + this.watchDistance;
                int r = Math.max(j, n) + this.watchDistance;

                for(int s = w; s <= q; ++s) {
                    for(int t = x; t <= r; ++t) {
                        ChunkPos chunkPos = new ChunkPos(s, t);
                        boolean bl4 = getChebyshevDistance(chunkPos, k, n) <= this.watchDistance;
                        boolean bl5 = getChebyshevDistance(chunkPos, i, j) <= this.watchDistance;

                        // Redirect
//                        this.sendWatchPackets(player, chunkPos, new Packet[2], bl4, bl5);

                        if (!bl4 && bl5 || bl4 && bl5) {
                            addedChunks.add(chunkPos);
                        } else if (bl4 && !bl5) {
                            removedChunks.add(chunkPos);
                        }
                    }
                }
            } else {
                ChunkPos chunkPos3;
                for(w = k - this.watchDistance; w <= k + this.watchDistance; ++w) {
                    for(x = n - this.watchDistance; x <= n + this.watchDistance; ++x) {
                        chunkPos3 = new ChunkPos(w, x);

                        // Redirect
//                        this.sendWatchPackets(player, chunkPos3, new Packet[2], true, false);

                        removedChunks.add(chunkPos3);
                    }
                }

                for(w = i - this.watchDistance; w <= i + this.watchDistance; ++w) {
                    for(x = j - this.watchDistance; x <= j + this.watchDistance; ++x) {
                        chunkPos3 = new ChunkPos(w, x);

                        // Redirect
//                            this.sendWatchPackets(player, chunkPos3, new Packet[2], false, true);

                        addedChunks.add(chunkPos3);
                    }
                }
            }
        }

        // Add
        if (cameraEntityMovedChunkSection && (!prevCameraChunkSectionPos.equals(chunkSectionPos) || !currentCameraChunkSectionPos.equals(chunkSectionPos2))) {
            int k = prevCameraChunkSectionPos.getSectionX();
            int n = prevCameraChunkSectionPos.getSectionZ();
            int w;
            int x;
            if (Math.abs(k - i2) <= this.watchDistance * 2 && Math.abs(n - j2) <= this.watchDistance * 2) {
                w = Math.min(i2, k) - this.watchDistance;
                x = Math.min(j2, n) - this.watchDistance;
                int q = Math.max(i2, k) + this.watchDistance;
                int r = Math.max(j2, n) + this.watchDistance;

                for(int s = w; s <= q; ++s) {
                    for(int t = x; t <= r; ++t) {
                        ChunkPos chunkPos = new ChunkPos(s, t);
                        boolean bl4 = getChebyshevDistance(chunkPos, k, n) <= this.watchDistance;
                        boolean bl5 = getChebyshevDistance(chunkPos, i2, j2) <= this.watchDistance;
                        if (!bl4 && bl5 || bl4 && bl5) {
                            addedChunks.add(chunkPos);
                        } else if (bl4 && !bl5) {
                            removedChunks.add(chunkPos);
                        }
//                            this.sendWatchPackets(player, chunkPos, new Packet[2], bl4, bl5);
                    }
                }
            } else {
                ChunkPos chunkPos3;
                for(w = k - this.watchDistance; w <= k + this.watchDistance; ++w) {
                    for(x = n - this.watchDistance; x <= n + this.watchDistance; ++x) {
                        chunkPos3 = new ChunkPos(w, x);
                        removedChunks.add(chunkPos3);
//                            this.sendWatchPackets(player, chunkPos3, new Packet[2], true, false);
                    }
                }

                for(w = i2 - this.watchDistance; w <= i2 + this.watchDistance; ++w) {
                    for(x = j2 - this.watchDistance; x <= j2 + this.watchDistance; ++x) {
                        chunkPos3 = new ChunkPos(w, x);
                        addedChunks.add(chunkPos3);
//                            this.sendWatchPackets(player, chunkPos3, new Packet[2], false, true);
                    }
                }
            }

            removedChunks.removeIf(addedChunks::contains);
        }

        addedChunks.forEach(chunkPos -> this.sendWatchPackets(player, chunkPos, new Packet[2], false, true));
        removedChunks.forEach(chunkPos -> this.sendWatchPackets(player, chunkPos, new Packet[2], true, false));

    }

    */


    // endregion updateCameraPosition

}
