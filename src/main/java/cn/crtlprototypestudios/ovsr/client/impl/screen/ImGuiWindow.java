package cn.crtlprototypestudios.ovsr.client.impl.screen;

import cn.crtlprototypestudios.ovsr.Ovsr;
import cn.crtlprototypestudios.ovsr.client.impl.interfaces.Renderable;
import cn.crtlprototypestudios.ovsr.client.impl.interfaces.Theme;
import imgui.ImGui;
import imgui.type.ImBoolean;
import net.minecraft.network.chat.Component;

public class ImGuiWindow implements Renderable {
    Theme theme;

    Component name;

    WindowRenderer renderer;

    public boolean canClose;

    ImBoolean open;

    public ImGuiWindow(Theme theme, Component name, WindowRenderer renderer, boolean canClose) {
        this.theme = theme;
        this.name = name;
        this.renderer = renderer;
        this.canClose = canClose;
        this.open = new ImBoolean(true);
    }

    @Override
    public String getName() {
        return this.name.getString();
    }

    @Override
    public Theme getTheme() {
        return this.theme;
    }

    @Override
    public void render() {
        if (!open.get()) {
            Ovsr.pullRenderableAfterRender(this);
            return;
        }

        if (canClose) {
            ImGui.begin(getName(), open);
        } else {
            ImGui.begin(getName());
        }

        renderer.renderWindow();

        ImGui.end();
    }
}
