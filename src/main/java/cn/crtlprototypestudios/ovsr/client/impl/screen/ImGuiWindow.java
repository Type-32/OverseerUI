package cn.crtlprototypestudios.ovsr.client.impl.screen;

import cn.crtlprototypestudios.ovsr.Ovsr;
import cn.crtlprototypestudios.ovsr.client.api.OverseerUtility;
import cn.crtlprototypestudios.ovsr.client.impl.interfaces.Renderable;
import cn.crtlprototypestudios.ovsr.client.impl.interfaces.Theme;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ImGuiWindow implements Renderable {
    Theme theme;

    Component name;

    WindowRenderer renderer;

    public boolean canClose;

    ImBoolean open;

    int windowFlags = ImGuiWindowFlags.None;

    public ImGuiWindow(Theme theme, Component name, WindowRenderer renderer, boolean canClose, int flags) {
        this.theme = theme;
        this.name = name;
        this.renderer = renderer;
        this.canClose = canClose;
        this.open = new ImBoolean(true);
        this.windowFlags = flags;
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
            ImGui.begin(getName(), open, windowFlags);
        } else {
            ImGui.begin(getName());
        }

        renderer.renderWindow();

        ImGui.end();
    }

    public void setWindowFlags(int flags){
        this.windowFlags = flags;
    }

    public void addWindowFlags(int flags){
        this.windowFlags |= flags;
    }

    public void removeWindowFlags(int flags){
        this.windowFlags &= ~flags;
    }
}
