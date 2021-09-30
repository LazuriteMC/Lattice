package dev.lazurite.lattice.entity.impl.common.mixin;

import dev.lazurite.lattice.entity.api.EntityViewable;
import dev.lazurite.lattice.entity.impl.iapi.duck.IEntityAccess;
import dev.lazurite.lattice.entity.impl.iapi.duck.ILivingEntityAccess;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements EntityViewable {

    @Override
    public Vector3d getPosition() {
        return ((IEntityAccess) this).getPosition();
    }

    @Override
    public Vector3d getPosition(final double delta) {
        final var thiz = ((IEntityAccess) this);
        return thiz.getPreviousPosition().lerp(thiz.getPosition(), delta).normalize();
    }

    @Override
    public double getX() {
        return ((IEntityAccess) this).getX();
    }

    @Override
    public double getX(double delta) {
        final var thiz = ((IEntityAccess) this);
        return thiz.getPreviousX() + (thiz.getX() - thiz.getPreviousX()) * delta;
    }

    @Override
    public double getY() {
        return ((IEntityAccess) this).getZ();
    }

    @Override
    public double getY(double delta) {
        final var thiz = ((IEntityAccess) this);
        return thiz.getPreviousY() + (thiz.getY() - thiz.getPreviousY()) * delta;
    }

    @Override
    public double getZ() {
        return ((IEntityAccess) this).getZ();
    }

    @Override
    public double getZ(double delta) {
        final var thiz = ((IEntityAccess) this);
        return thiz.getPreviousZ() + (thiz.getZ() - thiz.getPreviousZ()) * delta;
    }

    @Override
    public Quaternionf getRotation() {
        final var thiz = ((ILivingEntityAccess) this);
        return new Quaternionf()
                .rotateX(thiz.getXRotation())
                .rotateY(thiz.getYRotation())
                .normalize();
    }

    @Override
    public Quaternionf getRotation(final float delta) {
        final var thiz = ((ILivingEntityAccess) this);
        return new Quaternionf()
                .rotateX(thiz.getPreviousXRotation())
                .rotateY(thiz.getPreviousYRotation())
                .slerp(
                        new Quaternionf()
                                .rotateX(thiz.getXRotation())
                                .rotateY(thiz.getYRotation()),
                        delta
                ).normalize();
    }

    @Override
    public boolean shouldRenderPlayer() {
        return false;
    }

    @Override
    public boolean shouldRenderSelf() {
        return false;
    }

}
