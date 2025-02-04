package cn.crtlprototypestudios.ovsr.client.api;

import cn.crtlprototypestudios.ovsr.Ovsr;
import cn.crtlprototypestudios.ovsr.client.impl.interfaces.Renderable;
import cn.crtlprototypestudios.ovsr.client.impl.interfaces.Theme;
import cn.crtlprototypestudios.ovsr.client.impl.render.ViewportScaling;
import com.mojang.blaze3d.platform.Window;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class OverseerHUD {

    private static final List<HUDElement> elements = new ArrayList<>();

    public static void addElement(HUDElement element) {
        elements.add(element);
        Ovsr.pushRenderable(element);
    }

    public static void removeElement(HUDElement element) {
        elements.remove(element);
        Ovsr.pullRenderable(element);
    }

    public abstract static class HUDElement implements Renderable {
        // Alignment enums
        public enum VerticalAlignment {
            TOP, CENTER, BOTTOM
        }

        public enum HorizontalAlignment {
            LEFT, CENTER, RIGHT
        }

        protected final Theme theme;
        private final String id;
        protected boolean visible = true;
        protected float x = 0, y = 0;
        protected float width = 0, height = 0;
        protected int windowFlags = ImGuiWindowFlags.NoDecoration |
                ImGuiWindowFlags.NoBackground |
                ImGuiWindowFlags.AlwaysAutoResize |
                ImGuiWindowFlags.NoSavedSettings |
                ImGuiWindowFlags.NoFocusOnAppearing;

        // Position fields
        protected VerticalAlignment verticalAlignment = VerticalAlignment.TOP;
        protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
        protected float xOffset = 0;
        protected float yOffset = 0;

        // Render condition flags
        protected boolean renderWhenPaused = false;
        protected boolean renderInChat = true;
        protected boolean renderInInventory = true;
        protected boolean renderWithGuiHidden = false;
        protected boolean renderInMenus = false;

        protected HUDElement(String id, Theme theme) {
            this.id = id;
            this.theme = theme;
        }

        @Override
        public String getName() {
            return id;
        }

        @Override
        public Theme getTheme() {
            return theme;
        }

        @Override
        public void render() {
            Minecraft mc = Minecraft.getInstance();
            if (!shouldRender(mc)) return;
            if (!visible) return;

            // Get window position
            long window = mc.getWindow().getWindow();
            int[] winX = new int[1];
            int[] winY = new int[1];
            GLFW.glfwGetWindowPos(window, winX, winY);

            // Calculate base position based on alignment
            float baseX = calculateBaseX();
            float baseY = calculateBaseY();

            // Add offsets and window position
            float finalX = baseX + xOffset + ViewportScaling.X_OFFSET + winX[0];
            float finalY = baseY + yOffset + ViewportScaling.Y_OFFSET + winY[0];

            ImGui.setNextWindowPos(finalX, finalY);

            if (ImGui.begin(getName(), windowFlags)) {
                renderContent();
                width = ImGui.getWindowWidth();
                height = ImGui.getWindowHeight();
            }
            ImGui.end();
        }

        private float calculateBaseX() {
            switch (horizontalAlignment) {
                case LEFT:
                    return 0;
                case CENTER:
                    return (ViewportScaling.WIDTH - width) / 2;
                case RIGHT:
                    return ViewportScaling.WIDTH - width;
                default:
                    return 0;
            }
        }

        private float calculateBaseY() {
            switch (verticalAlignment) {
                case TOP:
                    return 0;
                case CENTER:
                    return (ViewportScaling.HEIGHT - height) / 2;
                case BOTTOM:
                    return ViewportScaling.HEIGHT - height;
                default:
                    return 0;
            }
        }

        // Positioning methods
        public HUDElement setAlignment(HorizontalAlignment horizontal, VerticalAlignment vertical) {
            this.horizontalAlignment = horizontal;
            this.verticalAlignment = vertical;
            return this;
        }

        public HUDElement setOffset(float x, float y) {
            this.xOffset = x;
            this.yOffset = y;
            return this;
        }

        // Flag manipulation methods
        public HUDElement addFlags(int flags) {
            this.windowFlags |= flags;
            return this;
        }

        public HUDElement removeFlags(int flags) {
            this.windowFlags &= ~flags;
            return this;
        }

        public HUDElement setFlags(int flags) {
            this.windowFlags = flags;
            return this;
        }

        public boolean hasFlag(int flag) {
            return (windowFlags & flag) != 0;
        }

        protected boolean shouldRender(Minecraft mc) {
            if (mc.player == null) return false;

            // Check pause state
            if (mc.isPaused() && !renderWhenPaused) return false;

            // Check GUI hidden state (F1)
            if (mc.options.hideGui && !renderWithGuiHidden) return false;

            // Check chat screen
            if (mc.screen instanceof ChatScreen && !renderInChat) return false;

            if (mc.screen instanceof InventoryScreen && !renderInInventory) return false;

            // Check other menu screens
            if (mc.screen != null && !(mc.screen instanceof ChatScreen) && !(mc.screen instanceof InventoryScreen) && !renderInMenus) return false;

            return true;
        }

        protected abstract void renderContent();

        // Render condition setters
        public HUDElement setRenderWhenPaused(boolean render) {
            this.renderWhenPaused = render;
            return this;
        }

        public HUDElement setRenderInChat(boolean render) {
            this.renderInChat = render;
            return this;
        }

        public HUDElement setRenderInInventory(boolean render) {
            this.renderInInventory = render;
            return this;
        }

        public HUDElement setRenderWithGuiHidden(boolean render) {
            this.renderWithGuiHidden = render;
            return this;
        }

        public HUDElement setRenderInMenus(boolean render) {
            this.renderInMenus = render;
            return this;
        }

        // Positioning methods using Minecraft window coordinates
        public HUDElement position(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public HUDElement alignCenter() {
            this.x = (ViewportScaling.WIDTH - width) / 2;
            this.y = (ViewportScaling.HEIGHT - height) / 2;
            return this;
        }

        public HUDElement alignCenterHorizontally() {
            Window window = Minecraft.getInstance().getWindow();
            this.x = (window.getGuiScaledWidth() - width) / 2;
            return this;
        }

        public HUDElement alignCenterVertically() {
            Window window = Minecraft.getInstance().getWindow();
            this.y = (window.getGuiScaledHeight() - height) / 2;
            return this;
        }
    }
}



