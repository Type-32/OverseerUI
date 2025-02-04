package cn.crtlprototypestudios.ovsr.client.api.components;

import imgui.ImGui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Component {
    protected final Props props;
    protected final List<Component> children;
    protected final Map<String, Slot> slots;
    protected Component parent;
    protected boolean mounted = false;
    protected String id;
    protected float lastX = 0;
    protected float lastY = 0;

    public Component() {
        this.props = new Props();
        this.children = new ArrayList<>();
        this.slots = new HashMap<>();
        this.id = generateId();
    }

    protected abstract void render();

    public final void internalRender() {
        if (!mounted) {
            onBeforeMount();
            mounted = true;
            onMounted();
        }

        // Save current cursor position
        float currentX = ImGui.getCursorPosX();
        float currentY = ImGui.getCursorPosY();

        // Set position if it has changed significantly
        if (Math.abs(currentX - lastX) > 1 || Math.abs(currentY - lastY) > 1) {
            lastX = currentX;
            lastY = currentY;
        }

        ImGui.setCursorPos(lastX, lastY);
        ImGui.pushID(id);
        render();
        ImGui.popID();
    }

    // Lifecycle methods
    protected void onBeforeMount() {}
    protected void onMounted() {}
    protected void onBeforeUnmount() {}
    protected void onUnmounted() {}

    // Component tree methods
    public Component withChild(Component child) {
        children.add(child);
        child.parent = this;
        return this;
    }

    public Component withChildren(Component... components) {
        for (Component component : components) {
            withChild(component);
        }
        return this;
    }

    public Component withSlot(String name, Slot slot) {
        slots.put(name, slot);
        return this;
    }

    public Component withProps(Consumer<Props> propsConfig) {
        propsConfig.accept(props);
        return this;
    }

    protected void unmount() {
        if (mounted) {
            onBeforeUnmount();
            children.forEach(Component::unmount);
            mounted = false;
            onUnmounted();
        }
    }

    private static long nextId = 0;

    protected String generateId() {
        return getClass().getSimpleName() + "##" + (nextId++);
    }
}
