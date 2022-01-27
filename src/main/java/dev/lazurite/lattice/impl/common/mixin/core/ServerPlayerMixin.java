package dev.lazurite.lattice.impl.common.mixin.core;

import com.mojang.authlib.GameProfile;
import dev.lazurite.lattice.impl.common.duck.InternalLatticeServerPlayer;
import dev.lazurite.toolbox.api.util.ChunkPosUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Implements {@link InternalLatticeServerPlayer} onto {@link ServerPlayer}.
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements InternalLatticeServerPlayer {

    @Shadow public abstract SectionPos getLastSectionPos();

    private SectionPos lastViewableSectionPos = SectionPos.of(0, 0, 0);

    private SectionPos lastLastSectionPos = SectionPos.of(0, 0, 0);
    private SectionPos lastLastViewableSectionPos = SectionPos.of(0, 0, 0);

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Override
    public void setLastViewableSectionPos(final SectionPos sectionPos) {
        this.lastViewableSectionPos = sectionPos;
    }

    @Override
    public SectionPos getLastViewableSectionPos() {
        return this.lastViewableSectionPos;
    }

    @Override
    public void updateLastLastSectionPos() {
        this.lastLastSectionPos = this.getLastSectionPos();
    }

    @Override
    public void updateLastLastViewableSectionPos() {
        this.lastLastViewableSectionPos = this.lastViewableSectionPos;
    }

    @Override
    public SectionPos getLastLastViewableSectionPos() {
        return this.lastLastViewableSectionPos;
    }

    @Override
    public boolean isViewableInSameChunk() {
        return ChunkPosUtil.of(this).equals(ChunkPosUtil.of(this.getViewable().getPosition()));
    }

    @Override
    public boolean wasViewableInSameChunk(final boolean useLastLast) {
        return useLastLast ? this.lastLastSectionPos.chunk().equals(this.lastLastViewableSectionPos.chunk()) : this.getLastSectionPos().chunk().equals(this.lastViewableSectionPos.chunk());
    }

}
