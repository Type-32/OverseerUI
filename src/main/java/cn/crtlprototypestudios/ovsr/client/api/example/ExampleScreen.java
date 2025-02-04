package cn.crtlprototypestudios.ovsr.client.api.example;

import cn.crtlprototypestudios.ovsr.client.api.OverseerScreen;
import cn.crtlprototypestudios.ovsr.client.impl.screen.ImGuiWindow;
import cn.crtlprototypestudios.ovsr.client.impl.theme.ImGuiDarkTheme;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ExampleScreen extends OverseerScreen {
    public ExampleScreen() {
        super(Component.literal("Example Screen"));
    }

    @Override
    protected List<ImGuiWindow> initImGui() {
        return List.of(
                window("Settings")
                        .theme(new ImGuiDarkTheme())
                        .addFlag(ImGuiWindowFlags.NoResize)
                        .build(() -> {
                            ImGui.text("Hello World!");
                            if (ImGui.button("Click me!")) {
                                // Handle click
                            }
                        }),

                window("Statistics")
                        .notCloseable()
                        .addFlag(ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)
                        .build(() -> {
                            ImGui.text("Player Stats:");
                            ImGui.bulletText("Health: 20");
                            ImGui.bulletText("XP: 30");
                        })
        );
    }
}

