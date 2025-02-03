package cn.crtlprototypestudios.ovsr.api.components.primitives;

import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;
import cn.crtlprototypestudios.ovsr.api.components.Clickable;
import cn.crtlprototypestudios.ovsr.api.components.Hoverable;
import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class InputBoxComponent extends BaseComponent implements Clickable, Hoverable {
    private String value = "";
    private String placeholder = "";
    private int maxLength = 32;
    private boolean focused;
    private int cursorPos;
    private int selectionPos;
    private int frameCount;
    private boolean visible = true;
    private boolean password = false;
    private boolean editable = true;
    private int textColor = 0xE0E0E0;
    private int placeholderColor = 0x707070;
    private int backgroundColor = 0x80000000;
    private int borderColor = 0xFF000000;
    private Predicate<String> validator = s -> true;
    private Consumer<String> onValueChange;

    public InputBoxComponent(ComponentData data) {
        super(
                data.getIntAttribute("x", 0),
                data.getIntAttribute("y", 0),
                data.getIntAttribute("width", 200),
                data.getIntAttribute("height", 20)
        );

        // Parse attributes
        this.value = data.getChildText("value") != null ? data.getChildText("value") : "";
        this.placeholder = data.getChildText("placeholder") != null ? data.getChildText("placeholder") : "";
        this.maxLength = data.getIntAttribute("max-length", 32);
        this.textColor = Integer.parseInt(data.getAttribute("text-color", "E0E0E0"), 16);
        this.placeholderColor = Integer.parseInt(data.getAttribute("placeholder-color", "707070"), 16);
        this.backgroundColor = Integer.parseInt(data.getAttribute("background-color", "80000000"), 16);
        this.borderColor = Integer.parseInt(data.getAttribute("border-color", "FF000000"), 16);

        // Initialize cursor at end of text
        this.cursorPos = this.value.length();
        this.selectionPos = this.cursorPos;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;

        // Draw background
        graphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, borderColor);
        graphics.fill(x, y, x + width, y + height, backgroundColor);

        int textY = y + (height - 8) / 2;

        // Draw placeholder if no text and not focused
        if (value.isEmpty() && !focused) {
            graphics.drawString(
                    Minecraft.getInstance().font,
                    placeholder,
                    x + 4,
                    textY,
                    placeholderColor
            );
            return;
        }

        // Calculate scroll offset for long text
        int textWidth = Minecraft.getInstance().font.width(value);
        int maxTextWidth = width - 8;
        int scrollOffset = Math.max(0, textWidth - maxTextWidth);

        // Draw text
        String visibleText = Minecraft.getInstance().font.plainSubstrByWidth(value, maxTextWidth);
        graphics.drawString(
                Minecraft.getInstance().font,
                visibleText,
                x + 4,
                textY,
                textColor
        );

        // Draw cursor and selection when focused
        if (focused) {
            frameCount++;

            // Calculate cursor position
            int cursorX = x + 4 + Minecraft.getInstance().font.width(value.substring(0, cursorPos)) - scrollOffset;

            // Draw selection highlight
            if (cursorPos != selectionPos) {
                int selectionStart = Math.min(cursorPos, selectionPos);
                int selectionEnd = Math.max(cursorPos, selectionPos);
                int selectionStartX = x + 4 + Minecraft.getInstance().font.width(value.substring(0, selectionStart)) - scrollOffset;
                int selectionEndX = x + 4 + Minecraft.getInstance().font.width(value.substring(0, selectionEnd)) - scrollOffset;

                graphics.fill(
                        selectionStartX,
                        textY - 1,
                        selectionEndX,
                        textY + 9,
                        0xFF3399FF
                );
            }

            // Draw blinking cursor
            if ((frameCount / 6) % 2 == 0) {
                graphics.fill(cursorX, textY - 1, cursorX + 1, textY + 9, 0xFFFFFFFF);
            }
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focused) return false;

        switch (keyCode) {
            case 259: // Backspace
                if (value.isEmpty()) return false;
                if (cursorPos > 0) {
                    deleteChars(-1);
                }
                return true;

            case 261: // Delete
                if (value.isEmpty()) return false;
                if (cursorPos < value.length()) {
                    deleteChars(1);
                }
                return true;

            case 262: // Right
                moveCursor(1, modifiers);
                return true;

            case 263: // Left
                moveCursor(-1, modifiers);
                return true;

            case 264: // Down
            case 265: // Up
                return false;

            case 257: // Enter
                setFocused(false);
                return true;

            default:
                return false;
        }
    }

    public boolean charTyped(char c, int modifiers) {
        if (!focused) return false;

        if (SharedConstants.isAllowedChatCharacter(c)) {
            if (cursorPos != selectionPos) {
                deleteSelectedText();
            }

            String newText = new StringBuilder(value)
                    .insert(cursorPos, c)
                    .toString();

            if (newText.length() <= maxLength && validator.test(newText)) {
                value = newText;
                moveCursor(1, 0);
                onValueChanged();
            }

            return true;
        }

        return false;
    }

    private void deleteChars(int offset) {
        if (cursorPos != selectionPos) {
            deleteSelectedText();
            return;
        }

        int start = offset < 0 ? cursorPos + offset : cursorPos;
        int end = offset < 0 ? cursorPos : cursorPos + offset;

        if (start >= 0 && end <= value.length()) {
            String newText = new StringBuilder(value)
                    .delete(start, end)
                    .toString();

            if (validator.test(newText)) {
                value = newText;
                moveCursor(offset, 0);
                onValueChanged();
            }
        }
    }

    private void deleteSelectedText() {
        int start = Math.min(cursorPos, selectionPos);
        int end = Math.max(cursorPos, selectionPos);

        String newText = new StringBuilder(value)
                .delete(start, end)
                .toString();

        if (validator.test(newText)) {
            value = newText;
            cursorPos = start;
            selectionPos = cursorPos;
            onValueChanged();
        }
    }

    private void moveCursor(int offset, int modifiers) {
        boolean shift = (modifiers & 1) != 0;

        if (!shift) {
            selectionPos = cursorPos;
        }

        cursorPos = Mth.clamp(cursorPos + offset, 0, value.length());

        if (!shift) {
            selectionPos = cursorPos;
        }
    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button) {
        boolean wasOver = isMouseOver(mouseX, mouseY);
        setFocused(wasOver);

        if (wasOver && focused) {
            int textX = (int) (mouseX - (x + 4));
            String visibleText = value;
            cursorPos = Minecraft.getInstance().font.plainSubstrByWidth(visibleText, textX).length();
            selectionPos = cursorPos;
        }

        return wasOver;
    }

    @Override
    public boolean onRelease(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public void onHover(double mouseX, double mouseY) {
        // Not needed for input box
    }

    @Override
    public void onUnhover() {
        // Not needed for input box
    }

    private void onValueChanged() {
        if (onValueChange != null) {
            onValueChange.accept(value);
        }
    }

    // Getters and setters
    public String getValue() { return value; }
    public void setValue(String value) {
        if (validator.test(value)) {
            this.value = value;
            this.cursorPos = value.length();
            this.selectionPos = this.cursorPos;
            onValueChanged();
        }
    }

    public String getPlaceholder() { return placeholder; }
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }

    public boolean isFocused() { return focused; }
    public void setFocused(boolean focused) { this.focused = focused; }

    public void setValidator(Predicate<String> validator) { this.validator = validator; }
    public void setOnValueChange(Consumer<String> onValueChange) { this.onValueChange = onValueChange; }

    @Override
    public boolean isInteractive() {
        return true; // Input boxes are always interactive
    }

    @Override
    public void onFocused() {
        setFocused(true);
    }

    @Override
    public void onFocusLost() {
        setFocused(false);
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        return keyPressed(keyCode, scanCode, modifiers); // Delegate to existing method
    }

    @Override
    public boolean onCharTyped(char codePoint, int modifiers) {
        return charTyped(codePoint, modifiers); // Delegate to existing method
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int button) {
        return onClick(mouseX, mouseY, button); // Delegate to existing method
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (tooltip != null && isMouseOver(mouseX, mouseY) && !focused) {
            graphics.renderTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
        }
    }

    public void setMaxLength(int intAttribute) {
        this.maxLength = intAttribute;
    }

    public void setPassword(boolean password) {
        this.password = password;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setTextFilter(Predicate<String> predicate) {
        validator = predicate;
    }
}

