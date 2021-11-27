package dev.lazurite.lattice.api.view;

import dev.lazurite.lattice.api.viewable.ControllableViewable;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ControllableView implements ControllableViewable {

    private Vec3 position;
    private Vec3 positionOld;
    private float xRot, yRot;
    private float xRotOld, yRotOld;

    public ControllableView(Vec3 position, float xRot, float yRot) {
        this.position = position;
        this.positionOld = position;

        this.xRot = xRot;
        this.xRotOld = xRot;

        this.yRot = yRot;
        this.yRotOld = yRot;
    }

    @Override
    public void setViewablePosition(Vec3 position) {
        this.positionOld = this.position;
        this.position = position;
    }

    @Override
    public Vec3 getViewablePosition() {
        return new Vec3(position.x(), position.y(), position.z());
    }

    @Override
    public Vec3 getViewablePosition(float delta) {
        return this.positionOld.lerp(this.position, delta);
    }

    @Override
    public double getViewableX() {
        return this.position.x();
    }

    @Override
    public double getViewableY() {
        return this.position.y();
    }

    @Override
    public double getViewableZ() {
        return this.position.z();
    }

    @Override
    public void setViewableXRot(float xRot) {
        this.xRotOld = this.xRot;
        this.xRot = xRot;
    }

    @Override
    public float getViewableXRot() {
        return this.xRot;
    }

    @Override
    public float getViewableXRot(float delta) {
        return Mth.lerp(delta, this.xRotOld, this.xRot);
    }

    @Override
    public void setViewableYRot(float yRot) {
        this.yRotOld = this.yRot;
        this.yRot = yRot;
    }

    @Override
    public float getViewableYRot() {
        return this.yRot;
    }

    @Override
    public float getViewableYRot(float delta) {
        return Mth.lerp(delta, this.yRotOld, this.yRot);
    }

    @Override
    public boolean shouldRenderPlayer() {
        return true;
    }

}
