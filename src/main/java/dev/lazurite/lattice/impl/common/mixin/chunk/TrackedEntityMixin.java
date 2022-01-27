package dev.lazurite.lattice.impl.common.mixin.chunk;

import dev.lazurite.lattice.api.LatticePlayer;
import dev.lazurite.lattice.api.Viewable;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public abstract class TrackedEntityMixin {

    @Shadow @Final ServerEntity serverEntity;

    /**
     * Returns the minimum of the original calculation (which uses the {@link ServerPlayer})
     * and the following (which uses the {@link ServerPlayer}'s {@link Viewable}).
     */
    @ModifyVariable(
            method = "updatePlayer",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            ),
            ordinal = 1
    )
    public double updatePlayer_STORE(double e, ServerPlayer serverPlayer) {
        final var position = ((LatticePlayer) serverPlayer).getViewable().getPosition().subtract(this.serverEntity.sentPos());
        return Math.min(e, position.x * position.x + position.z * position.z);
    }

}
