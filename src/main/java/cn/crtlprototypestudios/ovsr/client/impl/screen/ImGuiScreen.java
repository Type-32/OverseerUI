package cn.crtlprototypestudios.ovsr.client.impl.screen;

import cn.crtlprototypestudios.ovsr.Ovsr;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ImGuiScreen extends Screen {

    List<ImGuiWindow> windows;

    boolean closeWhenNoWindows;

    boolean alreadyInitialised;

    protected ImGuiScreen(Component component, boolean closeWhenNoWindows) {
        super(component);
        this.closeWhenNoWindows = closeWhenNoWindows;
        alreadyInitialised = false;
    }

    protected List<ImGuiWindow> initImGui() {
        return List.of();
    }

    @Override
    protected void init() {
        super.init();
        if (!alreadyInitialised) {
            windows = initImGui();
            for (ImGuiWindow window : windows) {
                Ovsr.pushRenderable(window);
            }
            alreadyInitialised = true;
        }
    }

    @Override
    public void onClose() {
        for (ImGuiWindow window : windows) {
            Ovsr.pullRenderable(window);
        }
        super.onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);

        if (closeWhenNoWindows) {
            boolean foundOpen = false;
            for (ImGuiWindow window : windows) {
                if (window.open.get()) {
                    foundOpen = true;
                }
            }
            if (!foundOpen)
                onClose();
        }
    }

    protected void pushWindow(ImGuiWindow window) {
        windows.add(window);
        Ovsr.pushRenderable(window);
    }

    protected void pullWindow(ImGuiWindow window) {
        windows.remove(window);
        Ovsr.pullRenderableAfterRender(window);
    }
}
