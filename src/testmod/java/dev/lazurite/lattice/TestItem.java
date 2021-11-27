package dev.lazurite.lattice;

import dev.lazurite.lattice.api.view.ControllableView;
import dev.lazurite.lattice.api.LatticePlayer;
import dev.lazurite.lattice.api.viewable.Viewable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TestItem extends Item {

    public TestItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        final var latticePlayer = (LatticePlayer) player;
        final var viewable = latticePlayer.getViewable();

        if (viewable != player) {
            // getYRot vs getViewYRot vs getYHeadRot
            latticePlayer.setViewable(new ControllableView(player.position(), player.getXRot(), player.getYRot()));
        } else {
            latticePlayer.setViewable((Viewable) player);
        }

        return super.use(level, player, interactionHand);
    }
}
