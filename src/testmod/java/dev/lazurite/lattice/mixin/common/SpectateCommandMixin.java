package dev.lazurite.lattice.mixin.common;

import dev.lazurite.lattice.api.LatticePlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.commands.SpectateCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpectateCommand.class)
public abstract class SpectateCommandMixin {

    @Inject(
            method = "spectate",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/brigadier/exceptions/SimpleCommandExceptionType;create()Lcom/mojang/brigadier/exceptions/CommandSyntaxException;",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private static void spectate_create(CommandSourceStack commandSourceStack, Entity entity, ServerPlayer serverPlayer, CallbackInfoReturnable<Integer> cir) {
        ((LatticePlayer) serverPlayer).setCameraWithoutViewable(entity);
        if (entity != null) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.spectate.success.started", new Object[]{entity.getDisplayName()}), false);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.spectate.success.stopped"), false);
        }

        cir.setReturnValue(1);
    }

    @Redirect(
            method = "spectate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayerGameMode;getGameModeForPlayer()Lnet/minecraft/world/level/GameType;"
            )
    )
    private static GameType spectate_getGameModeForPlayer(ServerPlayerGameMode instance) {
        return GameType.SPECTATOR;
    }

}
