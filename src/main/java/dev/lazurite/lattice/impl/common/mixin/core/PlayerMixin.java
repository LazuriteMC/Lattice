package dev.lazurite.lattice.impl.common.mixin.core;

import dev.lazurite.lattice.api.LatticePlayer;
import dev.lazurite.lattice.api.Viewable;
import dev.lazurite.toolbox.api.network.ServerNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Implements {@link LatticePlayer} onto {@link Player}.
 */
@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements LatticePlayer {

    // TODO Stack? (Viewable) this?
    private Viewable viewable = (Viewable) this;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void setViewable(final Viewable viewable) {
        this.viewable = viewable;

        if (!this.level.isClientSide()) {
            ServerNetworking.send(((ServerPlayer) (Object) this), new ResourceLocation("lattice", "set_viewable"), friendlyByteBuf -> {
                friendlyByteBuf.writeBoolean(true);
                friendlyByteBuf.writeVarInt(((ServerPlayer) (Object) this).getCamera().getId());
            });
        }
    }

    @Override
    public Viewable getViewable() {
        return this.viewable;
    }

    @Override
    public void removeViewable() {
        this.viewable = (Viewable) this;
    }

    @Override
    public boolean isViewableSelf() {
        return this.viewable == this;
    }

}
