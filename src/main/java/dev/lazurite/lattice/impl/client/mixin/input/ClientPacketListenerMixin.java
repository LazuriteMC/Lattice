package dev.lazurite.lattice.impl.client.mixin.input;

import dev.lazurite.lattice.impl.client.LatticeKeyboardInput;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    /**
     * Redirect {@link KeyboardInput} constructors to {@link LatticeKeyboardInput} constructors.
     * Still not sure if I want to do this or strictly use mixins instead of a custom class.
     */
    @Redirect(
            method = { "handleLogin", "handleRespawn" },
            at = @At(
                    value = "NEW",
                    target = "Lnet/minecraft/client/player/KeyboardInput;"
            )
    )
    public KeyboardInput handleLogin_handleRespawn_NEW(Options options) {
        return new LatticeKeyboardInput(options);
    }

}
