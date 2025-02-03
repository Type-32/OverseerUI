package cn.crtlprototypestudios.ovsr.api.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseComponent {
    protected int x, y, width, height;
    protected boolean visible = true;
    protected Component tooltip;
    protected List<BaseComponent> children = new ArrayList<>();
    protected BaseComponent parent;

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

    public void setWidth(int cellWidth) {
        this.width = cellWidth;
    }

    public void setHeight(int cellHeight) {
        this.height = cellHeight;
    }

    public void addChild(BaseComponent child) {
        children.add(child);
        child.setParent(this);
        onChildAdded(child);
    }

    public void removeChild(BaseComponent child) {
        children.remove(child);
        child.setParent(null);
        onChildRemoved(child);
    }

    public void clearChildren() {
        for (BaseComponent child : new ArrayList<>(children)) {
            removeChild(child);
        }
    }

    public List<BaseComponent> getChildren() {
        return children;
    }

    protected void setParent(BaseComponent parent) {
        this.parent = parent;
    }

    public BaseComponent getParent() {
        return parent;
    }

    // Optional hooks for layouts to respond to child changes
    protected void onChildAdded(BaseComponent child) {}
    protected void onChildRemoved(BaseComponent child) {}

    // Get absolute position (considering parent positions)
    public int getAbsoluteX() {
        return (parent != null ? parent.getAbsoluteX() : 0) + x;
    }

    public int getAbsoluteY() {
        return (parent != null ? parent.getAbsoluteY() : 0) + y;
    }
}
