package cn.crtlprototypestudios.ovsr.api.layout;

import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;
import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridLayout extends BaseComponent {
    private final Map<BaseComponent, GridConstraints> constraints = new HashMap<>();

    private int rows;
    private int columns;
    private int gap = 5;
    private int[] rowHeights;    // -1 for auto
    private int[] columnWidths;  // -1 for auto
    private List<RowDefinition> rowDefs;
    private List<ColumnDefinition> columnDefs;

    public GridLayout(ComponentData data) {
        super(
                data.getIntAttribute("x", 0),
                data.getIntAttribute("y", 0),
                data.getIntAttribute("width", 300),
                data.getIntAttribute("height", 300)
        );

        this.rows = data.getIntAttribute("rows", 3);
        this.columns = data.getIntAttribute("columns", 3);
        this.gap = data.getIntAttribute("gap", 5);

        initializeGrid();
        parseGridDefinitions(data);
    }

    private void initializeGrid() {
        rowHeights = new int[rows];
        columnWidths = new int[columns];
        rowDefs = new ArrayList<>(rows);
        columnDefs = new ArrayList<>(columns);

        // Initialize with default values
        for (int i = 0; i < rows; i++) {
            rowDefs.add(new RowDefinition(SizeType.AUTO));
        }
        for (int i = 0; i < columns; i++) {
            columnDefs.add(new ColumnDefinition(SizeType.AUTO));
        }
    }

    @Override
    protected void onChildAdded(BaseComponent child) {
        // Add default constraints if none specified
        if (!constraints.containsKey(child)) {
            constraints.put(child, new GridConstraints(0, 0));
        }
        recalculateLayout();
    }

    @Override
    protected void onChildRemoved(BaseComponent child) {
        constraints.remove(child);
        recalculateLayout();
    }

    public void setConstraints(BaseComponent component, GridConstraints constraint) {
        if (children.contains(component)) {
            constraints.put(component, constraint);
            recalculateLayout();
        }
    }

    private void parseGridDefinitions(ComponentData data) {
        // Parse row definitions
        String[] rowData = data.getAttribute("row-definitions", "").split(",");
        for (int i = 0; i < Math.min(rowData.length, rows); i++) {
            rowDefs.set(i, parseDefinition(rowData[i].trim()));
        }

        // Parse column definitions
        String[] colData = data.getAttribute("column-definitions", "").split(",");
        for (int i = 0; i < Math.min(colData.length, columns); i++) {
            columnDefs.set(i, new ColumnDefinition(parseDefinition(colData[i].trim()).sizeType));
        }
    }

    private RowDefinition parseDefinition(String def) {
        if (def.endsWith("px")) {
            return new RowDefinition(SizeType.FIXED,
                    Integer.parseInt(def.substring(0, def.length() - 2)));
        } else if (def.endsWith("*")) {
            float weight = def.length() > 1 ?
                    Float.parseFloat(def.substring(0, def.length() - 1)) : 1f;
            return new RowDefinition(SizeType.STAR, weight);
        } else {
            return new RowDefinition(SizeType.AUTO);
        }
    }

    public void addComponent(BaseComponent component, GridConstraints constraint) {
        addChild(component);
        constraints.put(component, constraint);
        recalculateLayout();
    }

    public void removeComponent(BaseComponent component) {
        removeChild(component);
        constraints.remove(component);
        recalculateLayout();
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

    private void recalculateLayout() {
        if (children.isEmpty()) return;

        // Calculate auto sizes first
        calculateAutoSizes();

        // Calculate star (proportional) sizes
        distributeRemainingSpace();

        // Position components
        positionComponents();
    }

    private void calculateAutoSizes() {
        // Reset sizes
        for (int i = 0; i < rows; i++) {
            rowHeights[i] = rowDefs.get(i).sizeType == SizeType.FIXED ?
                    rowDefs.get(i).size : 0;
        }
        for (int i = 0; i < columns; i++) {
            columnWidths[i] = columnDefs.get(i).sizeType == SizeType.FIXED ?
                    columnDefs.get(i).size : 0;
        }

        // Calculate sizes based on components
        for (BaseComponent component : getChildren()) {
            GridConstraints gc = constraints.get(component);
            if (gc == null) continue;

            // Update row heights
            if (rowDefs.get(gc.row).sizeType == SizeType.AUTO) {
                rowHeights[gc.row] = Math.max(rowHeights[gc.row],
                        component.getHeight() / gc.rowSpan);
            }

            // Update column widths
            if (columnDefs.get(gc.column).sizeType == SizeType.AUTO) {
                columnWidths[gc.column] = Math.max(columnWidths[gc.column],
                        component.getWidth() / gc.columnSpan);
            }
        }
    }

    private void distributeRemainingSpace() {
        // Calculate total available space
        int usedWidth = 0;
        int usedHeight = 0;
        float totalStarColumnsWeight = 0;
        float totalStarRowsWeight = 0;

        for (int i = 0; i < columns; i++) {
            if (columnDefs.get(i).sizeType != SizeType.STAR) {
                usedWidth += columnWidths[i];
            } else {
                totalStarColumnsWeight += columnDefs.get(i).weight;
            }
        }

        for (int i = 0; i < rows; i++) {
            if (rowDefs.get(i).sizeType != SizeType.STAR) {
                usedHeight += rowHeights[i];
            } else {
                totalStarRowsWeight += rowDefs.get(i).weight;
            }
        }

        // Account for gaps
        usedWidth += gap * (columns - 1);
        usedHeight += gap * (rows - 1);

        // Distribute remaining space
        int remainingWidth = width - usedWidth;
        int remainingHeight = height - usedHeight;

        if (totalStarColumnsWeight > 0) {
            float pixelsPerStarUnit = remainingWidth / totalStarColumnsWeight;
            for (int i = 0; i < columns; i++) {
                if (columnDefs.get(i).sizeType == SizeType.STAR) {
                    columnWidths[i] = (int)(pixelsPerStarUnit * columnDefs.get(i).weight);
                }
            }
        }

        if (totalStarRowsWeight > 0) {
            float pixelsPerStarUnit = remainingHeight / totalStarRowsWeight;
            for (int i = 0; i < rows; i++) {
                if (rowDefs.get(i).sizeType == SizeType.STAR) {
                    rowHeights[i] = (int)(pixelsPerStarUnit * rowDefs.get(i).weight);
                }
            }
        }
    }

    private void positionComponents() {
        // Calculate cell positions
        int[] xPositions = new int[columns];
        int[] yPositions = new int[rows];

        xPositions[0] = x;
        for (int i = 1; i < columns; i++) {
            xPositions[i] = xPositions[i-1] + columnWidths[i-1] + gap;
        }

        yPositions[0] = y;
        for (int i = 1; i < rows; i++) {
            yPositions[i] = yPositions[i-1] + rowHeights[i-1] + gap;
        }

        // Position components
        for (BaseComponent component : getChildren()) {
            GridConstraints gc = constraints.get(component);
            if (gc == null) continue;

            // Calculate spanned size
            int cellWidth = -gap;
            int cellHeight = -gap;

            for (int col = gc.column; col < gc.column + gc.columnSpan; col++) {
                cellWidth += columnWidths[col] + gap;
            }

            for (int row = gc.row; row < gc.row + gc.rowSpan; row++) {
                cellHeight += rowHeights[row] + gap;
            }

            // Position and resize component
            int componentX = xPositions[gc.column];
            int componentY = yPositions[gc.row];

            // Apply alignment
            switch (gc.horizontalAlignment) {
                case CENTER:
                    componentX += (cellWidth - component.getWidth()) / 2;
                    break;
                case RIGHT:
                    componentX += cellWidth - component.getWidth();
                    break;
                case STRETCH:
                    component.setWidth(cellWidth);
                    break;
            }

            switch (gc.verticalAlignment) {
                case CENTER:
                    componentY += (cellHeight - component.getHeight()) / 2;
                    break;
                case BOTTOM:
                    componentY += cellHeight - component.getHeight();
                    break;
                case STRETCH:
                    component.setHeight(cellHeight);
                    break;
            }

            component.setPosition(componentX, componentY);
        }
    }

    public static class GridConstraints {
        public int row;
        public int column;
        public int rowSpan;
        public int columnSpan;
        public HorizontalAlignment horizontalAlignment;
        public VerticalAlignment verticalAlignment;

        public GridConstraints(int row, int column) {
            this(row, column, 1, 1,
                    HorizontalAlignment.STRETCH,
                    VerticalAlignment.STRETCH);
        }

        public GridConstraints(int row, int column,
                               int rowSpan, int columnSpan,
                               HorizontalAlignment hAlign,
                               VerticalAlignment vAlign) {
            this.row = row;
            this.column = column;
            this.rowSpan = rowSpan;
            this.columnSpan = columnSpan;
            this.horizontalAlignment = hAlign;
            this.verticalAlignment = vAlign;
        }
    }

    private static class RowDefinition {
        SizeType sizeType;
        int size;
        float weight;

        RowDefinition(SizeType sizeType) {
            this(sizeType, 0, 1f);
        }

        RowDefinition(SizeType sizeType, float weight) {
            this(sizeType, 0, weight);
        }

        RowDefinition(SizeType sizeType, int size) {
            this(sizeType, size, 1f);
        }

        RowDefinition(SizeType sizeType, int size, float weight) {
            this.sizeType = sizeType;
            this.size = size;
            this.weight = weight;
        }
    }

    private static class ColumnDefinition {
        public int size;
        SizeType sizeType;
        float weight;

        ColumnDefinition(SizeType sizeType) {
            this(sizeType, 1f);
        }

        ColumnDefinition(SizeType sizeType, float weight) {
            this.sizeType = sizeType;
            this.weight = weight;
        }
    }

    public enum SizeType {
        AUTO,    // Size based on content
        FIXED,   // Fixed pixel size
        STAR     // Proportional size
    }

    public enum HorizontalAlignment {
        LEFT,
        CENTER,
        RIGHT,
        STRETCH
    }

    public enum VerticalAlignment {
        TOP,
        CENTER,
        BOTTOM,
        STRETCH
    }
}
