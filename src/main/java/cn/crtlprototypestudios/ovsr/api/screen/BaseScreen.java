package cn.crtlprototypestudios.ovsr.api.screen;

import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;
import cn.crtlprototypestudios.ovsr.api.factory.ComponentFactory;
import cn.crtlprototypestudios.ovsr.api.reload.UIDefinition;
import cn.crtlprototypestudios.ovsr.api.reload.UIResourceManager;
import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseScreen extends Screen {
    protected final List<BaseComponent> rootComponents = new ArrayList<>();
    protected BaseComponent focusedComponent;
    protected BaseComponent hoveredComponent;

    protected int lastMouseX;
    protected int lastMouseY;

    private String definitionId;

    protected BaseScreen(Component title, String definitionId) {
        super(title);
        this.definitionId = definitionId;
        UIResourceManager.getInstance().registerScreen(definitionId, this);
    }

    @Override
    public void removed() {
        super.removed();
        UIResourceManager.getInstance().unregisterScreen(definitionId);
//        rootComponents.clear();
//        focusedComponent = null;
//        hoveredComponent = null;
    }

    public void reloadLayout() {
        // Save current state if needed
        Map<String, Object> state = captureState();

        // Clear existing components
        rootComponents.clear();

        // Reload components from definition
        UIDefinition definition = UIResourceManager.getInstance().getDefinition(definitionId);
        if (definition != null) {
            loadComponentsFromDefinition(definition);
        }

        // Restore state
        restoreState(state);
    }

    protected Map<String, Object> captureState() {
        Map<String, Object> state = new HashMap<>();
        // Capture state of components that need to persist across reloads
        for (BaseComponent component : rootComponents) {
            if (component.getId() != null) {
                state.put(component.getId(), component.captureState());
            }
        }
        return state;
    }

    protected void restoreState(Map<String, Object> state) {
        // Restore state to components
        for (BaseComponent component : rootComponents) {
            if (component.getId() != null && state.containsKey(component.getId())) {
                component.restoreState(state.get(component.getId()));
            }
        }
    }

    protected BaseComponent createComponent(ComponentData data) {
        return ComponentFactory.getInstance().createComponent(data);
    }

    protected void loadComponentsFromDefinition(UIDefinition definition) {
        for (ComponentData componentData : definition.getRootComponent().getChildren()) {
            BaseComponent component = createComponent(componentData);
            if (component != null) {
                addComponent(component);
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        rootComponents.clear();
        initComponents();
    }

    /**
     * Override this method to initialize your screen components
     */
    protected abstract void initComponents();

    public void addComponent(BaseComponent component) {
        rootComponents.add(component);
    }

    public void removeComponent(BaseComponent component) {
        rootComponents.remove(component);
        if (focusedComponent == component) {
            focusedComponent = null;
        }
        if (hoveredComponent == component) {
            hoveredComponent = null;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;

        // Update hovered component
        BaseComponent newHovered = findComponentAt(mouseX, mouseY);
        if (newHovered != hoveredComponent) {
            if (hoveredComponent != null) {
                hoveredComponent.onMouseLeave(mouseX, mouseY);
            }
            hoveredComponent = newHovered;
            if (hoveredComponent != null) {
                hoveredComponent.onMouseEnter(mouseX, mouseY);
            }
        }

        // Render components
        for (BaseComponent component : rootComponents) {
            if (component.isVisible()) {
                component.render(graphics, mouseX, mouseY, partialTicks);
            }
        }

        // Render tooltips last
        if (hoveredComponent != null) {
            hoveredComponent.renderTooltip(graphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        BaseComponent clicked = findComponentAt((int)mouseX, (int)mouseY);

        // Handle focus
        if (clicked != focusedComponent) {
            if (focusedComponent != null) {
                focusedComponent.onFocusLost();
            }
            focusedComponent = clicked;
            if (focusedComponent != null) {
                focusedComponent.onFocused();
            }
        }

        // Handle click
        if (clicked != null) {
            return clicked.onMouseClick((int)mouseX, (int)mouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (focusedComponent != null) {
            return focusedComponent.onMouseRelease((int)mouseX, (int)mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (focusedComponent != null) {
            return focusedComponent.onMouseDrag((int)mouseX, (int)mouseY, button, dragX, dragY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (focusedComponent != null) {
            return focusedComponent.onKeyPress(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (focusedComponent != null) {
            return focusedComponent.onKeyRelease(keyCode, scanCode, modifiers);
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (focusedComponent != null) {
            return focusedComponent.onCharTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
    }

    protected BaseComponent findComponentAt(int x, int y) {
        // Search in reverse order (top-most first)
        for (int i = rootComponents.size() - 1; i >= 0; i--) {
            BaseComponent component = rootComponents.get(i);
            BaseComponent found = findComponentAtRecursive(component, x, y);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    protected BaseComponent findComponentAtRecursive(BaseComponent component, int x, int y) {
        if (!component.isVisible() || !component.contains(x, y)) {
            return null;
        }

        // Check children first (reverse order)
        for (int i = component.getChildren().size() - 1; i >= 0; i--) {
            BaseComponent child = component.getChildren().get(i);
            BaseComponent found = findComponentAtRecursive(child, x, y);
            if (found != null) {
                return found;
            }
        }

        // If no child was hit, return this component if it's interactive
        return component.isInteractive() ? component : null;
    }
}
