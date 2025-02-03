package cn.crtlprototypestudios.ovsr.api.components.primitives;

import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;
import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.w3c.dom.Element;

public class LabelComponent extends BaseComponent {
    private Component text;
    private int color = 0xFFFFFF;
    private boolean shadow = true;
    private float scale = 1.0f;
    private TextAlignment alignment = TextAlignment.LEFT;

    public LabelComponent(ComponentData data) {
        super(
                data.getIntAttribute("x", 0),
                data.getIntAttribute("y", 0),
                data.getIntAttribute("width", 0),
                data.getIntAttribute("height", 0)
        );

        // Parse text using the new helper method
        this.text = data.parseTextComponent("text");
        if (this.text == null) {
            this.text = Component.literal(""); // Default empty text
        }

        // Parse other attributes
        this.color = Integer.parseInt(data.getAttribute("color", "FFFFFF"), 16);
        this.shadow = data.getBoolAttribute("shadow", true);
        this.scale = Float.parseFloat(data.getAttribute("scale", "1.0"));
        this.alignment = TextAlignment.valueOf(data.getAttribute("alignment", "LEFT").toUpperCase());

        // Auto-calculate width and height if not specified
        if (this.width == 0) {
            this.width = Minecraft.getInstance().font.width(text);
        }
        if (this.height == 0) {
            this.height = Minecraft.getInstance().font.lineHeight;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;

        var font = Minecraft.getInstance().font;
        int textX = x;

        // Handle alignment
        switch (alignment) {
            case CENTER -> textX = x + (width - font.width(text)) / 2;
            case RIGHT -> textX = x + width - font.width(text);
        }

        // Apply scale if needed
        if (scale != 1.0f) {
            graphics.pose().pushPose();
            graphics.pose().scale(scale, scale, 1.0f);
            textX /= scale;
            int textY = (int) (y / scale);

            if (shadow) {
                graphics.drawString(font, text, textX, textY, color, true);
            } else {
                graphics.drawString(font, text, textX, textY, color);
            }

            graphics.pose().popPose();
        } else {
            if (shadow) {
                graphics.drawString(font, text, textX, y, color, true);
            } else {
                graphics.drawString(font, text, textX, y, color);
            }
        }
    }

    // Getters and setters
    public Component getText() { return text; }
    public void setText(Component text) {
        this.text = text;
        // Recalculate width if auto-sized
        if (width == 0) {
            this.width = Minecraft.getInstance().font.width(text);
        }
    }

    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }

    public boolean hasShadow() { return shadow; }
    public void setShadow(boolean shadow) { this.shadow = shadow; }

    public float getScale() { return scale; }
    public void setScale(float scale) { this.scale = scale; }

    public TextAlignment getAlignment() { return alignment; }
    public void setAlignment(TextAlignment alignment) { this.alignment = alignment; }

    public enum TextAlignment {
        LEFT, CENTER, RIGHT
    }
}
