package dev.lazurite.lattice.impl.common.mixin.core;

import dev.lazurite.lattice.api.viewable.Viewable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin implements Viewable {

    @Shadow public abstract double getX();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();
    @Shadow public abstract Vec3 getPosition(float f);
    @Shadow public abstract Vec3 getEyePosition();
    @Shadow public abstract Vec3 getEyePosition(float f);
    @Shadow public abstract float getXRot();
    @Shadow public abstract float getViewXRot(float f);
    @Shadow public abstract float getYRot();
    @Shadow public abstract float getViewYRot(float f);

    @Override
    public Vec3 getViewablePosition() {
        // this.position() doesn't return a copy
        return new Vec3(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public Vec3 getViewablePosition(float delta) {
        return this.getPosition(delta);
    }

    @Override
    public Vec3 getViewableViewPosition() {
        return this.getEyePosition();
    }

    @Override
    public Vec3 getViewableViewPosition(float delta) {
        return this.getEyePosition(delta);
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
    public float getViewableXRot(float delta) {
        return this.getViewXRot(delta);
    }

    @Override
    public float getViewableYRot() {
        return this.getYRot();
    }

    @Override
    public float getViewableYRot(float delta) {
        return this.getViewYRot(delta);
    }

}
