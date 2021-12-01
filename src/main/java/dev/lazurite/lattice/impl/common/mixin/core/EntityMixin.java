package dev.lazurite.lattice.impl.common.mixin.core;

import dev.lazurite.lattice.api.Viewable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin implements Viewable {

    @Shadow public abstract double getX();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();
    @Shadow public abstract float getXRot();
    @Shadow public abstract float getYRot();

    @Override
    public Vec3 getViewablePosition() {
        // this.position() doesn't return a copy
        return new Vec3(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public double getViewableX() {
        return this.getX();
    }

    @Override
    public double getViewableY() {
        return this.getY();
    }

    @Override
    public double getViewableZ() {
        return this.getZ();
    }

    @Override
    public float getViewableXRot() {
        return this.getXRot();
    }

    @Override
    public float getViewableYRot() {
        return this.getYRot();
    }

}
