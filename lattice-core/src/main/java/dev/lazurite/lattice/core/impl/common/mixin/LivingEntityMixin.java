package dev.lazurite.lattice.core.impl.common.mixin;

import dev.lazurite.lattice.core.api.EntityViewable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityViewable {

    @Shadow public abstract float getYHeadRot();
    @Shadow public abstract float getViewYRot(float f);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public Vec3 latGetPosition() {
        return this.getEyePosition();
    }

    @Override
    public Vec3 latGetPosition(final float delta) {
        return this.getEyePosition(delta);
    }

    @Override
    public Vec3 latGetPosition(final double delta) {
        // Entity#getEyePosition(float) lerps double values by a float delta
        return new Vec3(
                this.latGetX(delta),
                this.latGetY(delta),
                this.latGetZ(delta)
        );
    }

    @Override
    public double latGetX() {
        return this.getX();
    }

    @Override
    public double latGetX(final float delta) {
        // Entity#getX(double) isn't a lerping method
        return this.xo + (this.getX() - this.xo) * delta;
    }

    @Override
    public double latGetX(final double delta) {
        // Entity#getX(double) isn't a lerping method
        return this.xo + (this.getX() - this.xo) * delta;
    }

    @Override
    public double latGetY() {
        return this.getEyeY();
    }

    @Override
    public double latGetY(final float delta) {
        // Entity#getY(double) isn't a lerping method and doesn't use eyeHeight
        final var eyeYO = this.yo + this.getEyeHeight();
        return eyeYO + (this.getEyeY() - eyeYO) * delta;
    }

    @Override
    public double latGetY(final double delta) {
        // Entity#getY(float) isn't a lerping method and doesn't use eyeHeight
        final var eyeYO = this.yo + this.getEyeHeight();
        return eyeYO + (this.getEyeY() - eyeYO) * delta;
    }

    @Override
    public double latGetZ() {
        return this.getZ();
    }

    @Override
    public double latGetZ(final float delta) {
        // Entity#getZ(double) isn't a lerping method
        return this.zo + (this.getZ() - this.zo) * delta;
    }

    @Override
    public double latGetZ(final double delta) {
        // Entity#getZ(float) isn't a lerping method
        return this.zo + (this.getZ() - this.zo) * delta;
    }

    @Override
    public float latGetPitch() {
        return this.getXRot();
    }

    @Override
    public float latGetPitch(final float delta) {
        return this.getViewXRot(delta);
    }

    @Override
    public float latGetYaw() {
        return this.getYHeadRot();
    }

    @Override
    public float latGetYaw(final float delta) {
        return this.getViewYRot(delta);
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
