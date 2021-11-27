package dev.lazurite.lattice.impl.client;

import dev.lazurite.lattice.api.LatticePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.phys.Vec2;

// TODO: This can be done with a custom class and ctor redirection (like this)
//  or via mixins... idk which is better
public class LatticeKeyboardInput extends KeyboardInput {

    public float latticeLeftImpulse;
    public float latticeForwardImpulse;
    public boolean latticeUp;
    public boolean latticeDown;
    public boolean latticeLeft;
    public boolean latticeRight;
    public boolean latticeJumping;
    public boolean latticeShiftKeyDown;

    public LatticeKeyboardInput(Options options) {
        super(options);
    }

    private void setLatticeInputs() {
        this.latticeUp = this.up;
        this.latticeDown = this.down;
        this.latticeLeft = this.left;
        this.latticeRight = this.right;
        this.latticeJumping = this.jumping;
        this.latticeShiftKeyDown = this.shiftKeyDown;

        this.latticeForwardImpulse = this.forwardImpulse;
        this.latticeLeftImpulse = this.leftImpulse;
    }

    private void resetInputs() {
        this.up = false;
        this.down = false;
        this.left = false;
        this.right = false;
        this.jumping = false;
        this.shiftKeyDown = false;

        this.forwardImpulse = 0.0F;
        this.leftImpulse = 0.0F;
    }

    @Override
    public void tick(boolean bl) {
        super.tick(bl);

        final var player = (LatticePlayer) Minecraft.getInstance().player;

        if (player.getViewable() != player) {
            this.setLatticeInputs();
            this.resetInputs();
        }
    }

    @Override
    public Vec2 getMoveVector() {
        return new Vec2(this.latticeLeftImpulse, this.latticeForwardImpulse);
    }

    @Override
    public boolean hasForwardImpulse() {
        return this.latticeForwardImpulse > 1.0E-5F;
    }

}
