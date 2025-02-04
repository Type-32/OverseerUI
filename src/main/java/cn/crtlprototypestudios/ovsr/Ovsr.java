package cn.crtlprototypestudios.ovsr;

import cn.crtlprototypestudios.ovsr.client.api.OverseerHUD;
import cn.crtlprototypestudios.ovsr.client.api.example.HealthBarHUD;
import cn.crtlprototypestudios.ovsr.client.api.example.Notification;
import cn.crtlprototypestudios.ovsr.client.api.example.NotificationHUD;
import cn.crtlprototypestudios.ovsr.client.impl.command.OvsrCommands;
import cn.crtlprototypestudios.ovsr.client.impl.debug.DebugRenderable;
import cn.crtlprototypestudios.ovsr.client.impl.interfaces.Renderable;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import com.mojang.logging.LogUtils;
import imgui.ImGui;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.ArrayList;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Ovsr.MODID)
public class Ovsr {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "ovsr";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ArrayList<Renderable> renderstack = new ArrayList<>();
    public static ArrayList<Renderable> toRemove = new ArrayList<>();

    public Ovsr(final FMLJavaModLoadingContext ctx) {
        IEventBus modEventBus = ctx.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(OvsrCommands.class);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ctx.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
//            pushRenderable(new DebugRenderable());
            MixinExtrasBootstrap.init();
            init();
        }

        public static void init() {
            // Create HUD elements
//            var healthBar = new HealthBarHUD();
//            var notifications = new NotificationHUD();
//
//            // Position them on screen
//            notifications.addNotification(new Notification(1));
//            notifications.addNotification(new Notification(2));
//            notifications.addNotification(new Notification(3));
//            notifications.addNotification(new Notification(4));
//
//            // Add them to the HUD
//            OverseerHUD.addElement(healthBar);
//            OverseerHUD.addElement(notifications);
        }
    }

    public static boolean shouldCancelGameKeyboardInputs() {
        return ImGui.isAnyItemActive() || ImGui.isAnyItemFocused();
    }

    public static int getDockId() {
        return ImGui.getID(getDockIdString());
    }

    public static String getDockIdString() {
        return "ovsr.ui.dockspace";
    }

    public static Renderable pushRenderable(Renderable renderable) {
        renderstack.add(renderable);
        return renderable;
    }

    public static Renderable pullRenderable(Renderable renderable) {
        renderstack.remove(renderable);
        return renderable;
    }

    public static Renderable pullRenderableAfterRender(Renderable renderable) {
        toRemove.add(renderable);
        return renderable;
    }
}
