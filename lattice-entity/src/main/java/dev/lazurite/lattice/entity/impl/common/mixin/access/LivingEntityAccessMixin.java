package dev.lazurite.lattice.entity.impl.common.mixin.access;

import dev.lazurite.lattice.entity.impl.iapi.duck.ILivingEntityAccess;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class LivingEntityAccessMixin implements ILivingEntityAccess {

    @Shadow public float yHeadRot;
    @Shadow public float yHeadRotO;

    @Override
    public float getYRotation() {
        return this.yHeadRot;
    }

    @Override
    public float getPreviousYRotation() {
        return this.yHeadRotO;
    }

}
