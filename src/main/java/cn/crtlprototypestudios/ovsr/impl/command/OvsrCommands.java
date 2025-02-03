package cn.crtlprototypestudios.ovsr.impl.command;

import cn.crtlprototypestudios.ovsr.impl.screen.TestScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class OvsrCommands {
    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("ovsrtest")
                        .executes(context -> {
                            Minecraft.getInstance().setScreen(new TestScreen());
                            return 1;
                        })
        );
    }
}
