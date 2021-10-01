package dev.lazurite.lattice.camera.impl.client.mixin;

import dev.lazurite.lattice.core.impl.common.util.BlockPosUtil;
import dev.lazurite.lattice.core.impl.iapi.duck.IPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    /**
     * Uses the {@link dev.lazurite.lattice.core.api.Viewable} position instead of the player's position.
     */
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getBlockX()I"
            )
    )
    public final int tick_getBlockX(LocalPlayer localPlayer) {
        return BlockPosUtil.posToBlockCoord(((IPlayer) localPlayer).getViewable().getX());
    }

    /**
     * Uses the {@link dev.lazurite.lattice.core.api.Viewable} position instead of the player's position.
     */
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getBlockY()I"
            )
    )
    public final int tick_getBlockY(LocalPlayer localPlayer) {
        return BlockPosUtil.posToBlockCoord(((IPlayer) localPlayer).getViewable().getX());
    }

    /**
     * Uses the {@link dev.lazurite.lattice.core.api.Viewable} position instead of the player's position.
     */
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getBlockZ()I"
            )
    )
    public final int tick_getBlockZ(LocalPlayer localPlayer) {
        return BlockPosUtil.posToBlockCoord(((IPlayer) localPlayer).getViewable().getX());
    }

}
