package cn.crtlprototypestudios.ovsr.client.impl.command;

import cn.crtlprototypestudios.ovsr.client.api.example.ExampleScreen;
import cn.crtlprototypestudios.ovsr.client.impl.screen.TestScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class OvsrCommands {
    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("overseer")
                        .then(Commands.literal("examples")
                                .then(Commands.literal("eg1")
                                        .executes(context -> {
                                            Minecraft.getInstance().setScreen(new TestScreen());
                                            return 1;
                                        }))
                                .then(Commands.literal("eg2")
                                        .executes(context -> {
                                            Minecraft.getInstance().setScreen(new ExampleScreen());
                                            return 1;
                                        }))
                        )

        );
    }
}
