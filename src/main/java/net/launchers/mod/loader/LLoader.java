package net.launchers.mod.loader;

import com.mojang.logging.LogUtils;
import net.launchers.mod.entity_renderer.abstraction.LauncherBlockEntityRenderer;
import net.launchers.mod.initializer.*;
import net.launchers.mod.network.packet.UnboundedEntityVelocityS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(LLoader.MOD_ID)
public class LLoader {
    public static final String MOD_ID = "launchersmodneoforge";

    public static final Logger LOGGER = LogUtils.getLogger();


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public LLoader(IEventBus modEventBus, ModContainer modContainer) {
        //NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::register);
        modEventBus.addListener(this::registerEntityRenderers);
        modEventBus.addListener(this::buildContents);

        LBlocks.register(modEventBus);
        LEntities.register(modEventBus);
        LItems.register(modEventBus);
        LSounds.register(modEventBus);
        LCommands.register();
    }

    public void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(UnboundedEntityVelocityS2CPacket.TYPE, UnboundedEntityVelocityS2CPacket.STREAM_CODEC, UnboundedEntityVelocityS2CPacket::handleDataOnMain);
    }

    public void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(LEntities.LAUNCHER_BLOCK_TILE_ENTITY.get(), LauncherBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(LEntities.POWERED_LAUNCHER_BLOCK_TILE_ENTITY.get(), LauncherBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(LEntities.EXTREME_LAUNCHER_BLOCK_TILE_ENTITY.get(), LauncherBlockEntityRenderer::new);
    }

    public void buildContents(BuildCreativeModeTabContentsEvent event) {
        // Is this the tab we want to add to?
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(LItems.LAUNCHER_BLOCK_ITEM.get());
            event.accept(LItems.POWERED_LAUNCHER_BLOCK_ITEM.get());
            event.accept(LItems.EXTREME_LAUNCHER_BLOCK_ITEM.get());
        }
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
