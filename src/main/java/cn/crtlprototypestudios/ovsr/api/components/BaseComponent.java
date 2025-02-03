package cn.crtlprototypestudios.ovsr.api.components;

import cn.crtlprototypestudios.ovsr.api.event.EventHandlerRegistry;
import cn.crtlprototypestudios.ovsr.api.event.UIEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseComponent {
    protected int x, y, width, height;
    protected boolean visible = true;
    protected boolean enabled = true;
    protected Component tooltip;
    protected List<BaseComponent> children = new ArrayList<>();
    protected BaseComponent parent;
    protected String id;

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
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
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

    /**
     * Called when the mouse enters this component
     */
    public void onMouseEnter(int mouseX, int mouseY) {}

    /**
     * Called when the mouse leaves this component
     */
    public void onMouseLeave(int mouseX, int mouseY) {}

    /**
     * Called when this component gains focus
     */
    public void onFocused() {}

    /**
     * Called when this component loses focus
     */
    public void onFocusLost() {}

    /**
     * Handle mouse click events
     * @return true if the event was handled
     */
    public boolean onMouseClick(int mouseX, int mouseY, int button) {
        return false;
    }

    /**
     * Handle mouse release events
     * @return true if the event was handled
     */
    public boolean onMouseRelease(int mouseX, int mouseY, int button) {
        return false;
    }

    /**
     * Handle mouse drag events
     * @return true if the event was handled
     */
    public boolean onMouseDrag(int mouseX, int mouseY, int button, double dragX, double dragY) {
        return false;
    }

    /**
     * Handle key press events
     * @return true if the event was handled
     */
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    /**
     * Handle key release events
     * @return true if the event was handled
     */
    public boolean onKeyRelease(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    /**
     * Handle character typing events
     * @return true if the event was handled
     */
    public boolean onCharTyped(char codePoint, int modifiers) {
        return false;
    }

    /**
     * Render tooltip for this component
     */
    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (tooltip != null) {
            graphics.renderTooltip(
                    Minecraft.getInstance().font,
                    tooltip,
                    mouseX,
                    mouseY
            );
        }
    }

    /**
     * Check if this component contains the given point
     */
    public boolean contains(int x, int y) {
        return isMouseOver(x, y);
    }

    /**
     * Check if this component can receive input focus
     */
    public boolean isInteractive() {
        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object captureState() {
        return null; // Override in components that need state persistence
    }

    public void restoreState(Object state) {
        // Override in components that need state persistence
    }

    protected void fireEvent(String eventType) {
        fireEvent(eventType, null);
    }

    protected void fireEvent(String eventType, Object data) {
        UIEvent event = new UIEvent(this, eventType, data);
        EventHandlerRegistry.getInstance().fireEvent(event);
    }

    // Helper methods for common events
    protected void fireClickEvent() {
        fireEvent("click");
    }

    protected void fireChangeEvent(Object value) {
        fireEvent("change", value);
    }
}
