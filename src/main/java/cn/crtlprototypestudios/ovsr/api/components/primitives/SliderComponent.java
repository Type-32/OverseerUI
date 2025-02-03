package cn.crtlprototypestudios.ovsr.api.components.primitives;

import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class SliderComponent extends BaseComponent {
    private double minValue;
    private double maxValue;
    private double value;
    private double step;
    private boolean vertical;
    private boolean dragging;
    private boolean showValue;
    private Consumer<Double> onValueChange;

    private static final int HANDLE_WIDTH = 8;
    private static final int HANDLE_HEIGHT = 20;

    public SliderComponent(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.minValue = 0.0;
        this.maxValue = 1.0;
        this.value = 0.5;
        this.step = 0.01;
        this.vertical = false;
        this.showValue = false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;

        // Draw track
        int trackColor = isEnabled() ? 0xFF666666 : 0xFF333333;
        int handleColor = isEnabled() ? (dragging ? 0xFFFFFFFF : 0xFFCCCCCC) : 0xFF888888;

        if (vertical) {
            // Vertical track
            graphics.fill(x + (width - 4) / 2, y, x + (width + 4) / 2, y + height, trackColor);

            // Vertical handle
            int handleY = y + (int)((height - HANDLE_WIDTH) * (1.0 - getNormalizedValue()));
            graphics.fill(x, handleY, x + width, handleY + HANDLE_WIDTH, handleColor);
        } else {
            // Horizontal track
            graphics.fill(x, y + (height - 4) / 2, x + width, y + (height + 4) / 2, trackColor);

            // Horizontal handle
            int handleX = x + (int)((width - HANDLE_WIDTH) * getNormalizedValue());
            graphics.fill(handleX, y, handleX + HANDLE_WIDTH, y + height, handleColor);
        }

        // Draw value text if enabled
        if (showValue) {
            String valueText = String.format("%.2f", value);
            int textColor = isEnabled() ? 0xFFFFFFFF : 0xFF666666;
            if (vertical) {
                graphics.drawCenteredString(
                        Minecraft.getInstance().font,
                        valueText,
                        x + width / 2,
                        y + height + 4,
                        textColor
                );
            } else {
                graphics.drawCenteredString(
                        Minecraft.getInstance().font,
                        valueText,
                        x + width / 2,
                        y - 12,
                        textColor
                );
            }
        }
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int button) {
        if (!enabled || button != 0) return false;

        if (isMouseOver(mouseX, mouseY)) {
            dragging = true;
            updateValueFromMouse(mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseDrag(int mouseX, int mouseY, int button, double dragX, double dragY) {
        if (dragging && enabled) {
            updateValueFromMouse(mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseRelease(int mouseX, int mouseY, int button) {
        if (dragging) {
            dragging = false;
            return true;
        }
        return false;
    }

    private void updateValueFromMouse(int mouseX, int mouseY) {
        double normalizedValue;
        if (vertical) {
            normalizedValue = 1.0 - Mth.clamp((mouseY - y) / (double)(height - HANDLE_WIDTH), 0.0, 1.0);
        } else {
            normalizedValue = Mth.clamp((mouseX - x) / (double)(width - HANDLE_WIDTH), 0.0, 1.0);
        }

        double newValue = minValue + (normalizedValue * (maxValue - minValue));

        // Apply stepping if needed
        if (step > 0) {
            newValue = Math.round(newValue / step) * step;
        }

        setValue(newValue);
    }

    private double getNormalizedValue() {
        return (value - minValue) / (maxValue - minValue);
    }

    public void setValue(double value) {
        double oldValue = this.value;
        this.value = Mth.clamp(value, minValue, maxValue);

        if (oldValue != this.value && onValueChange != null) {
            onValueChange.accept(this.value);
        }
    }

    // Getters and setters
    public double getValue() { return value; }
    public double getMinValue() { return minValue; }
    public double getMaxValue() { return maxValue; }
    public double getStep() { return step; }
    public boolean isVertical() { return vertical; }
    public boolean isShowValue() { return showValue; }

    public void setMinValue(double minValue) { this.minValue = minValue; }
    public void setMaxValue(double maxValue) { this.maxValue = maxValue; }
    public void setStep(double step) { this.step = step; }
    public void setVertical(boolean vertical) { this.vertical = vertical; }
    public void setShowValue(boolean showValue) { this.showValue = showValue; }

    public void setOnValueChange(Consumer<Double> onValueChange) {
        this.onValueChange = onValueChange;
    }

    @Override
    public boolean isInteractive() {
        return true;
    }

    @Override
    public Object captureState() {
        return value;
    }

    @Override
    public void restoreState(Object state) {
        if (state instanceof Double) {
            setValue((Double) state);
        }
    }
}
