package dev.lazurite.lattice.impl.mixin.core.level;

import dev.lazurite.lattice.api.supplier.ChunkPosSupplier;
import dev.lazurite.lattice.api.point.ViewPoint;
import dev.lazurite.lattice.impl.ChunkPosSupplierWrapperImpl;
import dev.lazurite.lattice.impl.api.ChunkPosSupplierWrapper;
import dev.lazurite.lattice.impl.api.level.InternalLatticeServerLevel;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Set;
import java.util.stream.Collectors;

// TODO: Verify this works.
//  ServerPlayer from a different ServerLevel should cause error.
//  Too many new objects being created.

@Mixin(ServerLevel.class)
@SuppressWarnings("UnstableApiUsage")
public abstract class ServerLevelMixin implements InternalLatticeServerLevel {

    final MutableGraph<ChunkPosSupplier> chunkPosSuppliers = GraphBuilder.directed()
            .allowsSelfLoops(true)
            .incidentEdgeOrder(ElementOrder.stable())
            .build();

    @Override
    public void register(final ChunkPosSupplier chunkPosSupplier) {
        if (chunkPosSupplier instanceof Player) return;

        this.chunkPosSuppliers.addNode(new ChunkPosSupplierWrapperImpl(chunkPosSupplier, (ServerLevel) (Object) this));
    }

    @Override
    public void unregister(final ChunkPosSupplier chunkPosSupplier) {
        if (chunkPosSupplier instanceof Player) return;

        final var chunkPosSupplierWrapper = new ChunkPosSupplierWrapperImpl(chunkPosSupplier, (ServerLevel) (Object) this);

        if (chunkPosSupplier instanceof ViewPoint) {
            // update any viewing ServerPlayer's edge to be self-referential
            this.chunkPosSuppliers.predecessors(chunkPosSupplierWrapper).forEach(serverPlayer -> {
//                final var chunkPosSupplierWrapper = this.getChunkPosSupplierWrapper((ServerPlayer) serverPlayer);
//                this.chunkPosSuppliers.putEdge(chunkPosSupplierWrapper, chunkPosSupplierWrapper);
                this.chunkPosSuppliers.putEdge(serverPlayer, serverPlayer);
            });
        }

        this.chunkPosSuppliers.removeNode(chunkPosSupplierWrapper); // also removes edges
    }

    @Override
    public Set<ChunkPosSupplier> getAllChunkPosSuppliers() {
        return this.chunkPosSuppliers.nodes();
    }

    @Override
    public void bind(final ServerPlayer serverPlayer, final ViewPoint viewPoint) {
        this.unbind(serverPlayer);

        this.chunkPosSuppliers.putEdge(
                this.getChunkPosSupplierWrapper(serverPlayer),
                new ChunkPosSupplierWrapperImpl(viewPoint, (ServerLevel) (Object) this)
        );
    }

    @Override
    public void unbind(final ServerPlayer serverPlayer) {
        final var chunkPosSupplierWrapper = new ChunkPosSupplierWrapperImpl((ChunkPosSupplier) serverPlayer, (ServerLevel) (Object) this);

        this.chunkPosSuppliers.successors(chunkPosSupplierWrapper).stream()
                .filter(viewPoint -> !(viewPoint instanceof ServerPlayer)) // remove ServerPlayers
                .filter(viewPoint -> this.chunkPosSuppliers.inDegree(viewPoint) == 0) // get ViewPoints with no viewers
                .map(viewPoint -> (ViewPoint) viewPoint) // cast to ViewPoint
                .filter(ViewPoint::unregistersWithNoViewers) // get ViewPoints to be unregistered
                .forEach(this.chunkPosSuppliers::removeNode); // remove them (also removes edges)

        this.chunkPosSuppliers.putEdge(chunkPosSupplierWrapper, chunkPosSupplierWrapper);
    }

    @Override
    public void unbindAll(final ViewPoint viewPoint) {
        final var chunkPosSupplierWrapper = new ChunkPosSupplierWrapperImpl(viewPoint, (ServerLevel) (Object) this);

        this.chunkPosSuppliers.predecessors(chunkPosSupplierWrapper).forEach(serverPlayer -> {
            this.chunkPosSuppliers.removeEdge(serverPlayer, chunkPosSupplierWrapper);
            this.chunkPosSuppliers.putEdge(serverPlayer, serverPlayer);
        });

        if (viewPoint.unregistersWithNoViewers()) {
            this.chunkPosSuppliers.removeNode(chunkPosSupplierWrapper);
        }
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public ViewPoint getViewPoint(final ServerPlayer serverPlayer) {
        final var chunkPosSupplierWrapper = new ChunkPosSupplierWrapperImpl((ChunkPosSupplier) serverPlayer, (ServerLevel) (Object) this);
        return (ViewPoint) ((ChunkPosSupplierWrapper) this.chunkPosSuppliers.successors(chunkPosSupplierWrapper).stream().findFirst().get()).getChunkPosSupplier();
    }

    @Override
    public Set<ServerPlayer> getBoundPlayers(final ViewPoint viewPoint) {
        final var chunkPosSupplierWrapper = new ChunkPosSupplierWrapperImpl(viewPoint, (ServerLevel) (Object) this);
        return this.chunkPosSuppliers.predecessors(chunkPosSupplierWrapper).stream()
                .map(_viewPoint -> (ServerPlayer) _viewPoint) // cast to ServerPlayer
                .collect(Collectors.toSet()); // collect to Set
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public Set<ServerPlayer> getAllBoundPlayers() {
        return this.chunkPosSuppliers.nodes().stream()
                .filter(chunkPosSupplierWrapper -> ((ChunkPosSupplierWrapper) chunkPosSupplierWrapper).getChunkPosSupplier() instanceof ServerPlayer) // get ServerPlayers
                .filter(serverPlayer -> !this.chunkPosSuppliers.successors(serverPlayer).stream().findFirst().get().equals(serverPlayer)) // remove self-referential edges
                .map(chunkPosSupplierWrapper -> (ServerPlayer) ((ChunkPosSupplierWrapper) chunkPosSupplierWrapper).getChunkPosSupplier()) // cast to ServerPlayer
                .collect(Collectors.toSet()); // collect to Set
    }

    @Override
    public void registerPlayer(final ServerPlayer serverPlayer) {
        final var chunkPosSupplierWrapper = new ChunkPosSupplierWrapperImpl((ChunkPosSupplier) serverPlayer, (ServerLevel) (Object) this);
        this.chunkPosSuppliers.putEdge(chunkPosSupplierWrapper, chunkPosSupplierWrapper);
    }

    @Override
    public void unregisterPlayer(final ServerPlayer serverPlayer) {
        this.unbind(serverPlayer);
        this.chunkPosSuppliers.removeNode(new ChunkPosSupplierWrapperImpl((ChunkPosSupplier) serverPlayer, (ServerLevel) (Object) this));
    }

    @Override
    @SuppressWarnings({"OptionalGetWithoutIsPresent", "EqualsBetweenInconvertibleTypes"})
    public ChunkPosSupplierWrapper getChunkPosSupplierWrapper(final ServerPlayer serverPlayer) {
        return (ChunkPosSupplierWrapper) this.chunkPosSuppliers.nodes().stream().filter(chunkPosSupplier -> chunkPosSupplier.equals(serverPlayer)).findFirst().get();
    }

}
