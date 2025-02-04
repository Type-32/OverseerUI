package cn.crtlprototypestudios.ovsr.client.api;

import cn.crtlprototypestudios.ovsr.Ovsr;
import cn.crtlprototypestudios.ovsr.client.impl.interfaces.Renderable;
import cn.crtlprototypestudios.ovsr.client.impl.interfaces.Theme;
import com.mojang.blaze3d.platform.Window;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

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
        protected final Theme theme;
        private final String id;
        protected boolean visible = true;
        protected float x = 0, y = 0;
        protected float width = 0, height = 0;

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

            if (x != 0 || y != 0) {
                ImGui.setNextWindowPos(x, y);
            }

            int flags = ImGuiWindowFlags.NoDecoration |
                    ImGuiWindowFlags.NoBackground |
                    ImGuiWindowFlags.NoInputs |
                    ImGuiWindowFlags.AlwaysAutoResize |
                    ImGuiWindowFlags.NoSavedSettings |
                    ImGuiWindowFlags.NoFocusOnAppearing;

            if (ImGui.begin(getName(), flags)) {
                renderContent();
                width = ImGui.getWindowWidth();
                height = ImGui.getWindowHeight();
            }
            ImGui.end();
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

        public HUDElement alignTop(float padding) {
            this.y = padding;
            return this;
        }

        public HUDElement alignBottom(float padding) {
            Window window = Minecraft.getInstance().getWindow();
            this.y = window.getGuiScaledHeight() - height - padding;
            return this;
        }

        public HUDElement alignLeft(float padding) {
            this.x = padding;
            return this;
        }

        public HUDElement alignRight(float padding) {
            Window window = Minecraft.getInstance().getWindow();
            this.x = window.getGuiScaledWidth() - width - padding;
            return this;
        }

        // Center alignment methods
        public HUDElement alignCenter() {
            Window window = Minecraft.getInstance().getWindow();
            this.x = (window.getGuiScaledWidth() - width) / 2;
            this.y = (window.getGuiScaledHeight() - height) / 2;
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



