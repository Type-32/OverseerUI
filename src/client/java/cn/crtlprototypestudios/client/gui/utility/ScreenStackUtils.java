package cn.crtlprototypestudios.client.gui.utility;

import cn.crtlprototypestudios.controlui_refactored.client.gui.screens.menus.MainMenuScreen;
import cn.crtlprototypestudios.controlui_refactored.client.gui.screens.menus.MenuScreen;
import net.minecraft.client.MinecraftClient;

import java.util.Stack;

public class ScreenStackUtils {
    private static Stack<MenuScreen> screens = new Stack<>();

    public static MenuScreen to(MenuScreen screen) {
        return render(screens.push(screen)); // Runs push() method first then returns the pushed stack result.
    }

    public static MenuScreen back() {
        MenuScreen screen = screens.pop();
        System.out.println(screen.getClass().getSimpleName() + " has been popped from the stack.");
        return render(screens.peek()); // Runs pop() method first then returns the popped stack result.
    }

    public static MenuScreen home() {
        screens.clear();
        return to(new MainMenuScreen());
    }

    public static MenuScreen render(MenuScreen screen){
        MinecraftClient.getInstance().setScreen(screen);
        if (screens.size() <= 0) {
//            MinecraftClient.getInstance().setScreen(new MainMenuScreen());
            return null;
        }
        return screen;
    }

    public static MenuScreen refresh() {
        return render(screens.peek());
    }

    public static void clear() {
        screens.clear();
    }
    public static void exit(){
        clear();
        MinecraftClient.getInstance().setScreen(null);
    }

    public static MenuScreen getCurrent() {
        if (screens.size() <= 0) return null;
        return screens.peek();
    }
}
