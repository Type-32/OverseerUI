package cn.crtlprototypestudios.ovsr.client.impl.screen;

import imgui.ImGui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TestScreen extends Screen {
    private boolean showDemoWindow = true;
    private float[] clearColor = {0.45f, 0.55f, 0.60f, 1.00f};

    public TestScreen() {
        super(Component.literal("Overseer UI Test Screen"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        // Test window
        ImGui.begin("Overseer UI Test Window");
        ImGui.text("Welcome to Overseer UI!");
        ImGui.checkbox("Demo Window", showDemoWindow);
        ImGui.colorEdit4("Clear Color", clearColor);

        if (ImGui.button("Close Screen")) {
            this.onClose();
        }
        ImGui.end();

        // Demo window if enabled
        if (showDemoWindow) {
            ImGui.showDemoWindow();
        }
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
