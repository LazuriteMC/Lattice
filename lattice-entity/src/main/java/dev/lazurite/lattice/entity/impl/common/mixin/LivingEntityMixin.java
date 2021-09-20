package dev.lazurite.lattice.entity.impl.common.mixin;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;
import dev.lazurite.lattice.entity.api.EntityViewable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityViewable {

    public LivingEntityMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public Vector3d latGetPosition() {
        return null;
    }

    @Override
    public Vector3d latGetPosition(float delta) {
        return null;
    }

    @Override
    public Vector3d latGetPosition(double delta) {
        return null;
    }

    @Override
    public Quaternion latGetRotation() {
        return null;
    }

    @Override
    public Quaternion latGetRotation(float delta) {
        return null;
    }

    @Override
    public Quaternion latGetRotation(double delta) {
        return null;
    }

    @Override
    public boolean latShouldRenderPlayer() {
        return false;
    }

    @Override
    public Vector3d latGetEyePosition() {
        return null;
    }

    @Override
    public Vector3d latGetEyePosition(float delta) {
        return null;
    }

    @Override
    public Vector3d latGetEyePosition(double delta) {
        return null;
    }

    @Override
    public Quaternion latGetHeadRotation() {
        return null;
    }

    @Override
    public Quaternion latGetHeadRotation(float delta) {
        return null;
    }

    @Override
    public Quaternion latGetHeadRotation(double delta) {
        return null;
    }

    @Override
    public boolean latShouldRenderSelf() {
        return false;
    }

}
