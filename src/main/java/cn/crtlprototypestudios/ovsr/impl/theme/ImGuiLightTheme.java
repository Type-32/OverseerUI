package cn.crtlprototypestudios.ovsr.impl.theme;

import cn.crtlprototypestudios.ovsr.impl.interfaces.Theme;
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
