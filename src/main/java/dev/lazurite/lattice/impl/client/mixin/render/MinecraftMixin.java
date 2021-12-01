package dev.lazurite.lattice.impl.client.mixin.render;

import dev.lazurite.lattice.api.LatticePlayer;
import dev.lazurite.lattice.api.Viewable;
import dev.lazurite.lattice.impl.common.util.BlockPosUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    /**
     * Uses the {@link Viewable} position instead of the player's position.
     */
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getBlockX()I"
            )
    )
    public final int tick_getBlockX(LocalPlayer player) {
        return BlockPosUtil.posToBlockCoord(((LatticePlayer) player).getViewable().getViewableX());
    }

    /**
     * Uses the {@link Viewable} position instead of the player's position.
     */
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getBlockY()I"
            )
    )
    public final int tick_getBlockY(LocalPlayer player) {
        return BlockPosUtil.posToBlockCoord(((LatticePlayer) player).getViewable().getViewableY());
    }

    /**
     * Uses the {@link Viewable} position instead of the player's position.
     */
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getBlockZ()I"
            )
    )
    public final int tick_getBlockZ(LocalPlayer player) {
        return BlockPosUtil.posToBlockCoord(((LatticePlayer) player).getViewable().getViewableZ());
    }

}
