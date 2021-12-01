package dev.lazurite.lattice.impl.common.mixin.core;

import dev.lazurite.lattice.api.Viewable;
import dev.lazurite.lattice.impl.common.iapi.ILatticePlayer;
import io.netty.buffer.Unpooled;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements ILatticePlayer {

    // TODO Stack? (Viewable) this?
    private Viewable viewable = (Viewable) this;
    private SectionPos lastViewableSectionPos = SectionPos.of(0, 0, 0);

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void setViewable(Viewable viewable) {
        this.viewable = viewable;

        if (!this.level.isClientSide()) {
            final var serverPlayer = (ServerPlayer) (Object) this;

            final var friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());
            friendlyByteBuf.writeBoolean(true);
            friendlyByteBuf.writeInt(serverPlayer.getCamera().getId());
            // I'll have constants in the future
            serverPlayer.connection.send(new ClientboundCustomPayloadPacket(new ResourceLocation("lattice", "set_viewable"), friendlyByteBuf));
        }
    }

    @Override
    public Viewable getViewable() {
        return this.viewable;
    }

    @Override
    public boolean isViewableSelf() {
        return this.getViewable() == this;
    }

    @Override
    public void removeViewable() {
        this.viewable = (Viewable) this;
    }

    @Override
    public void setLastViewableSectionPos(SectionPos sectionPos) {
        this.lastViewableSectionPos = sectionPos;
    }

    @Override
    public SectionPos getLastViewableSectionPos() {
        return this.lastViewableSectionPos;
    }

}
