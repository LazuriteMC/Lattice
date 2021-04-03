package dev.lazurite.lattice.impl.mixin.client.access;

import dev.lazurite.lattice.impl.client.IClientChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net/minecraft/client/world/ClientChunkManager$ClientChunkMap")
public interface IClientChunkMapMixin extends IClientChunkMap {
    @Mutable
    @Accessor
    @Override
    void setDiameter(int diameter);
}
