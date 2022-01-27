package dev.lazurite.lattice.impl.common.mixin.core;

import dev.lazurite.lattice.api.Viewable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.*;

/**
 * Implements {@link Viewable} onto {@link Entity}.
 */
@Mixin(Entity.class)
@Implements(@Interface(iface = Viewable.class, prefix = "view$"))
public abstract class EntityMixin { // implements Viewable {

    @Shadow public abstract double getX();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();
    @Shadow public abstract float getXRot();
    @Shadow public abstract float getYRot();

    @Intrinsic
    public Vec3 view$getPosition() {
        // this.position() doesn't return a copy
        return new Vec3(this.getX(), this.getY(), this.getZ());
    }

    @Intrinsic
    public double view$getX() {
        return this.getX();
    }

    @Intrinsic
    public double view$getY() {
        return this.getY();
    }

    @Intrinsic
    public double view$getZ() {
        return this.getZ();
    }

    @Intrinsic
    public float view$getXRot() {
        return this.getXRot();
    }

    @Intrinsic
    public float view$getYRot() {
        return this.getYRot();
    }

}
