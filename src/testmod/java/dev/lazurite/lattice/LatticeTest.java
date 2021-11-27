package dev.lazurite.lattice;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public final class LatticeTest implements ModInitializer {

    @Override
    public void onInitialize() {

        Registry.register(
                Registry.ITEM,
                new ResourceLocation("lattice", "test_item"),
                new TestItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC))
        );

    }

}
