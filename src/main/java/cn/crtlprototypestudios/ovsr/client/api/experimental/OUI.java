package cn.crtlprototypestudios.ovsr.client.api.experimental;

import cn.crtlprototypestudios.ovsr.client.api.OverseerUtility;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * A very experimental declarative way to approach making UI with ImGUI-java.
 */
public class OUI {
    // Flex alignment enums
    public enum JustifyContent {
        START, CENTER, END, BETWEEN, AROUND
    }

    public enum AlignItems {
        START, CENTER, END, STRETCH
    }

    public enum Width {
        FULL,    // w-full (100%)
        FIT,     // w-fit
        HALF,    // w-1/2 (50%)
        THIRD,   // w-1/3 (33.33%)
        QUARTER, // w-1/4 (25%)
        W_20,    // w-1/5 (20%)
        W_40,    // w-2/5 (40%)
        W_60,    // w-3/5 (60%)
        W_80,    // w-4/5 (80%)
        CUSTOM(0);

        private float percent = 100;

        Width() {}
        Width(float percent) {
            this.percent = percent;
        }

        public float getPercent() {
            return percent / 100f;
        }
    }

    // Simplified flex container
    public static void flex(String direction, JustifyContent justify, AlignItems align, Runnable content) {
        ImGui.beginGroup();
        float startX = ImGui.getCursorPosX();
        float startY = ImGui.getCursorPosY();
        float availWidth = ImGui.getContentRegionAvail().x;

        // Store initial cursor position for alignment calculations
        content.run();

        // Get content size
        float contentWidth = ImGui.getItemRectSize().x;
        float contentHeight = ImGui.getItemRectSize().y;

        // Apply justification
        if ("row".equals(direction)) {
            float x = startX;
            switch (justify) {
                case CENTER:
                    x = startX + (availWidth - contentWidth) / 2;
                    break;
                case END:
                    x = startX + availWidth - contentWidth;
                    break;
                case BETWEEN:
                    // Implement space-between logic
                    break;
                case AROUND:
                    // Implement space-around logic
                    break;
            }
            ImGui.setCursorPosX(x);
        }

        // Apply alignment
        float y = startY;
        switch (align) {
            case CENTER:
                y = startY + (ImGui.getContentRegionAvail().y - contentHeight) / 2;
                break;
            case END:
                y = startY + ImGui.getContentRegionAvail().y - contentHeight;
                break;
            case STRETCH:
                // Implement stretch logic
                break;
        }
        ImGui.setCursorPosY(y);

        ImGui.endGroup();
    }

    // Simplified flex calls
    public static void flexCenter(Runnable content) {
        flex("row", JustifyContent.CENTER, AlignItems.CENTER, content);
    }

    public static void flexBetween(Runnable content) {
        flex("row", JustifyContent.BETWEEN, AlignItems.CENTER, content);
    }

    // Width control
    public static void withWidth(Width width, Runnable content) {
        float availWidth = ImGui.getContentRegionAvail().x;
        float targetWidth = switch (width) {
            case FULL -> availWidth;
            case HALF -> availWidth * 0.5f;
            case THIRD -> availWidth * 0.333f;
            case QUARTER -> availWidth * 0.25f;
            case FIT -> 0; // Let ImGui determine width based on content
            case W_20 -> 1/5F;
            case W_40 -> 2/5F;
            case W_60 -> 3/5F;
            case W_80 -> 4/5F;
            case CUSTOM -> width.getPercent();
        };

        if (width != Width.FIT) {
            ImGui.pushItemWidth(targetWidth);
        }

        content.run();

        if (width != Width.FIT) {
            ImGui.popItemWidth();
        }
    }

    public static void flexRowCenter(Runnable content) {
        flex("row", JustifyContent.CENTER, AlignItems.CENTER, content);
    }

    public static void flexColCenter(Runnable content) {
        flex("column", JustifyContent.CENTER, AlignItems.CENTER, content);
    }

    public static void flexSpaceBetween(Runnable content) {
        flex("row", JustifyContent.BETWEEN, AlignItems.CENTER, content);
    }

    public static void flexEnd(Runnable content) {
        flex("row", JustifyContent.END, AlignItems.CENTER, content);
    }

    // Popup utilities
    public static void popup(String id, Runnable content) {
        if (ImGui.beginPopup(id)) {
            content.run();
            ImGui.endPopup();
        }
    }

    public static void modalPopup(String id, Runnable content) {
        if (ImGui.beginPopupModal(id)) {
            content.run();
            ImGui.endPopup();
        }
    }

    public static void openPopup(String id) {
        ImGui.openPopup(id);
    }

    public static void closeCurrentPopup() {
        ImGui.closeCurrentPopup();
    }

    // For-each with auto-indexing
    public static <T> void each(List<T> items, BiConsumer<T, Integer> render) {
        for (int i = 0; i < items.size(); i++) {
            ImGui.pushID(OverseerUtility.getUniqueId());
            render.accept(items.get(i), i);
            ImGui.popID();
        }
    }

    // Spacing utilities (like Tailwind)
    public static void px(float x) { ImGui.dummy(x, 0); }  // padding-x
    public static void py(float y) { ImGui.dummy(0, y); }  // padding-y
    public static void p(float xy) { ImGui.dummy(xy, xy); } // padding

    // Margin (using sameLine with spacing)
    public static void mx(float x) { ImGui.sameLine(0, x); }

    // Container with common styles
    public static void container(String label, ContainerStyle style, Runnable content) {
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, style.rounding);
        ImGui.pushStyleColor(ImGuiCol.ChildBg, style.bgColor.x, style.bgColor.y, style.bgColor.z, style.bgColor.w);

        ImGui.beginChild(OverseerUtility.hiddenIndexString(label, OverseerUtility.getUniqueId()), style.width, style.height, style.border);
        content.run();
        ImGui.endChild();

        ImGui.popStyleColor();
        ImGui.popStyleVar();
    }

    // Quick style builder
    public static class ContainerStyle {
        float rounding = 0;
        float width = 0;
        float height = 0;
        boolean border = false;
        ImVec4 bgColor = new ImVec4(0,0,0,0);

        public ContainerStyle rounded(float r) {
            this.rounding = r;
            return this;
        }

        public ContainerStyle size(float w, float h) {
            this.width = w;
            this.height = h;
            return this;
        }

        public ContainerStyle withBorder() {
            this.border = true;
            return this;
        }

        public ContainerStyle bg(ImVec4 color) {
            this.bgColor = color;
            return this;
        }
    }

    // Grid system
    public static void grid(int columns, Runnable content) {
        ImGui.beginTable(OverseerUtility.hiddenIndexString("grid", OverseerUtility.getUniqueId()), columns);
        content.run();
        ImGui.endTable();
    }

    public static void gridCell(Runnable content) {
        ImGui.tableNextColumn();
        content.run();
    }

    // Conditional rendering
    public static void when(boolean condition, Runnable content) {
        if (condition) content.run();
    }

    // Simple state management
    private static Map<String, Object> reactive = new ConcurrentHashMap<>();

    public static void setState(String key, Object value) {
        reactive.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getState(String key, T defaultValue) {
        if(!reactive.containsKey(key)) return defaultValue;
        return (T) reactive.get(key);
    }

    public static <T> T getState(String key) {
        return getState(key, null);
    }
}
