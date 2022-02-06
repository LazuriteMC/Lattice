package dev.lazurite.lattice.impl.common.mixin.misc;

import dev.lazurite.lattice.api.LatticePlayer;
import dev.lazurite.lattice.api.Viewable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Shadow private @Nullable Entity camera;
    @Shadow public abstract Entity getCamera();

    /**
     * Cancels {@link ServerPlayer#absMoveTo(double, double, double, float, float)} in {@link ServerPlayer#tick()}.
     */
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;absMoveTo(DDDFF)V"
            )
    )
    public void absMoveTo(ServerPlayer serverPlayer, double d, double e, double f, float g, float h) { }

    /**
     * Set's {@link ServerPlayer#camera} to the {@link Viewable} of the desired target, if available.
     */
    @Inject(
            method = "setCamera",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.PUTFIELD,
                    target = "Lnet/minecraft/server/level/ServerPlayer;camera:Lnet/minecraft/world/entity/Entity;",
                    shift = At.Shift.AFTER
            )
    )
    public void setCamera_STORE(Entity entity, CallbackInfo ci) {
        if (entity instanceof Player player && !player.equals((Object) this) && !((LatticePlayer) player).isViewableSelf()) {
            this.camera = (Entity) ((LatticePlayer) player).getViewable(); // TODO
        }
    }

    /**
     * Sets the {@link ServerPlayer}'s {@link Viewable}.
     */
    @Inject(
            method = "setCamera",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;teleportTo(DDD)V"
            )
    )
    public void setCamera_teleportTo(Entity entity, CallbackInfo ci) {
        ((LatticePlayer) this).setViewable((Viewable) this.getCamera());
    }

    /**
     * Cancels {@link ServerPlayer#teleportTo(double, double, double)} in {@link ServerPlayer#setCamera(Entity)}.
     */
    @Redirect(
            method = "setCamera",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;teleportTo(DDD)V"
            )
    )
    public void teleportTo(ServerPlayer serverPlayer, double d, double e, double f) { }

}
