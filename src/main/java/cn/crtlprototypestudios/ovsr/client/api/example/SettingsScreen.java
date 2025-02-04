package cn.crtlprototypestudios.ovsr.client.api.example;

import cn.crtlprototypestudios.ovsr.client.api.components.Component;
import cn.crtlprototypestudios.ovsr.client.api.components.ScreenAction;
import cn.crtlprototypestudios.ovsr.client.api.components.primitives.*;
import cn.crtlprototypestudios.ovsr.client.api.reactive.Ref;
import cn.crtlprototypestudios.ovsr.client.api.reactive.composables.UseState;
import cn.crtlprototypestudios.ovsr.client.impl.screen.ImGuiScreen;
import cn.crtlprototypestudios.ovsr.client.impl.screen.ImGuiWindow;
import cn.crtlprototypestudios.ovsr.client.impl.theme.ImGuiDarkTheme;
import imgui.flag.ImGuiWindowFlags;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SettingsScreen extends ImGuiScreen {
    // Persistent state using useState
    private final Ref<String> username = UseState.useState("settings.username", "", String.class);
    private final Ref<Integer> volume = UseState.useState("settings.volume", 100, Integer.class);
    private final Ref<String> theme = UseState.useState("settings.theme", "Light", String.class);
    private final Ref<Boolean> showAdvanced = UseState.useState("settings.showAdvanced", false, Boolean.class);
    private final Queue<ScreenAction> pendingActions = new ConcurrentLinkedQueue<>();

    // Modal state
    private final Ref<Boolean> showResetConfirm = new Ref<>(false);
    private final Component settingsContent;
    private final Component resetModal;

    private final Window rootWindow;
    private boolean initialized = false;

    public SettingsScreen() {
        super(net.minecraft.network.chat.Component.literal("Settings"), true);
        settingsContent = createSettingsContent();
        resetModal = createResetConfirmModal();
        this.rootWindow = new Window();
        this.rootWindow.withProps(p -> {
            p.set("title", "Settings");
            p.set("flags", ImGuiWindowFlags.AlwaysAutoResize);
        });
    }

    protected void queueAction(Runnable action) {
        pendingActions.offer(new ScreenAction(action));
    }

    @Override
    protected List<ImGuiWindow> initImGui() {
        if (!initialized) {
            rootWindow.withChild(createSettingsContent());
            initialized = true;
        }

        return List.of(new ImGuiWindow(
                new ImGuiDarkTheme(),
                net.minecraft.network.chat.Component.literal("Settings"),
                () -> {
                    synchronized(rootWindow) {
                        rootWindow.internalRender();
                        if (showResetConfirm.get()) {
                            createResetConfirmModal().internalRender();
                        }
                    }
                },
                true
        ));
    }

    private Component createSettingsContent() {
        List<String> themeOptions = Arrays.asList("Dark", "Light", "Classic");

        return new VStack()
                .withProps(p -> p.set("spacing", 10f))
                .withChildren(
                        // Username section
                        new VStack()
                                .withProps(p -> p.set("spacing", 5f))
                                .withChildren(
                                        new Text()
                                                .withProps(p -> p.set("text", "Username")),
                                        new Input()
                                                .withProps(p -> {
                                                    p.setRef("modelValue", username);
                                                    p.set("hint", "Enter your username");
                                                })
                                ),

                        // Theme selection
                        new VStack()
                                .withProps(p -> p.set("spacing", 5f))
                                .withChildren(
                                        new Text()
                                                .withProps(p -> p.set("text", "Theme")),
                                        new Dropdown<>(theme, themeOptions)
                                                .withProps(p -> p.set("label", "Select Theme"))
                                ),

                        // Volume slider
                        new VStack()
                                .withProps(p -> p.set("spacing", 5f))
                                .withChildren(
                                        new Text()
                                                .withProps(p -> p.set("text", "Volume")),
                                        new Slider()
                                                .withProps(p -> {
                                                    p.setRef("value", volume);
                                                    p.set("min", 0);
                                                    p.set("max", 100);
                                                    p.set("format", "%d%%");
                                                })
                                ),

                        // Advanced settings toggle
                        new Checkbox()
                                .withProps(p -> {
                                    p.setRef("checked", showAdvanced);
                                    p.set("label", "Show Advanced Settings");
                                }),

                        // Advanced settings section
                        new Conditional()
                                .withProps(p -> p.setRef("when", showAdvanced))
                                .withChild(
                                        new VStack()
                                                .withProps(p -> p.set("spacing", 5f))
                                                .withChildren(
                                                        new Text()
                                                                .withProps(p -> p.set("text", "Advanced Settings"))
                                                        // Add your advanced settings components here
                                                )
                                ),

                        // Buttons
                        new HStack()
                                .withProps(p -> p.set("spacing", 10f))
                                .withChildren(
                                        new Button()
                                                .withProps(p -> {
                                                    p.set("text", "Save");
                                                    p.setCallback("onClick", () -> queueAction(this::saveSettings));
                                                }),
                                        new Button()
                                                .withProps(p -> {
                                                    p.set("text", "Reset");
                                                    p.setCallback("onClick", () -> queueAction(() -> showResetConfirm.set(true)));
                                                }),
                                        new Button()
                                                .withProps(p -> {
                                                    p.set("text", "Close");
                                                    p.setCallback("onClick", () -> queueAction(this::onClose));
                                                })
                                )
                );
    }

    private cn.crtlprototypestudios.ovsr.client.api.components.Component createResetConfirmModal() {
        return new Modal(showResetConfirm)
                .withProps(p -> p.set("title", "Confirm Reset"))
                .withChildren(
                        new VStack()
                                .withProps(p -> p.set("spacing", 10f))
                                .withChildren(
                                        new Text()
                                                .withProps(p -> p.set("text", "Are you sure you want to reset all settings?")),
                                        new HStack()
                                                .withProps(p -> p.set("spacing", 10f))
                                                .withChildren(
                                                        new Button()
                                                                .withProps(p -> {
                                                                    p.set("text", "Yes");
                                                                    p.setCallback("onClick", () -> queueAction(() -> {
                                                                        resetSettings();
                                                                        showResetConfirm.set(false);
                                                                    }));
                                                                }),
                                                        new Button()
                                                                .withProps(p -> {
                                                                    p.set("text", "No");
                                                                    p.setCallback("onClick", () -> queueAction(() -> showResetConfirm.set(false)));
                                                                })
                                                )
                                )
                );
    }

    private void saveSettings() {
        // Save settings logic here
        // The useState values are automatically persisted
        // Save settings logic here
        queueAction(this::onClose);
    }

    private void resetSettings() {
        queueAction(() -> {
            username.set("");
            volume.set(100);
            theme.set("Dark");
            showAdvanced.set(false);
            showResetConfirm.set(false);
        });
    }
}
