package dev.lazurite.lattice.impl.mixin.common.access;

import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityTrackerEntry.class)
public interface IEntityTrackerEntry {
    @Accessor
    ServerWorld getWorld();
}
