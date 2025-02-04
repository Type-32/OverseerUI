package cn.crtlprototypestudios.ovsr.client.impl.theme;

import cn.crtlprototypestudios.ovsr.client.impl.interfaces.Theme;
import imgui.ImGui;

public class ImGuiLightTheme implements Theme {
    @Override
    public void preRender() {
        ImGui.styleColorsLight();
    }

    @Override
    public void postRender() {

    }
}
