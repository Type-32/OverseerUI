package cn.crtlprototypestudios.ovsr.api.components.primitives;

import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;
import cn.crtlprototypestudios.ovsr.api.components.ContainerComponent;
import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.ContainerData;
import org.lwjgl.opengl.GL11;

public class ScrollContainerComponent extends ContainerComponent {
    private double scrollX = 0;
    private double scrollY = 0;
    private int contentWidth = 0;
    private int contentHeight = 0;
    private boolean horizontalScroll = false;
    private boolean verticalScroll = true;
    private boolean isDragging = false;
    private double lastMouseX;
    private double lastMouseY;
    private int scrollBarSize = 6;
    private int scrollBarPadding = 2;

    // Scrollbar colors
    private int scrollbarColor = 0x7F000000;
    private int scrollbarHoverColor = 0xAF000000;
    private int scrollbarActiveColor = 0xFF000000;

    public ScrollContainerComponent(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public ScrollContainerComponent(ComponentData data){
        this(data.getIntAttribute("x", 0),
                data.getIntAttribute("y", 0),
                data.getIntAttribute("width", 100),
                data.getIntAttribute("height", 100)
        );
    }

    @Override
    protected void updateLayout() {
        contentWidth = 0;
        contentHeight = 0;

        // Calculate content size based on children
        for (BaseComponent child : children) {
            contentWidth = Math.max(contentWidth, child.getX() + child.getWidth());
            contentHeight = Math.max(contentHeight, child.getY() + child.getHeight());
        }

        // Adjust scroll positions if they exceed bounds
        scrollX = Mth.clamp(scrollX, 0, Math.max(0, contentWidth - width + (verticalScroll ? scrollBarSize : 0)));
        scrollY = Mth.clamp(scrollY, 0, Math.max(0, contentHeight - height + (horizontalScroll ? scrollBarSize : 0)));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;

        // Setup scissor test for content masking
        double scale = Minecraft.getInstance().getWindow().getGuiScale();
        int scissorX = (int) (getAbsoluteX() * scale);
        int scissorY = (int) (Minecraft.getInstance().getWindow().getHeight() - (getAbsoluteY() + height) * scale);
        int scissorWidth = (int) (width * scale);
        int scissorHeight = (int) (height * scale);

        // Enable scissor test
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);

        // Translate graphics for scrolling
        graphics.pose().pushPose();
        graphics.pose().translate(-scrollX, -scrollY, 0);

        // Render children
        for (BaseComponent child : children) {
            child.render(graphics, (int)(mouseX + scrollX), (int)(mouseY + scrollY), partialTicks);
        }

        graphics.pose().popPose();

        // Disable scissor test
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        // Render scrollbars
        renderScrollbars(graphics, mouseX, mouseY);
    }

    private void renderScrollbars(GuiGraphics graphics, int mouseX, int mouseY) {
        // Vertical scrollbar
        if (verticalScroll && contentHeight > height) {
            int scrollbarHeight = (int) ((height - scrollBarPadding * 2) * height / contentHeight);
            int scrollbarY = y + scrollBarPadding + (int) ((height - scrollBarPadding * 2 - scrollbarHeight) * scrollY / (contentHeight - height));
            int scrollbarX = x + width - scrollBarSize - scrollBarPadding;

            boolean hovered = mouseX >= scrollbarX && mouseX <= scrollbarX + scrollBarSize &&
                    mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight;

            int color = isDragging ? scrollbarActiveColor : (hovered ? scrollbarHoverColor : scrollbarColor);
            graphics.fill(scrollbarX, scrollbarY, scrollbarX + scrollBarSize, scrollbarY + scrollbarHeight, color);
        }

        // Horizontal scrollbar
        if (horizontalScroll && contentWidth > width) {
            int scrollbarWidth = (int) ((width - scrollBarPadding * 2) * width / contentWidth);
            int scrollbarX = x + scrollBarPadding + (int) ((width - scrollBarPadding * 2 - scrollbarWidth) * scrollX / (contentWidth - width));
            int scrollbarY = y + height - scrollBarSize - scrollBarPadding;

            boolean hovered = mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
                    mouseY >= scrollbarY && mouseY <= scrollbarY + scrollBarSize;

            int color = isDragging ? scrollbarActiveColor : (hovered ? scrollbarHoverColor : scrollbarColor);
            graphics.fill(scrollbarX, scrollbarY, scrollbarX + scrollbarWidth, scrollbarY + scrollBarSize, color);
        }
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int button) {
        if (!enabled || button != 0) return false;

        // Check if clicking on scrollbar
        if (isOverScrollbar(mouseX, mouseY)) {
            isDragging = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }

        // Propagate click to children with adjusted coordinates
        return super.onMouseClick((int)(mouseX + scrollX), (int)(mouseY + scrollY), button);
    }

    @Override
    public boolean onMouseDrag(int mouseX, int mouseY, int button, double dragX, double dragY) {
        if (!enabled || !isDragging) return false;

        if (verticalScroll && contentHeight > height) {
            double scrollFactor = (double) contentHeight / height;
            scrollY = Mth.clamp(scrollY + (mouseY - lastMouseY) * scrollFactor,
                    0,
                    contentHeight - height);
        }

        if (horizontalScroll && contentWidth > width) {
            double scrollFactor = (double) contentWidth / width;
            scrollX = Mth.clamp(scrollX + (mouseX - lastMouseX) * scrollFactor,
                    0,
                    contentWidth - width);
        }

        lastMouseX = mouseX;
        lastMouseY = mouseY;
        return true;
    }

    @Override
    public boolean onMouseRelease(int mouseX, int mouseY, int button) {
        if (isDragging) {
            isDragging = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double delta) {
        if (!isMouseOver(mouseX, mouseY)) return false;

        if (verticalScroll && contentHeight > height) {
            scrollY = Mth.clamp(scrollY - delta * 16, 0, contentHeight - height);
            return true;
        }
        return false;
    }

    private boolean isOverScrollbar(double mouseX, double mouseY) {
        // Check vertical scrollbar
        if (verticalScroll && contentHeight > height) {
            int scrollbarHeight = (int) ((height - scrollBarPadding * 2) * height / contentHeight);
            int scrollbarY = y + scrollBarPadding + (int) ((height - scrollBarPadding * 2 - scrollbarHeight) * scrollY / (contentHeight - height));
            int scrollbarX = x + width - scrollBarSize - scrollBarPadding;

            if (mouseX >= scrollbarX && mouseX <= scrollbarX + scrollBarSize &&
                    mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight) {
                return true;
            }
        }

        // Check horizontal scrollbar
        if (horizontalScroll && contentWidth > width) {
            int scrollbarWidth = (int) ((width - scrollBarPadding * 2) * width / contentWidth);
            int scrollbarX = x + scrollBarPadding + (int) ((width - scrollBarPadding * 2 - scrollbarWidth) * scrollX / (contentWidth - width));
            int scrollbarY = y + height - scrollBarSize - scrollBarPadding;

            return mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
                    mouseY >= scrollbarY && mouseY <= scrollbarY + scrollBarSize;
        }

        return false;
    }

    public void setHorizontalScroll(boolean enabled) {
        this.horizontalScroll = enabled;
        updateLayout();
    }

    public void setVerticalScroll(boolean enabled) {
        this.verticalScroll = enabled;
        updateLayout();
    }

    public void scrollTo(double x, double y) {
        scrollX = Mth.clamp(x, 0, Math.max(0, contentWidth - width));
        scrollY = Mth.clamp(y, 0, Math.max(0, contentHeight - height));
    }

    public void scrollToTop() {
        scrollY = 0;
    }

    public void scrollToBottom() {
        scrollY = Math.max(0, contentHeight - height);
    }

    @Override
    public boolean contains(int x, int y) {
        return super.contains(x, y) && !isOverScrollbar(x, y);
    }
}
