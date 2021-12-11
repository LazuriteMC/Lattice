package dev.lazurite.lattice.impl.common.mixin;

import dev.lazurite.lattice.api.LatticePlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

// TODO: There are probably many more places in PlayerList that need changing
@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    private ServerPlayer serverPlayer;

    @Inject(
            method = "broadcast",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Ljava/util/List;get(I)Ljava/lang/Object;",
                    shift = At.Shift.BY,
                    by = 2
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void broadcast(@Nullable Player player, double d, double e, double f, double g, ResourceKey<Level> resourceKey, Packet<?> packet, CallbackInfo ci, int i, ServerPlayer serverPlayer) {
        this.serverPlayer = serverPlayer;
    }

    @ModifyVariable(
            method = "broadcast",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            ),
            ordinal = 4
    )
    public double broadcast_STORE0(double h, @Nullable Player player, double d) {
        return Math.min(h, d - ((LatticePlayer) this.serverPlayer).getViewable().getViewableX());
    }

    @ModifyVariable(
            method = "broadcast",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            ),
            ordinal = 5
    )
    public double broadcast_STORE1(double j, @Nullable Player player, double d, double e) {
        return Math.min(j, e - ((LatticePlayer) this.serverPlayer).getViewable().getViewableY());
    }

    @ModifyVariable(
            method = "broadcast",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            ),
            ordinal = 6
    )
    public double broadcast_STORE2(double k, @Nullable Player player, double d, double e, double f) {
        return Math.min(k, f - ((LatticePlayer) this.serverPlayer).getViewable().getViewableZ());
    }

}
