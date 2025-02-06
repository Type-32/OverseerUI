package cn.crtlprototypestudios.ovsr.client.impl.theme;

import cn.crtlprototypestudios.ovsr.client.impl.interfaces.Theme;
import imgui.ImGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ImGuiClassicTheme implements Theme {
    @Override
    public void preRender() {
        ImGui.styleColorsClassic();
    }

    @Override
    public void postRender() {

    }
}
