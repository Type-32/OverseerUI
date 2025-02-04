package cn.crtlprototypestudios.ovsr.client.api.example;

import cn.crtlprototypestudios.ovsr.client.api.OverseerHUD;
import cn.crtlprototypestudios.ovsr.client.impl.theme.ImGuiDarkTheme;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class HealthBarHUD extends OverseerHUD.HUDElement {
    public HealthBarHUD() {
        super("health_bar", new ImGuiDarkTheme());
    }

    @Override
    protected boolean shouldRender(Minecraft mc) {
        // Call parent check first
        if (!super.shouldRender(mc)) return false;

        // Add custom conditions
        if(mc.player == null) return false;

        setAlignment(HorizontalAlignment.LEFT, VerticalAlignment.TOP)
                .setOffset(10, 10);

        return mc.player.isAlive() && // Only show when player is alive
                !mc.options.hideGui;    // Respect F1 mode
    }

    @Override
    protected void renderContent() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();

        // Draw health text
        ImGui.text("Health: " + (int)health + "/" + (int)maxHealth);

        // Draw health bar
        ImGui.pushStyleColor(ImGuiCol.PlotHistogram, 1.0f, 0.2f, 0.2f, 0.8f);
        ImGui.progressBar(health / maxHealth, 120, 12, "");
        ImGui.popStyleColor();
    }
}
