package cn.crtlprototypestudios.ovsr.client.impl.theme;

import cn.crtlprototypestudios.ovsr.client.impl.interfaces.Theme;
import imgui.ImGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ImGuiLightTheme implements Theme {
    @Override
    public void preRender() {
        ImGui.styleColorsLight();
    }

    @Override
    public void postRender() {

    }
}
