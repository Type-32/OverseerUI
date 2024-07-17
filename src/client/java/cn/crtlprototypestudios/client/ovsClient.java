package cn.crtlprototypestudios.client;

import cn.crtlprototypestudios.client.gui.utility.ScreenStackUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ovsClient implements ClientModInitializer {

    private static final KeyBinding TOGGLEUI = new KeyBinding("key.control_ui.toggle", GLFW.GLFW_KEY_B, "key.category.control_ui");
    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(TOGGLEUI);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLEUI.wasPressed()){
                ScreenStackUtils.to(new MainMenuScreen());
            }
            if (client.currentScreen == null){
                ScreenStackUtils.clear();
            }
        });
        System.out.println("Control UI Mod Client-Side Initialized!");
    }
}
