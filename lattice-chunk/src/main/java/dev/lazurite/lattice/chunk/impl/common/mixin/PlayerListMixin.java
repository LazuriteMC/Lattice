package dev.lazurite.lattice.chunk.impl.common.mixin;

import dev.lazurite.lattice.core.impl.iapi.duck.IPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @ModifyVariable(
            method = "broadcast",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            )
    )
    public double broadcast_STORE0(double h, @Nullable Player player, double d) {
        return Math.min(h, d - ((IPlayer) player).getViewable().latGetX());
    }

    @ModifyVariable(
            method = "broadcast",
            at = @At(
                    value = "STORE",
                    ordinal = 1
            )
    )
    public double broadcast_STORE1(double j, @Nullable Player player, double d, double e) {
        return Math.min(j, e - ((IPlayer) player).getViewable().latGetY());
    }

    @ModifyVariable(
            method = "broadcast",
            at = @At(
                    value = "STORE",
                    ordinal = 2
            )
    )
    public double broadcast_STORE2(double k, @Nullable Player player, double d, double e, double f) {
        return Math.min(k, f - ((IPlayer) player).getViewable().latGetZ());
    }

}
