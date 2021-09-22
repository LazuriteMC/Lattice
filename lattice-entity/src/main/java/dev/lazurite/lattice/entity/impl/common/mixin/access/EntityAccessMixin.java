package dev.lazurite.lattice.entity.impl.common.mixin.access;

import dev.lazurite.lattice.entity.impl.common.util.duck.IEntityAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityAccessMixin implements IEntityAccess {

    @Shadow private Vec3 position;
    @Shadow private float eyeHeight;
    @Shadow public double xo;
    @Shadow public double yo;
    @Shadow public double zo;
    @Shadow private float xRot;
    @Shadow public float xRotO;

    @Override
    public Vector3d getPosition() {
        final var position = this.position;

        return new Vector3d(
                position.x,
                position.y + this.eyeHeight,
                position.z
        );
    }

    @Override
    public Vector3d getPreviousPosition() {
        return new Vector3d(
                this.xo,
                this.yo + this.eyeHeight,
                this.zo
        );
    }

    @Override
    public double getX() {
        return this.position.x;
    }

    // TODO Use *n*o or *n*Old
    @Override
    public double getPreviousX() {
        return this.xo;
    }

    @Override
    public double getY() {
        return this.position.y + this.eyeHeight;
    }

    // TODO Use *n*o or *n*Old
    @Override
    public double getPreviousY() {
        return this.yo + this.eyeHeight;
    }

    @Override
    public double getZ() {
        return this.position.z;
    }

    // TODO Use *n*o or *n*Old
    @Override
    public double getPreviousZ() {
        return this.zo;
    }

    @Override
    public float getXRotation() {
        return this.xRot;
    }

    @Override
    public float getPreviousXRotation() {
        return this.xRotO;
    }

}
