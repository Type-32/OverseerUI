package cn.crtlprototypestudios.ovsr.client.api;

import cn.crtlprototypestudios.ovsr.client.impl.interfaces.Theme;
import cn.crtlprototypestudios.ovsr.client.impl.screen.ImGuiScreen;
import cn.crtlprototypestudios.ovsr.client.impl.screen.ImGuiWindow;
import cn.crtlprototypestudios.ovsr.client.impl.screen.WindowRenderer;
import cn.crtlprototypestudios.ovsr.client.impl.theme.ImGuiDarkTheme;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import net.minecraft.network.chat.Component;

public abstract class OverseerScreen extends ImGuiScreen {
    protected OverseerScreen(Component title) {
        super(title, true);
    }

    // Builder pattern for window creation
    protected WindowBuilder window(String title) {
        return new WindowBuilder(title);
    }

    public class WindowBuilder {
        private final String title;
        private ImBoolean closeable = new ImBoolean(true);
        private Theme theme = new ImGuiDarkTheme();
        private int flags = 0;  // ImGui uses integer flags

        public WindowBuilder(String title) {
            this.title = title;
        }

        public WindowBuilder notCloseable() {
            this.closeable.set(false);
            return this;
        }

        public WindowBuilder theme(Theme theme) {
            this.theme = theme;
            return this;
        }

        public WindowBuilder addFlag(int imguiFlag) {
            this.flags |= imguiFlag;  // Bitwise OR to combine flags
            return this;
        }


        public WindowBuilder removeFlags(int flags) {
            this.flags &= ~flags;
            return this;
        }

        public WindowBuilder setFlags(int flags) {
            this.flags = flags;
            return this;
        }

        public ImGuiWindow build(WindowRenderer renderer) {
            return new ImGuiWindow(theme,
                    Component.literal(title),
                    () -> {
                        // Set window flags before rendering content
                        if (flags != 0) {
                            ImGui.begin(title, closeable, flags);
                        }
                        renderer.renderWindow();
                    },
                    closeable.get());
        }
    }
}


