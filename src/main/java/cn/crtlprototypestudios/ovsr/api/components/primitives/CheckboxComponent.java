package cn.crtlprototypestudios.ovsr.api.components.primitives;

import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;
import cn.crtlprototypestudios.ovsr.api.components.Clickable;
import cn.crtlprototypestudios.ovsr.api.components.Hoverable;
import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.util.function.Consumer;

public class CheckboxComponent extends BaseComponent implements Clickable, Hoverable {
    private static final ResourceLocation CHECKBOX_TEXTURE = new ResourceLocation("ovsr", "textures/gui/checkbox.png");
    private static final int CHECKBOX_SIZE = 20; // Base size for the checkbox

    private Component text;
    private boolean checked;
    private boolean hovered;
    private boolean active = true;
    private Consumer<Boolean> onValueChange;
    private CheckboxStyle style = CheckboxStyle.VANILLA;
    private int textColor = 0xFFFFFF;
    private float animationProgress = 0f; // For smooth transitions
    private long lastToggleTime = 0L;

    public CheckboxComponent(ComponentData data) {
        super(
                data.getIntAttribute("x", 0),
                data.getIntAttribute("y", 0),
                data.getIntAttribute("width", 0),
                data.getIntAttribute("height", 20)
        );

        // Parse text
        this.text = data.parseTextComponent("text");
        if (this.text == null) {
            this.text = Component.literal("");
        }

        // Parse other attributes
        this.checked = data.getBoolAttribute("checked", false);
        this.active = data.getBoolAttribute("active", true);
        this.style = CheckboxStyle.valueOf(data.getAttribute("style", "VANILLA").toUpperCase());
        this.textColor = Integer.parseInt(data.getAttribute("text-color", "FFFFFF"), 16);

        // Calculate width if not specified
        if (this.width == 0) {
            this.width = CHECKBOX_SIZE + 4 + Minecraft.getInstance().font.width(text);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;

        this.hovered = isMouseOver(mouseX, mouseY);

        // Update animation
        long currentTime = System.currentTimeMillis();
        float targetProgress = checked ? 1.0f : 0.0f;
        if (animationProgress != targetProgress) {
            float delta = (currentTime - lastToggleTime) / 200.0f; // 200ms animation
            if (checked) {
                animationProgress = Math.min(1.0f, animationProgress + delta);
            } else {
                animationProgress = Math.max(0.0f, animationProgress - delta);
            }
        }

        switch (style) {
            case VANILLA -> renderVanilla(graphics);
            case MODERN -> renderModern(graphics);
            case MINIMAL -> renderMinimal(graphics);
        }
    }

    private void renderVanilla(GuiGraphics graphics) {
        RenderSystem.enableBlend();

        // Draw checkbox background
        graphics.blit(
                CHECKBOX_TEXTURE,
                x, y,
                active ? (hovered ? 20 : 0) : 40, // X in texture
                0, // Y in texture
                20, 20 // Width and height
        );

        // Draw check mark with animation
        if (animationProgress > 0) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, animationProgress);
            graphics.blit(
                    CHECKBOX_TEXTURE,
                    x, y,
                    60, 0, // Position in texture for check mark
                    20, 20
            );
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        // Draw text
        int textY = y + (height - 8) / 2;
        graphics.drawString(
                Minecraft.getInstance().font,
                text,
                x + 24,
                textY,
                active ? textColor : 0x7F7F7F
        );

        RenderSystem.disableBlend();
    }

    private void renderModern(GuiGraphics graphics) {
        // Draw rounded rectangle background
        int backgroundColor = active
                ? (hovered ? 0xFF505050 : 0xFF404040)
                : 0xFF303030;

        // Draw main box
        graphics.fill(x + 2, y + 2, x + 18, y + 18, backgroundColor);

        // Draw rounded corners
        graphics.fill(x + 1, y + 2, x + 2, y + 18, backgroundColor);
        graphics.fill(x + 18, y + 2, x + 19, y + 18, backgroundColor);
        graphics.fill(x + 2, y + 1, x + 18, y + 2, backgroundColor);
        graphics.fill(x + 2, y + 18, x + 18, y + 19, backgroundColor);

        // Draw check mark with animation
        if (animationProgress > 0) {
            int checkColor = 0xFF00FF00;
            float size = 12 * animationProgress;
            float offset = (12 - size) / 2;

            // Draw check mark as a simple line
            graphics.fill(
                    (int)(x + 4 + offset),
                    (int)(y + 9),
                    (int)(x + 4 + offset + size / 2),
                    (int)(y + 9 + size / 2),
                    checkColor
            );
            graphics.fill(
                    (int)(x + 4 + offset + size / 2),
                    (int)(y + 9 + size / 2),
                    (int)(x + 4 + offset + size),
                    (int)(y + 9 - size / 2),
                    checkColor
            );
        }

        // Draw text
        graphics.drawString(
                Minecraft.getInstance().font,
                text,
                x + 24,
                y + (height - 8) / 2,
                active ? textColor : 0x7F7F7F
        );
    }

    private void renderMinimal(GuiGraphics graphics) {
        // Draw box outline
        int outlineColor = active
                ? (hovered ? 0xFFFFFFFF : 0xFFAAAAAA)
                : 0xFF555555;

        graphics.fill(x, y, x + 20, y + 1, outlineColor);
        graphics.fill(x, y + 19, x + 20, y + 20, outlineColor);
        graphics.fill(x, y, x + 1, y + 20, outlineColor);
        graphics.fill(x + 19, y, x + 20, y + 20, outlineColor);

        // Draw check mark with animation
        if (animationProgress > 0) {
            int checkColor = active ? 0xFFFFFFFF : 0xFF555555;
            float size = 14 * animationProgress;
            float offset = (14 - size) / 2;

            // Draw check mark
            graphics.fill(
                    (int)(x + 3 + offset),
                    (int)(y + 10),
                    (int)(x + 3 + offset + size / 2),
                    (int)(y + 10 + size / 2),
                    checkColor
            );
            graphics.fill(
                    (int)(x + 3 + offset + size / 2),
                    (int)(y + 10 + size / 2),
                    (int)(x + 3 + offset + size),
                    (int)(y + 10 - size / 2),
                    checkColor
            );
        }

        // Draw text
        graphics.drawString(
                Minecraft.getInstance().font,
                text,
                x + 24,
                y + (height - 8) / 2,
                active ? textColor : 0x7F7F7F
        );
    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button) {
        if (!active || !isMouseOver(mouseX, mouseY)) return false;

        // Toggle state
        checked = !checked;
        lastToggleTime = System.currentTimeMillis();

        // Play click sound
        Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
                        SoundEvents.UI_BUTTON_CLICK, 1.0F
                )
        );

        // Notify listeners
        if (onValueChange != null) {
            onValueChange.accept(checked);
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

    // Getters and setters
    public boolean isChecked() { return checked; }

    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            this.lastToggleTime = System.currentTimeMillis();
            if (onValueChange != null) {
                onValueChange.accept(checked);
            }
        }
    }

    public void setOnValueChange(Consumer<Boolean> onValueChange) {
        this.onValueChange = onValueChange;
    }

    public enum CheckboxStyle {
        VANILLA,    // Classic Minecraft style
        MODERN,     // Rounded corners with smooth animation
        MINIMAL     // Simple outline with minimal animation
    }
}

