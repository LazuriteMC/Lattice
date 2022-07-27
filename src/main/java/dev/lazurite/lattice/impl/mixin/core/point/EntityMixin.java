package dev.lazurite.lattice.impl.mixin.core.point;

import dev.lazurite.lattice.api.point.ViewPoint;
import dev.lazurite.lattice.impl.mixin.access.IChunkMapMixin;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;

@Mixin(Entity.class)
@Implements(@Interface(iface = ViewPoint.class, prefix = "vp$"))
public abstract class EntityMixin {

    @Shadow public abstract double getX();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();
    @Shadow public abstract float getYRot();
    @Shadow public abstract float getXRot();
    @Shadow public abstract Level getLevel();

    @Intrinsic
    public double vp$getX() {
        return this.getX();
    }

    @Intrinsic
    public double vp$getY() {
        return this.getY();
    }

    @Intrinsic
    public double vp$getZ() {
        return this.getZ();
    }

    @Intrinsic
    public float vp$getYRot() {
        return this.getYRot();
    }

    @Intrinsic
    public float vp$getXRot() {
        return this.getXRot();
    }

    public int vp$getDistance() {
        if (this.getLevel().isClientSide()) {
            return ((ViewPoint) this).getDistance(); // TODO: shouldn't even matter
        } else {
            return ((IChunkMapMixin) ((ServerChunkCache) this.getLevel().getChunkSource()).chunkMap).getViewDistance();
        }
    }

}
