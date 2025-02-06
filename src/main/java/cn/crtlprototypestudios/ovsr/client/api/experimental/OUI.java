package cn.crtlprototypestudios.ovsr.client.api.experimental;

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
    private static int uniqueId = 0;

    // Simplified flex container
    public static void flex(String direction, Runnable content) {
        ImGui.beginGroup();
        if ("row".equals(direction)) {
            content.run();
        } else { // column
            float startY = ImGui.getCursorPosY();
            content.run();
            ImGui.setCursorPosY(startY);
        }
        ImGui.endGroup();
    }

    // For-each with auto-indexing
    public static <T> void each(List<T> items, BiConsumer<T, Integer> render) {
        for (int i = 0; i < items.size(); i++) {
            ImGui.pushID(uniqueId++);
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

        ImGui.beginChild(label, style.width, style.height, style.border);
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
        ImGui.beginTable("grid" + uniqueId++, columns);
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
    public static <T> T getState(String key) {
        return (T) reactive.get(key);
    }
}
