package cn.crtlprototypestudios.ovsr.api.components;

import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerComponent extends BaseComponent {
    protected List<BaseComponent> children = new ArrayList<>();
    protected int padding = 0;

    public ContainerComponent(int x, int y, int width, int height) {
        super(x, y, width, height);
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
}
