package cn.crtlprototypestudios.ovsr.client.impl.theme;

import cn.crtlprototypestudios.ovsr.client.impl.interfaces.Theme;
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
