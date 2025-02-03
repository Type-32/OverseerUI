package cn.crtlprototypestudios.ovsr.api.layout;

import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;
import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends BaseComponent {
    private int gap = 5; // Space between components
    private Orientation orientation = Orientation.HORIZONTAL;
    private WrapMode wrapMode = WrapMode.WRAP;
    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
    private VerticalAlignment verticalAlignment = VerticalAlignment.TOP;
    private Distribution distribution = Distribution.START;

    public FlowLayout(ComponentData data) {
        super(
                data.getIntAttribute("x", 0),
                data.getIntAttribute("y", 0),
                data.getIntAttribute("width", 200),
                data.getIntAttribute("height", 200)
        );

        // Parse layout properties
        this.gap = data.getIntAttribute("gap", 5);
        this.orientation = Orientation.valueOf(data.getAttribute("orientation", "HORIZONTAL").toUpperCase());
        this.wrapMode = WrapMode.valueOf(data.getAttribute("wrap-mode", "WRAP").toUpperCase());

        // Parse alignment properties
        this.horizontalAlignment = HorizontalAlignment.valueOf(
                data.getAttribute("horizontal-align", "LEFT").toUpperCase()
        );
        this.verticalAlignment = VerticalAlignment.valueOf(
                data.getAttribute("vertical-align", "TOP").toUpperCase()
        );
        this.distribution = Distribution.valueOf(
                data.getAttribute("distribution", "START").toUpperCase()
        );
    }

    @Override
    protected void onChildAdded(BaseComponent child) {
        recalculateLayout();
    }

    @Override
    protected void onChildRemoved(BaseComponent child) {
        recalculateLayout();
    }

    private void recalculateLayout() {
        if (children.isEmpty()) return;

        if (orientation == Orientation.HORIZONTAL) {
            calculateHorizontalLayout();
        } else {
            calculateVerticalLayout();
        }
    }

    private void calculateHorizontalLayout() {
        if (getChildren().isEmpty()) return;

        List<RowData> rows = new ArrayList<>();
        RowData currentRow = new RowData();
        rows.add(currentRow);

        int currentX = x;
        int currentY = y;

        // First pass: Group components into rows
        for (BaseComponent component : getChildren()) {
            if (!component.isVisible()) continue;

            if (wrapMode == WrapMode.WRAP &&
                    currentX + component.getWidth() > x + width &&
                    !currentRow.components.isEmpty()) {
                // Start new row
                currentRow = new RowData();
                rows.add(currentRow);
                currentX = x;
            }

            currentRow.components.add(component);
            currentRow.width += component.getWidth();
            if (!currentRow.components.isEmpty()) {
                currentRow.width += gap;
            }
            currentRow.height = Math.max(currentRow.height, component.getHeight());
            currentX += component.getWidth() + gap;
        }

        // Remove trailing gap from row widths
        for (RowData row : rows) {
            if (!row.components.isEmpty()) {
                row.width -= gap;
            }
        }

        // Second pass: Position components according to alignment
        currentY = y;
        int totalHeight = rows.stream().mapToInt(r -> r.height).sum() +
                (rows.size() - 1) * gap;

        // Calculate starting Y based on vertical alignment
        switch (verticalAlignment) {
            case CENTER:
                currentY += (height - totalHeight) / 2;
                break;
            case BOTTOM:
                currentY += height - totalHeight;
                break;
            case TOP:
            default:
                break;
        }

        for (RowData row : rows) {
            int rowX = x;
            int extraSpace = width - row.width;

            // Calculate starting X based on horizontal alignment
            switch (horizontalAlignment) {
                case CENTER:
                    rowX += extraSpace / 2;
                    break;
                case RIGHT:
                    rowX += extraSpace;
                    break;
                case LEFT:
                default:
                    break;
            }

            // Calculate gaps for distribution
            float distributionGap = 0;
            switch (distribution) {
                case SPACE_BETWEEN:
                    if (row.components.size() > 1) {
                        distributionGap = extraSpace / (float)(row.components.size() - 1);
                    }
                    break;
                case SPACE_AROUND:
                    if (!row.components.isEmpty()) {
                        distributionGap = extraSpace / (float)(row.components.size() + 1);
                        rowX += distributionGap;
                    }
                    break;
                case SPACE_EVENLY:
                    if (!row.components.isEmpty()) {
                        distributionGap = extraSpace / (float)(row.components.size() + 2);
                        rowX += distributionGap;
                    }
                    break;
                case START:
                default:
                    break;
            }

            // Position components in row
            for (BaseComponent component : row.components) {
                // Vertical alignment within row
                int componentY = currentY;
                switch (verticalAlignment) {
                    case CENTER:
                        componentY += (row.height - component.getHeight()) / 2;
                        break;
                    case BOTTOM:
                        componentY += row.height - component.getHeight();
                        break;
                    case TOP:
                    default:
                        break;
                }

                component.setPosition(rowX, componentY);
                rowX += (int) (component.getWidth() + gap + distributionGap);
            }

            currentY += row.height + gap;
        }
    }

    public void addComponent(BaseComponent component) {
        addChild(component);
        recalculateLayout();
    }

    public void removeComponent(BaseComponent component) {
        removeChild(component);
        recalculateLayout();
    }

    public void clear() {
        getChildren().clear();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;

        for (BaseComponent component : getChildren()) {
            if (component.isVisible()) {
                component.render(graphics, mouseX, mouseY, partialTicks);
            }
        }
    }

    private void calculateVerticalLayout() {
        int currentX = x;
        int currentY = y;
        int columnWidth = 0;
        int columnStartIndex = 0;

        // First pass: Calculate positions and find column breaks
        for (int i = 0; i < getChildren().size(); i++) {
            BaseComponent component = getChildren().get(i);
            if (!component.isVisible()) continue;

            // Check if we need to wrap
            if (wrapMode == WrapMode.WRAP &&
                    currentY + component.getHeight() > y + height &&
                    i > columnStartIndex) {
                // Align the completed column
                alignColumn(columnStartIndex, i - 1, currentX, columnWidth);
                // Move to next column
                currentY = y;
                currentX += columnWidth + gap;
                columnWidth = 0;
                columnStartIndex = i;
            }

            // Position component
            component.setPosition(currentX, currentY);

            // Update tracking variables
            currentY += component.getHeight() + gap;
            columnWidth = Math.max(columnWidth, component.getWidth());
        }

        // Align the last column
        alignColumn(columnStartIndex, getChildren().size() - 1, currentX, columnWidth);
    }

    private void alignRow(int startIndex, int endIndex, int rowY, int rowHeight) {
        if (horizontalAlignment == HorizontalAlignment.CENTER || horizontalAlignment == HorizontalAlignment.RIGHT) {
            // Calculate total width of components in row
            int totalWidth = 0;
            int componentCount = 0;

            for (int i = startIndex; i <= endIndex; i++) {
                BaseComponent component = getChildren().get(i);
                if (!component.isVisible()) continue;
                totalWidth += component.getWidth();
                if (i < endIndex) totalWidth += gap;
                componentCount++;
            }

            // Calculate offset
            int offset = horizontalAlignment == HorizontalAlignment.CENTER
                    ? (width - totalWidth) / 2
                    : (width - totalWidth);

            // Adjust component positions
            if (offset > 0) {
                for (int i = startIndex; i <= endIndex; i++) {
                    BaseComponent component = getChildren().get(i);
                    if (!component.isVisible()) continue;
                    component.setPosition(
                            component.getX() + offset,
                            component.getY()
                    );
                }
            }
        }
    }

    private void alignColumn(int startIndex, int endIndex, int columnX, int columnWidth) {
        if (horizontalAlignment == HorizontalAlignment.CENTER || horizontalAlignment == HorizontalAlignment.RIGHT) {
            // Calculate total height of components in column
            int totalHeight = 0;
            int componentCount = 0;

            for (int i = startIndex; i <= endIndex; i++) {
                BaseComponent component = getChildren().get(i);
                if (!component.isVisible()) continue;
                totalHeight += component.getHeight();
                if (i < endIndex) totalHeight += gap;
                componentCount++;
            }

            // Calculate offset
            int offset = horizontalAlignment == HorizontalAlignment.CENTER
                    ? (height - totalHeight) / 2
                    : (height - totalHeight);

            // Adjust component positions
            if (offset > 0) {
                for (int i = startIndex; i <= endIndex; i++) {
                    BaseComponent component = getChildren().get(i);
                    if (!component.isVisible()) continue;
                    component.setPosition(
                            component.getX(),
                            component.getY() + offset
                    );
                }
            }
        }
    }

    private static class RowData {
        List<BaseComponent> components = new ArrayList<>();
        int width = 0;
        int height = 0;
    }

    public enum HorizontalAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum VerticalAlignment {
        TOP,
        CENTER,
        BOTTOM
    }

    public enum Distribution {
        START,          // Components packed at start
        SPACE_BETWEEN,  // Equal space between components
        SPACE_AROUND,   // Equal space around components
        SPACE_EVENLY    // Equal space between and around components
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    public enum WrapMode {
        WRAP,
        NO_WRAP
    }

    // Getters and setters
    public void setHorizontalAlignment(HorizontalAlignment alignment) {
        this.horizontalAlignment = alignment;
        recalculateLayout();
    }

    public void setVerticalAlignment(VerticalAlignment alignment) {
        this.verticalAlignment = alignment;
        recalculateLayout();
    }

    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
        recalculateLayout();
    }

    public void setWrapMode(WrapMode wrapMode) {
        this.wrapMode = wrapMode;
        recalculateLayout();
    }

    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        recalculateLayout();
    }
}
