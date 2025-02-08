package cn.crtlprototypestudios.ovsr.client.impl.render;

import cn.crtlprototypestudios.ovsr.Ovsr;
import cn.crtlprototypestudios.ovsr.client.impl.interfaces.Renderable;
import com.mojang.blaze3d.platform.Window;
import imgui.*;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGuiDockNode;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.InputStream;

@OnlyIn(Dist.CLIENT)
public class ImGuiManager {
    private static final ImGuiImplGlfw IMPL_GLFW = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 IMPL_GL3 = new ImGuiImplGl3();
    private static boolean initialized = false;
    private static long windowHandle;

    public static void onGlfwInit(long handle) {
        if (initialized) return;


        // Change this line
//        GLFW.glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
//            ImGui.getIO().setKeysDown(key, action != GLFW.GLFW_RELEASE);
//        });

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

        initializeImGui(handle);
        IMPL_GLFW.init(handle, true);
        IMPL_GL3.init("#version 410"); // Specify GLSL version for macOS

        windowHandle = handle;
        initialized = true;

        Ovsr.LOGGER.info("ImGui initialized successfully");
    }

    public static void initializeImGui(long glHandle) {
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
            final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder();
            final ImFontAtlas fontAtlas = io.getFonts();
            final ImFontConfig fontConfig = new ImFontConfig();

            rangesBuilder.addRanges(fontAtlas.getGlyphRangesDefault());
            rangesBuilder.addRanges(fontAtlas.getGlyphRangesChineseSimplifiedCommon());
//            rangesBuilder.addRanges(fontAtlas.getGlyphRangesChineseFull());
            rangesBuilder.addRanges(fontAtlas.getGlyphRangesCyrillic());

            short[] glyphRanges = rangesBuilder.buildRanges();
            fontConfig.setGlyphRanges(glyphRanges);
//            fontConfig.setSizePixels(16.0f);  // Slightly larger size
//            fontConfig.setOversampleH(2);     // Horizontal oversampling
//            fontConfig.setOversampleV(2);     // Vertical oversampling
//            fontConfig.setRasterizerMultiply(1.2f);  // Make the font slightly bolder
            fontAtlas.addFontDefault(fontConfig);

            // Optionally, add a specific Chinese font
            try {
                // Load font from resources
                InputStream is = ImGuiManager.class.getResourceAsStream("/assets/ovsr/fonts/NotoSansSC-Medium.ttf");
                if (is != null) {
                    byte[] fontData = is.readAllBytes();
                    fontConfig.setMergeMode(true);
//                    fontConfig.setSizePixels(16.0f);
//                    fontConfig.setOversampleH(2);
//                    fontConfig.setOversampleV(2);
//                    fontConfig.setRasterizerMultiply(1.2f);
                    fontAtlas.addFontFromMemoryTTF(fontData, 16.0f, fontConfig, glyphRanges);
                    is.close();
                }
            } catch (IOException e) {
                Ovsr.LOGGER.error("Failed to load Chinese font", e);
            }

            fontConfig.setMergeMode(true); // When enabled, all fonts added with this config would be merged with the previously added font
            fontConfig.setPixelSnapH(true);

            fontConfig.destroy();

            // Setup style
            ImGui.styleColorsDark();
            if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                final ImGuiStyle style = ImGui.getStyle();
                style.setWindowRounding(1.0f);
                style.setColor(ImGuiCol.WindowBg, ImGui.getColorU32(ImGuiCol.WindowBg, 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setupDockspace() {
        int windowFlags = ImGuiWindowFlags.NoDocking;

        Window window = Minecraft.getInstance().getWindow();
        ImGui.setNextWindowPos(window.getX(), window.getY(), ImGuiCond.Always);
        ImGui.setNextWindowSize(window.getWidth(), window.getHeight());

        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.NoBackground |
                ImGuiWindowFlags.NoNavInputs;

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
        ImGui.begin("Overseer UI Dockspace", windowFlags);
        ImGui.popStyleVar(2);

        int dockspaceId = ImGui.dockSpace(Ovsr.getDockId(), 0, 0, ImGuiDockNodeFlags.PassthruCentralNode |
                ImGuiDockNodeFlags.NoCentralNode | ImGuiDockNodeFlags.NoDockingInCentralNode);

        // Update viewport scaling based on central dock node
        ImGuiDockNode centralNode = imgui.internal.ImGui.dockBuilderGetCentralNode(dockspaceId);
        if (centralNode != null) {
            ViewportScaling.updateFromDockNode(centralNode);
        }
    }

    public static void endDockspace(){
        ImGui.end();
    }

    public static void onFrameRender() {
        if (!initialized) return;

        IMPL_GLFW.newFrame();
        ImGui.newFrame();
        setupDockspace();

        // User render code.
        for (Renderable renderable: Ovsr.renderstack) {
            renderable.getTheme().preRender();
            renderable.render();
            renderable.getTheme().postRender();
        }

        for (Renderable renderable : Ovsr.toRemove) {
            Ovsr.pullRenderable(renderable);
        }
        Ovsr.toRemove.clear();

        endDockspace();
        ImGui.render();
        endFrame();
    }

    public static void endFrame() {
        if (!initialized) return;

        IMPL_GL3.renderDrawData(ImGui.getDrawData());

        // Update and Render additional Platform Windows
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
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
