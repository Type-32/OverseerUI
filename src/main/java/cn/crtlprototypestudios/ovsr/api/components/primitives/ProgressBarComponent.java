package cn.crtlprototypestudios.ovsr.api.components.primitives;

import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;
import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ProgressBarComponent extends BaseComponent {
    private static final ResourceLocation PROGRESS_TEXTURE = new ResourceLocation("ovsr", "textures/gui/progress_bar.png");

    private float progress = 0.0f; // 0.0 to 1.0
    private float targetProgress = 0.0f;
    private float animationSpeed = 0.05f;
    private boolean animated = true;
    private ProgressStyle style = ProgressStyle.VANILLA;
    private Direction direction = Direction.RIGHT;

    // Colors for different styles
    private int backgroundColor = 0xFF000000;
    private int borderColor = 0xFF555555;
    private int fillColor = 0xFF00FF00;
    private int fillColorSecondary = 0xFF55FF55; // For gradient or animated effects

    // Optional text display
    private boolean showPercentage = false;
    private String format = "%.0f%%";
    private int textColor = 0xFFFFFF;

    public ProgressBarComponent(ComponentData data) {
        super(
                data.getIntAttribute("x", 0),
                data.getIntAttribute("y", 0),
                data.getIntAttribute("width", 100),
                data.getIntAttribute("height", 16)
        );

        // Parse attributes
        this.progress = data.getFloatAttribute("progress", 0.0f);
        this.targetProgress = progress;
        this.animated = data.getBoolAttribute("animated", true);
        this.animationSpeed = data.getFloatAttribute("animation-speed", 0.05f);
        this.style = ProgressStyle.valueOf(data.getAttribute("style", "VANILLA").toUpperCase());
        this.direction = Direction.valueOf(data.getAttribute("direction", "RIGHT").toUpperCase());

        // Parse colors
        this.backgroundColor = Integer.parseInt(data.getAttribute("background-color", "000000"), 16);
        this.borderColor = Integer.parseInt(data.getAttribute("border-color", "555555"), 16);
        this.fillColor = Integer.parseInt(data.getAttribute("fill-color", "00FF00"), 16);
        this.fillColorSecondary = Integer.parseInt(data.getAttribute("fill-color-secondary", "55FF55"), 16);

        // Parse text options
        this.showPercentage = data.getBoolAttribute("show-percentage", false);
        this.format = data.getAttribute("format", "%.0f%%");
        this.textColor = Integer.parseInt(data.getAttribute("text-color", "FFFFFF"), 16);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;

        // Animate progress
        if (animated && progress != targetProgress) {
            if (Math.abs(targetProgress - progress) < animationSpeed) {
                progress = targetProgress;
            } else if (targetProgress > progress) {
                progress += animationSpeed;
            } else {
                progress -= animationSpeed;
            }
        }

        switch (style) {
            case VANILLA -> renderVanilla(graphics);
            case MODERN -> renderModern(graphics);
            case MINIMAL -> renderMinimal(graphics);
            case GRADIENT -> renderGradient(graphics);
        }

        // Render percentage text if enabled
        if (showPercentage) {
            String text = String.format(format, progress * 100);
            int textWidth = Minecraft.getInstance().font.width(text);
            int textX = x + (width - textWidth) / 2;
            int textY = y + (height - 8) / 2;

            graphics.drawString(
                    Minecraft.getInstance().font,
                    text,
                    textX,
                    textY,
                    textColor
            );
        }
    }

    private void renderVanilla(GuiGraphics graphics) {
        // Draw background
        graphics.blit(PROGRESS_TEXTURE, x, y, 0, 0, width, height, 256, 20);

        // Draw progress bar
        int progressWidth = (int)(width * progress);
        if (progressWidth > 0) {
            graphics.blit(PROGRESS_TEXTURE, x, y, 0, 20, progressWidth, height, 256, 20);
        }
    }

    private void renderModern(GuiGraphics graphics) {
        // Draw background with rounded corners
        fillRoundedRect(graphics, x, y, width, height, backgroundColor, 3);

        // Draw progress
        int progressWidth = (int)(width * progress);
        if (progressWidth > 0) {
            fillRoundedRect(graphics, x, y, progressWidth, height, fillColor, 3);
        }

        // Draw border with rounded corners
        drawRoundedRectOutline(graphics, x, y, width, height, borderColor, 3);
    }

    private void renderMinimal(GuiGraphics graphics) {
        // Draw simple background
        graphics.fill(x, y, x + width, y + height, backgroundColor);

        // Draw progress
        int progressWidth = (int)(width * progress);
        if (progressWidth > 0) {
            graphics.fill(x, y, x + progressWidth, y + height, fillColor);
        }

        // Draw border
        graphics.fill(x, y, x + width, y + 1, borderColor); // Top
        graphics.fill(x, y + height - 1, x + width, y + height, borderColor); // Bottom
        graphics.fill(x, y, x + 1, y + height, borderColor); // Left
        graphics.fill(x + width - 1, y, x + width, y + height, borderColor); // Right
    }

    private void renderGradient(GuiGraphics graphics) {
        // Draw background
        graphics.fill(x, y, x + width, y + height, backgroundColor);

        // Draw progress with gradient
        int progressWidth = (int)(width * progress);
        if (progressWidth > 0) {
            for (int i = 0; i < progressWidth; i++) {
                float ratio = (float)i / progressWidth;
                int color = interpolateColors(fillColor, fillColorSecondary, ratio);
                graphics.fill(x + i, y, x + i + 1, y + height, color);
            }
        }
    }

    private void fillRoundedRect(GuiGraphics graphics, int x, int y, int width, int height, int color, int radius) {
        // Main rectangle
        graphics.fill(x + radius, y, x + width - radius, y + height, color);
        // Left and right rectangles
        graphics.fill(x, y + radius, x + radius, y + height - radius, color);
        graphics.fill(x + width - radius, y + radius, x + width, y + height - radius, color);

        // Corners
        fillCircle(graphics, x + radius, y + radius, radius, color);
        fillCircle(graphics, x + width - radius, y + radius, radius, color);
        fillCircle(graphics, x + radius, y + height - radius, radius, color);
        fillCircle(graphics, x + width - radius, y + height - radius, radius, color);
    }

    private void drawRoundedRectOutline(GuiGraphics graphics, int x, int y, int width, int height, int color, int radius) {
        // Horizontal lines
        graphics.fill(x + radius, y, x + width - radius, y + 1, color);
        graphics.fill(x + radius, y + height - 1, x + width - radius, y + height, color);

        // Vertical lines
        graphics.fill(x, y + radius, x + 1, y + height - radius, color);
        graphics.fill(x + width - 1, y + radius, x + width, y + height - radius, color);

        // Corner outlines
        drawCircleOutline(graphics, x + radius, y + radius, radius, color);
        drawCircleOutline(graphics, x + width - radius, y + radius, radius, color);
        drawCircleOutline(graphics, x + radius, y + height - radius, radius, color);
        drawCircleOutline(graphics, x + width - radius, y + height - radius, radius, color);
    }

    private void fillCircle(GuiGraphics graphics, int centerX, int centerY, int radius, int color) {
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                if (x * x + y * y <= radius * radius) {
                    graphics.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, color);
                }
            }
        }
    }

    private void drawCircleOutline(GuiGraphics graphics, int centerX, int centerY, int radius, int color) {
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                int distSq = x * x + y * y;
                if (distSq <= radius * radius && distSq >= (radius-1) * (radius-1)) {
                    graphics.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, color);
                }
            }
        }
    }

    private int interpolateColors(int color1, int color2, float ratio) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (int)(r1 + (r2 - r1) * ratio);
        int g = (int)(g1 + (g2 - g1) * ratio);
        int b = (int)(b1 + (b2 - b1) * ratio);

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public void setProgress(float progress) {
        this.targetProgress = Mth.clamp(progress, 0.0f, 1.0f);
        if (!animated) {
            this.progress = this.targetProgress;
        }
    }

    public enum ProgressStyle {
        VANILLA,  // Classic Minecraft style
        MODERN,   // Rounded corners with clean look
        MINIMAL,  // Simple rectangular design
        GRADIENT  // Gradient fill effect
    }

    public enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }
}

