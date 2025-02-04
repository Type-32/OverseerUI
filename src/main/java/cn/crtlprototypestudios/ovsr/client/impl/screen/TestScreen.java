package cn.crtlprototypestudios.ovsr.client.impl.screen;

import cn.crtlprototypestudios.ovsr.client.impl.theme.ImGuiDarkTheme;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImString;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TestScreen extends ImGuiScreen {
    private ImFloat floatValue = new ImFloat(0.0f);
    private ImBoolean checkboxValue = new ImBoolean(false);
    private String inputText = "";
    private final ImString inputBuffer = new ImString();

    public TestScreen() {
        super(Component.literal("Overseer UI Test Screen"), true);
    }

    @Override
    protected java.util.List<ImGuiWindow> initImGui() {
        return java.util.List.of(
                // Main demo window
                new ImGuiWindow(
                        new ImGuiDarkTheme(),
                        Component.literal("OverseerUI Demo"),
                        () -> {
                            // Basic widgets
                            ImGui.text("Welcome to OverseerUI Demo!");
                            ImGui.separator();

                            // Slider
                            ImGui.sliderFloat("Float Slider", floatValue.getData(), 0.0f, 1.0f);

                            // Checkbox
                            ImGui.checkbox("Test Checkbox", checkboxValue);

                            // Input text
                            if (ImGui.inputText("Input Text", inputBuffer, ImGuiWindowFlags.None)) {
                                inputText = String.valueOf(inputBuffer).trim();
                            }

                            // Button
                            if (ImGui.button("Click Me!")) {
                                // Show a popup window when clicked
                                ImGui.openPopup("Hello");
                            }

                            // Popup
                            if (ImGui.beginPopup("Hello")) {
                                ImGui.text("Hello from OverseerUI!");
                                if (ImGui.button("Close")) {
                                    ImGui.closeCurrentPopup();
                                }
                                ImGui.endPopup();
                            }

                            // Display values
                            ImGui.separator();
                            ImGui.text("Current Values:");
                            ImGui.text("Slider: " + floatValue);
                            ImGui.text("Checkbox: " + checkboxValue);
                            ImGui.text("Input: " + inputText);
                        },
                        true
                ),

                // Stats window
                new ImGuiWindow(
                        new ImGuiDarkTheme(),
                        Component.literal("Stats"),
                        () -> {
                            ImGui.text("FPS: " + net.minecraft.client.Minecraft.getInstance().getFps());
                            ImGui.text("Render Distance: " +
                                    net.minecraft.client.Minecraft.getInstance().options.renderDistance().get());
                        },
                        true
                )
        );
    }
}
