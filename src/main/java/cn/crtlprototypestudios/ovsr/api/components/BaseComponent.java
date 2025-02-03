package cn.crtlprototypestudios.ovsr.api.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public abstract class BaseComponent {
    protected int x, y, width, height;
    protected boolean visible = true;
    protected Component tooltip;

    public BaseComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);

    public boolean isMouseOver(double mouseX, double mouseY) {
        return visible && mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    // Getters and setters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public Component getTooltip() { return tooltip; }
    public void setTooltip(Component tooltip) { this.tooltip = tooltip; }
}
