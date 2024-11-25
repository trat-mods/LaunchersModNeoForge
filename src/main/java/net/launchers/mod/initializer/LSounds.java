package net.launchers.mod.initializer;

import net.launchers.mod.loader.LLoader;
import net.launchers.mod.block.abstraction.AbstractLauncherBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, LLoader.MOD_ID);
    public static final DeferredHolder<SoundEvent, ?> LAUNCHER_SOUND_EVENT = SOUNDS.register(AbstractLauncherBlock.LAUNCH_SOUND.getPath(),
                                                                                             () -> SoundEvent.createVariableRangeEvent(AbstractLauncherBlock.LAUNCH_SOUND));

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }
}
