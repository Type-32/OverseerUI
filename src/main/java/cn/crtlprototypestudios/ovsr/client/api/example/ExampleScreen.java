package cn.crtlprototypestudios.ovsr.client.api.example;

import cn.crtlprototypestudios.ovsr.client.api.OverseerScreen;
import cn.crtlprototypestudios.ovsr.client.api.OverseerUtility;
import cn.crtlprototypestudios.ovsr.client.api.experimental.OUI;
import cn.crtlprototypestudios.ovsr.client.impl.screen.ImGuiWindow;
import cn.crtlprototypestudios.ovsr.client.impl.theme.ImGuiDarkTheme;
import cn.crtlprototypestudios.ovsr.client.impl.theme.ImGuiLightTheme;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ExampleScreen extends OverseerScreen {
    private final OUI.ContainerStyle style = new OUI.ContainerStyle().rounded(8).bg(OverseerUtility.Colors.rgba(35, 35, 35, 0.9f));
    public ExampleScreen() {
        super(Component.literal("Example Screen"));
    }

    private final WindowBuilder settingsWindow = window("Settings")
            .theme(new ImGuiLightTheme())
            .addFlag(ImGuiWindowFlags.NoResize);
    private final WindowBuilder statsWindow = window("Statistics")
            .notCloseable()
            .addFlag(ImGuiWindowFlags.NoResize);
    private final WindowBuilder declarativeWindow = window("Declarative UI 1")
            .notCloseable();

    @Override
    protected List<ImGuiWindow> initImGui() {
        return List.of(
                settingsWindow.build(() -> {
                            ImGui.text("Hello World!");
                            if (ImGui.button("Click me!")) {
                                // Handle click
                            }
                        }),

                statsWindow.build(() -> {
                            ImGui.text("Player Stats:");
                            ImGui.bulletText("Health: 20");
                            ImGui.bulletText("XP: 30");
                        }),

                declarativeWindow.build(() -> {
                            try {
                                ImGui.text("Graphics");
                                OUI.container("settings", new OUI.ContainerStyle()
                                                .rounded(8)
                                                .bg(OverseerUtility.Colors.rgba(35, 35, 35, 0.9f)),
                                        () -> {
                                            ImGui.text("Graphics");
                                            OUI.flex("column", OUI.JustifyContent.START, OUI.AlignItems.CENTER, () -> {
                                                // Graphics settings
                                                ImGui.text("Graphics");
                                                OUI.py(4);
                                                OUI.grid(2, () -> {
                                                    OUI.gridCell(() -> ImGui.text("Resolution"));
                                                    OUI.gridCell(() -> {
                                                        if (ImGui.button("1920x1080")) {
                                                            // Change resolution
                                                        }
                                                    });
                                                });

                                                OUI.py(8);

                                                // Audio settings
                                                ImGui.text("Audio");
                                                OUI.py(4);
                                                OUI.flex("row", OUI.JustifyContent.CENTER, OUI.AlignItems.CENTER, () -> {
                                                    ImGui.text("Volume");
                                                    OUI.mx(8);
                                                    // Volume slider
                                                    float[] volume = {OUI.<Float>getState("volume", 1.0f)};
                                                    if (ImGui.sliderFloat("##volume", volume, 0, 1)) {
                                                        OUI.setState("volume", volume[0]);
                                                    }
                                                });
                                            });
                                        }
                                );
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                            }
                        })
        );
    }
}

