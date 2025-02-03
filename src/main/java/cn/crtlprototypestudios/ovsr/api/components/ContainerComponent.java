package cn.crtlprototypestudios.ovsr.api.components;

import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerComponent extends BaseComponent {
    private int backgroundColor;
    protected List<BaseComponent> children = new ArrayList<>();
    protected int padding = 0;

    public ContainerComponent(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public ContainerComponent(ComponentData data){
        this(data.getIntAttribute("x", 0),
                data.getIntAttribute("y", 0),
                data.getIntAttribute("width", 100),
                data.getIntAttribute("height", 100));
    }

    public void addChild(BaseComponent child) {
        children.add(child);
        updateLayout(); // Implement in subclasses
    }

    public void removeChild(BaseComponent child) {
        children.remove(child);
        updateLayout();
    }

    protected abstract void updateLayout();

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;

        // Render children
        for (BaseComponent child : children) {
            child.render(graphics, mouseX, mouseY, partialTicks);
        }
    }

    public int getPadding() { return padding; }
    public void setPadding(int padding) {
        this.padding = padding;
        updateLayout();
    }

    @Override
    public boolean isInteractive() {
        return false; // Containers themselves aren't interactive
    }

    public abstract boolean onMouseScroll(double mouseX, double mouseY, double delta);

    @Override
    public boolean contains(int x, int y) {
        // First check if the point is within the container bounds
        if (!super.contains(x, y)) return false;

        // Then check if any child contains the point
        for (BaseComponent child : children) {
            if (child.contains(x, y)) return true;
        }

        return true;
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int button) {
        // Propagate click to children in reverse order (top to bottom)
        for (int i = children.size() - 1; i >= 0; i--) {
            BaseComponent child = children.get(i);
            if (child.isVisible() && child.contains(mouseX, mouseY)) {
                if (child.onMouseClick(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        // Propagate key press to focused child if any
        for (BaseComponent child : children) {
            if (child.isVisible() && child.onKeyPress(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        // Render child tooltips first
        for (BaseComponent child : children) {
            if (child.isVisible() && child.contains(mouseX, mouseY)) {
                child.renderTooltip(graphics, mouseX, mouseY);
                return;
            }
        }

        // Render container tooltip if no child has one
        super.renderTooltip(graphics, mouseX, mouseY);
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }
}
