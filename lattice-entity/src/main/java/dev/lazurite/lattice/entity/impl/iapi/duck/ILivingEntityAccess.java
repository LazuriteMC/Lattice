package dev.lazurite.lattice.entity.impl.iapi.duck;

/**
 * Used to access data from {@link net.minecraft.world.entity.LivingEntity} internally.
 */
public interface ILivingEntityAccess extends IEntityAccess {
    float getYRotation();
    float getPreviousYRotation();
}
