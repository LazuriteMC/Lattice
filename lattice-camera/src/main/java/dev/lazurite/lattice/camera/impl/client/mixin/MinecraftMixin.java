package dev.lazurite.lattice.camera.impl.client.mixin;

import dev.lazurite.lattice.core.impl.iapi.duck.IPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow @Nullable public LocalPlayer player;

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getBlockX()I"
            )
    )
    public final int tick_getBlockX(LocalPlayer localPlayer) {
        return Mth.floor(((IPlayer) this.player).getViewable().getX()); // TODO Why floor instead of int cast?
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getBlockY()I"
            )
    )
    public final int tick_getBlockY(LocalPlayer localPlayer) {
        return Mth.floor(((IPlayer) this.player).getViewable().getY()); // TODO Why floor instead of int cast?
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getBlockZ()I"
            )
    )
    public final int tick_getBlockZ(LocalPlayer localPlayer) {
        return Mth.floor(((IPlayer) this.player).getViewable().getZ()); // TODO Why floor instead of int cast?
    }

}
