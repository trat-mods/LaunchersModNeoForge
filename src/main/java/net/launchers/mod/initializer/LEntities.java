package net.launchers.mod.initializer;

import net.launchers.mod.loader.LLoader;
import net.launchers.mod.block.ExtremeLauncherBlock;
import net.launchers.mod.block.LauncherBlock;
import net.launchers.mod.block.PoweredLauncherBlock;
import net.launchers.mod.entity.ExtremeLauncherBlockEntity;
import net.launchers.mod.entity.LauncherBlockEntity;
import net.launchers.mod.entity.PoweredLauncherBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LEntities {
    public static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, LLoader.MOD_ID);

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LauncherBlockEntity>> LAUNCHER_BLOCK_TILE_ENTITY = ENTITIES.register(LauncherBlock.ID,
                                                                                                                                                () -> BlockEntityType.Builder
                                                                                                                                                        .of(LauncherBlockEntity::new,
                                                                                                                                                            LBlocks.LAUNCHER_BLOCK.get())
                                                                                                                                                        .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PoweredLauncherBlockEntity>> POWERED_LAUNCHER_BLOCK_TILE_ENTITY = ENTITIES.register(
            PoweredLauncherBlock.ID, () -> BlockEntityType.Builder.of(PoweredLauncherBlockEntity::new, LBlocks.POWERED_LAUNCHER_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExtremeLauncherBlockEntity>> EXTREME_LAUNCHER_BLOCK_TILE_ENTITY = ENTITIES.register(
            ExtremeLauncherBlock.ID, () -> BlockEntityType.Builder.of(ExtremeLauncherBlockEntity::new, LBlocks.EXTREME_LAUNCHER_BLOCK.get()).build(null));


}
