package cn.crtlprototypestudios.ovsr.api.components.primitives;

import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;
import cn.crtlprototypestudios.ovsr.api.components.Clickable;
import cn.crtlprototypestudios.ovsr.api.components.Hoverable;
import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.util.function.Consumer;

public class ButtonComponent extends BaseComponent implements Clickable, Hoverable {
    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");

    private Component text;
    private boolean hovered;
    private boolean active = true;
    private Consumer<ButtonComponent> onClick;
    private ButtonStyle style = ButtonStyle.VANILLA;

    public ButtonComponent(ComponentData data) {
        super(
                data.getIntAttribute("x", 0),
                data.getIntAttribute("y", 0),
                data.getIntAttribute("width", 200),
                data.getIntAttribute("height", 20)
        );

        // Parse text using the new helper method
        this.text = data.parseTextComponent("text");
        if (this.text == null) {
            this.text = Component.literal(""); // Default empty text
        }

        this.active = data.getBoolAttribute("active", true);
        String styleStr = data.getAttribute("style", "VANILLA");
        this.style = ButtonStyle.valueOf(styleStr.toUpperCase());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;

        this.hovered = isMouseOver(mouseX, mouseY);

        switch (style) {
            case VANILLA -> renderVanilla(graphics);
            case FLAT -> renderFlat(graphics);
            case OUTLINE -> renderOutline(graphics);
        }
    }

    private void renderVanilla(GuiGraphics graphics) {
        int i = 1; // Base texture y-offset

        if (!active) {
            i = 0;
        } else if (hovered) {
            i = 2;
        }

        graphics.blit(WIDGETS_LOCATION, x, y, 0, 46 + i * 20, width / 2, height);
        graphics.blit(WIDGETS_LOCATION, x + width / 2, y, 200 - width / 2, 46 + i * 20, width / 2, height);

        int textColor = active ? (hovered ? 0xFFFFA0 : 0xFFFFFF) : 0xA0A0A0;
        int textX = x + (width - Minecraft.getInstance().font.width(text)) / 2;
        int textY = y + (height - 8) / 2;

        graphics.drawCenteredString(Minecraft.getInstance().font, text, textX + width/2, textY, textColor);
    }

    private void renderFlat(GuiGraphics graphics) {
        int backgroundColor = active ?
                (hovered ? 0xFF505050 : 0xFF404040) :
                0xFF303030;

        graphics.fill(x, y, x + width, y + height, backgroundColor);

        int textColor = active ? 0xFFFFFF : 0xA0A0A0;
        graphics.drawCenteredString(
                Minecraft.getInstance().font,
                text,
                x + width/2,
                y + (height - 8) / 2,
                textColor
        );
    }

    private void renderOutline(GuiGraphics graphics) {
        // Draw outline
        graphics.fill(x, y, x + width, y + 1, 0xFFFFFFFF);
        graphics.fill(x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
        graphics.fill(x, y, x + 1, y + height, 0xFFFFFFFF);
        graphics.fill(x + width - 1, y, x + width, y + height, 0xFFFFFFFF);

        // Draw background
        int backgroundColor = active ?
                (hovered ? 0x80505050 : 0x80404040) :
                0x80303030;

        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, backgroundColor);

        int textColor = active ? 0xFFFFFF : 0xA0A0A0;
        graphics.drawCenteredString(
                Minecraft.getInstance().font,
                text,
                x + width/2,
                y + (height - 8) / 2,
                textColor
        );
    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button) {
        if (!active || !isMouseOver(mouseX, mouseY)) return false;

        // Play click sound
        Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
                        SoundEvents.UI_BUTTON_CLICK, 1.0F
                )
        );

        if (onClick != null) {
            onClick.accept(this);
        }
        return true;
    }

    @Override
    public boolean onRelease(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public void onHover(double mouseX, double mouseY) {
        this.hovered = true;
    }

    @Override
    public void onUnhover() {
        this.hovered = false;
    }

    public void setOnClick(Consumer<ButtonComponent> onClick) {
        this.onClick = onClick;
    }

    public Component getText() {
        return text;
    }

    public void setText(MutableComponent text) {
        this.text = text;
    }

    public enum ButtonStyle {
        VANILLA,
        FLAT,
        OUTLINE
    }

    @Override
    public boolean isInteractive() {
        return active; // Buttons are interactive when active
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int button) {
        return onClick(mouseX, mouseY, button); // Delegate to existing onClick method
    }

    @Override
    public void onMouseEnter(int mouseX, int mouseY) {
        onHover(mouseX, mouseY); // Delegate to existing onHover method
    }

    @Override
    public void onMouseLeave(int mouseX, int mouseY) {
        onUnhover(); // Delegate to existing onUnhover method
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (tooltip != null && hovered) {
            graphics.renderTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
        }
    }
}
