package dev.lazurite.lattice.chunk.impl.common.mixin;

import dev.lazurite.lattice.core.impl.iapi.duck.IPlayer;
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

    @ModifyVariable(
            method = "Lnet/minecraft/server/level/ChunkMap$TrackedEntity;updatePlayer(Lnet/minecraft/server/level/ServerPlayer;)V",
//            method = "updatePlayer",
            at = @At(
                    value = "STORE",
                    ordinal = 1
            )
    )
    public double updatePlayer_STORE(double e, ServerPlayer serverPlayer) {
        final var serverEntityPosition = this.serverEntity.sentPos();
        final var viewablePosition = ((IPlayer) serverPlayer).getViewable().latGetPosition();

        final var position = viewablePosition.subtract(serverEntityPosition);

        return Math.min(e, position.x() * position.x() + position.z() * position.z());
    }

}
