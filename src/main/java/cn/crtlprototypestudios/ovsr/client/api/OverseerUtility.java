package cn.crtlprototypestudios.ovsr.client.api;

import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTableFlags;

public class OverseerUtility {

    private static int uniqueId = 0;

    public class UI {
        // Styling constants
        public static final int CORNER_NONE = 0;
        public static final int CORNER_TOP_LEFT = 1;
        public static final int CORNER_TOP_RIGHT = 2;
        public static final int CORNER_BOTTOM_LEFT = 4;
        public static final int CORNER_BOTTOM_RIGHT = 8;
        public static final int CORNER_ALL = 15;

        // Spacing and padding
        public static void spacing(int count) {
            for (int i = 0; i < count; i++) {
                ImGui.spacing();
            }
        }

        public static void padding(float padding) {
            ImGui.dummy(padding, padding);
        }

        public static void horizontalPadding(float padding) {
            ImGui.dummy(padding, 0);
        }

        public static void verticalPadding(float padding) {
            ImGui.dummy(0, padding);
        }

        // Container styles
        public static void beginRoundedContainer(String id, float rounding, int flags) {
            ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, rounding);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, rounding);
            ImGui.beginChild(id, 0, 0, true, flags);
        }

        public static void endRoundedContainer() {
            ImGui.endChild();
            ImGui.popStyleVar(2);
        }

        // Flex layout helpers
        public static void beginHorizontal(String id) {
            ImGui.beginGroup();
            float startX = ImGui.getCursorPosX();
            float startY = ImGui.getCursorPosY();
        }

        public static void endHorizontal() {
            ImGui.endGroup();
        }

        public static void beginVertical(String id) {
            ImGui.beginGroup();
        }

        public static void endVertical() {
            ImGui.endGroup();
        }

        // Grid layout
        public static void beginGrid(String id, int columns) {
            ImGui.beginTable(id, columns, ImGuiTableFlags.None);
        }

        public static void nextGridColumn() {
            ImGui.tableNextColumn();
        }

        public static void endGrid() {
            ImGui.endTable();
        }

        // Container with background
        public static void beginContainerWithBackground(String id, ImVec4 backgroundColor) {
            ImGui.pushStyleColor(ImGuiCol.ChildBg, backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w);
            ImGui.beginChild(id);
        }

        public static void endContainerWithBackground() {
            ImGui.endChild();
            ImGui.popStyleColor();
        }

        // Container with outline
        public static void beginContainerWithOutline(String id, ImVec4 outlineColor, float thickness) {
            ImGui.pushStyleColor(ImGuiCol.Border, outlineColor.x, outlineColor.y, outlineColor.z, outlineColor.w);
            ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, thickness);
            ImGui.beginChild(id, 0, 0, true);
        }

        public static void endContainerWithOutline() {
            ImGui.endChild();
            ImGui.popStyleVar();
            ImGui.popStyleColor();
        }

        // Auto-width container
        public static float calculateContentWidth() {
            return ImGui.getContentRegionAvail().x;
        }

        public static void beginAutoWidthContainer(String id) {
            float contentWidth = calculateContentWidth();
            ImGui.beginChild(id, contentWidth, 0);
        }

        public static void endAutoWidthContainer() {
            ImGui.endChild();
        }

        // Styled button variations
        public static boolean roundedButton(String label, float rounding) {
            ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, rounding);
            boolean clicked = ImGui.button(label);
            ImGui.popStyleVar();
            return clicked;
        }

        public static boolean outlinedButton(String label, ImVec4 outlineColor) {
            ImGui.pushStyleColor(ImGuiCol.Border, outlineColor.x, outlineColor.y, outlineColor.z, outlineColor.w);
            ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 1.0f);
            boolean clicked = ImGui.button(label);
            ImGui.popStyleVar();
            ImGui.popStyleColor();
            return clicked;
        }

        // Layout helpers
        public static void sameLine(float spacing) {
            ImGui.sameLine(0, spacing);
        }

        public static void indent(float indent) {
            ImGui.indent(indent);
        }

        public static void unindent(float indent) {
            ImGui.unindent(indent);
        }

        // Container builder pattern
        public static class ContainerBuilder {
            private String id;
            private float rounding = 0;
            private ImVec4 backgroundColor = null;
            private ImVec4 outlineColor = null;
            private float outlineThickness = 1;
            private boolean autoWidth = false;
            private int flags = 0;

            public ContainerBuilder(String id) {
                this.id = id;
            }

            public ContainerBuilder withRounding(float rounding) {
                this.rounding = rounding;
                return this;
            }

            public ContainerBuilder withBackground(ImVec4 color) {
                this.backgroundColor = color;
                return this;
            }

            public ContainerBuilder withOutline(ImVec4 color, float thickness) {
                this.outlineColor = color;
                this.outlineThickness = thickness;
                return this;
            }

            public ContainerBuilder withAutoWidth() {
                this.autoWidth = true;
                return this;
            }

            public ContainerBuilder withFlags(int flags) {
                this.flags = flags;
                return this;
            }

            public void begin() {
                if (rounding > 0) {
                    ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, rounding);
                    ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, rounding);
                }

                if (backgroundColor != null) {
                    ImGui.pushStyleColor(ImGuiCol.ChildBg, backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w);
                }

                if (outlineColor != null) {
                    ImGui.pushStyleColor(ImGuiCol.Border, outlineColor.x, outlineColor.y, outlineColor.z, outlineColor.w);
                    ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, outlineThickness);
                }

                float width = autoWidth ? calculateContentWidth() : 0;
                ImGui.beginChild(id, width, 0, outlineColor != null, flags);
            }

            public void end() {
                ImGui.endChild();

                if (outlineColor != null) {
                    ImGui.popStyleVar();
                    ImGui.popStyleColor();
                }

                if (backgroundColor != null) {
                    ImGui.popStyleColor();
                }

                if (rounding > 0) {
                    ImGui.popStyleVar(2);
                }
            }
        }
    }

    public class Colors {
        public static final ImVec4 WHITE = new ImVec4(1.0f, 1.0f, 1.0f, 1.0f);
        public static final ImVec4 BLACK = new ImVec4(0.0f, 0.0f, 0.0f, 1.0f);
        public static final ImVec4 TRANSPARENT = new ImVec4(0.0f, 0.0f, 0.0f, 0.0f);

        public static ImVec4 rgba(float r, float g, float b, float a) {
            return new ImVec4(r/255f, g/255f, b/255f, a);
        }

        public static ImVec4 rgb(float r, float g, float b) {
            return rgba(r, g, b, 1.0f);
        }

        public static ImVec4 withAlpha(ImVec4 color, float alpha) {
            return new ImVec4(color.x, color.y, color.z, alpha);
        }
    }

    public static String hiddenIndexString(String content, Object uniqueIndex){
        return String.format("%s##%s", content, String.valueOf(uniqueIndex));
    }

    public static String uniqueString(String content) {
        return hiddenIndexString(content, getUniqueId());
    }

    public static int getUniqueId() {
        return uniqueId;
    }
}
