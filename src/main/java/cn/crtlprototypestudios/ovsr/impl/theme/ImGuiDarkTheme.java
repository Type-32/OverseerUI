package cn.crtlprototypestudios.ovsr.impl.theme;

import cn.crtlprototypestudios.ovsr.impl.interfaces.Theme;
import imgui.ImGui;

public class ImGuiDarkTheme implements Theme {
    @Override
    public void preRender() {
        ImGui.styleColorsDark();
    }

    @Override
    public void postRender() {

    }
}
