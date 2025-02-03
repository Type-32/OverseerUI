package cn.crtlprototypestudios.ovsr.impl.render;

import cn.crtlprototypestudios.ovsr.Ovsr;
import com.mojang.blaze3d.platform.Window;
import imgui.*;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGuiDockNode;
import net.minecraft.client.Minecraft;

public class ImGuiManager {
    private static final ImGuiImplGlfw IMPL_GLFW = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 IMPL_GL3 = new ImGuiImplGl3();
    private static boolean initialized = false;

    public static void initialize(long glHandle) {
        if (initialized) return;

        try {
            ImGui.createContext();

            final ImGuiIO io = ImGui.getIO();
            io.setIniFilename(null); // Don't save settings
            io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
            io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
            io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);

            io.setConfigViewportsNoTaskBarIcon(true);

            // Setup font
            final ImFontAtlas fontAtlas = io.getFonts();
            final ImFontConfig fontConfig = new ImFontConfig();
            fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesCyrillic());
            fontAtlas.addFontDefault();

            fontConfig.setMergeMode(true); // When enabled, all fonts added with this config would be merged with the previously added font
            fontConfig.setPixelSnapH(true);

            fontConfig.destroy();

            // Setup style
            ImGui.styleColorsDark();
            ImGuiStyle style = ImGui.getStyle();
            if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                style.setWindowRounding(0.0f);
                // Get the current window background color
                float[] color = new float[] {0F,0F,0F,0F};
                style.getColor(ImGuiCol.WindowBg, new ImVec4(color[0], color[1], color[2], color[3]));
                // Set it back with full alpha
                color[3] = 1.0f;
                style.setColor(ImGuiCol.WindowBg, color[0], color[1], color[2], color[3]);
            }

            // Get GLFW window handle
            IMPL_GLFW.init(glHandle, true);
            IMPL_GL3.init();

            initialized = true;
            Ovsr.LOGGER.info("ImGui initialized successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setupDockspace() {
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;

        Window window = Minecraft.getInstance().getWindow();
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(window.getWidth(), window.getHeight());

        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
        ImGui.begin("OvsrUI Dockspace", windowFlags);
        ImGui.popStyleVar(2);

        int dockspaceId = ImGui.getID("OvsrUIDockspace");
        ImGui.dockSpace(dockspaceId, 0, 0, ImGuiDockNodeFlags.PassthruCentralNode |
                ImGuiDockNodeFlags.NoCentralNode | ImGuiDockNodeFlags.NoDockingInCentralNode);

        // Update viewport scaling based on central dock node
        ImGuiDockNode centralNode = imgui.internal.ImGui.dockBuilderGetCentralNode(dockspaceId);
        if (centralNode != null) {
            ViewportScaling.updateFromDockNode(centralNode);
        }
    }

    public static void endDockspace() {
        ImGui.end();
    }

    public static void onFrameRender() {
        beginFrame();
        endFrame();
    }

    public static void beginFrame() {
        if (!initialized) return;

        IMPL_GLFW.newFrame();
        ImGui.newFrame();

        setupDockspace();
    }

    public static void endFrame() {
        if (!initialized) return;

        endDockspace();

        ImGui.render();
        IMPL_GL3.renderDrawData(ImGui.getDrawData());

        // Update and Render additional Platform Windows
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
        }
    }

    public static void dispose() {
        if (!initialized) return;

        IMPL_GL3.dispose();
        IMPL_GLFW.dispose();
        ImGui.destroyContext();
        initialized = false;
    }
}
