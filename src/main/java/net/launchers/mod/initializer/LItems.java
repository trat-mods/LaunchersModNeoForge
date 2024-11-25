package net.launchers.mod.initializer;

import net.launchers.mod.loader.LLoader;
import net.launchers.mod.block.ExtremeLauncherBlock;
import net.launchers.mod.block.LauncherBlock;
import net.launchers.mod.block.PoweredLauncherBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, LLoader.MOD_ID);

    public static final DeferredHolder<Item, BlockItem> LAUNCHER_BLOCK_ITEM = ITEMS.register(LauncherBlock.ID,
                                                                                             () -> new BlockItem(LBlocks.LAUNCHER_BLOCK.get(), new Item.Properties()));

    public static final DeferredHolder<Item, BlockItem> POWERED_LAUNCHER_BLOCK_ITEM = ITEMS.register(PoweredLauncherBlock.ID,
                                                                                                     () -> new BlockItem(LBlocks.POWERED_LAUNCHER_BLOCK.get(),
                                                                                                                         new Item.Properties()));

    public static final DeferredHolder<Item, BlockItem> EXTREME_LAUNCHER_BLOCK_ITEM = ITEMS.register(ExtremeLauncherBlock.ID,
                                                                                                     () -> new BlockItem(LBlocks.EXTREME_LAUNCHER_BLOCK.get(),
                                                                                                                         new Item.Properties()));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
