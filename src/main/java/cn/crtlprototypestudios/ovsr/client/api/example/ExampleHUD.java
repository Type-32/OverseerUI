package cn.crtlprototypestudios.ovsr.client.api.example;

import cn.crtlprototypestudios.ovsr.client.api.OverseerHUD;
import cn.crtlprototypestudios.ovsr.client.impl.interfaces.Theme;
import cn.crtlprototypestudios.ovsr.client.impl.theme.ImGuiDarkTheme;
import imgui.ImGui;
import net.minecraft.client.Minecraft;

public class ExampleHUD extends OverseerHUD.HUDElement {
    public ExampleHUD() {
        super("overseer_example_hud", new ImGuiDarkTheme());
    }

    @Override
    public String getName() {
        return "Example HUD";
    }

    @Override
    protected void renderContent() {
        ImGui.text("Player: " + Minecraft.getInstance().player.getName().getString());
        ImGui.text("FPS: " + Minecraft.getInstance().getFps());
    }
}

