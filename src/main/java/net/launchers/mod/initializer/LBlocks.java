package net.launchers.mod.initializer;

import net.launchers.mod.loader.LLoader;
import net.launchers.mod.block.ExtremeLauncherBlock;
import net.launchers.mod.block.LauncherBlock;
import net.launchers.mod.block.PoweredLauncherBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, LLoader.MOD_ID);

    public static final DeferredHolder<Block, LauncherBlock> LAUNCHER_BLOCK = BLOCKS.register(LauncherBlock.ID, LauncherBlock::new);
    public static final DeferredHolder<Block, PoweredLauncherBlock> POWERED_LAUNCHER_BLOCK = BLOCKS.register(PoweredLauncherBlock.ID, PoweredLauncherBlock::new);
    public static final DeferredHolder<Block, ExtremeLauncherBlock> EXTREME_LAUNCHER_BLOCK = BLOCKS.register(ExtremeLauncherBlock.ID, ExtremeLauncherBlock::new);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
