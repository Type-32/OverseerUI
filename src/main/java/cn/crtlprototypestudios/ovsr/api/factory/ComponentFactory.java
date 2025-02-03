package cn.crtlprototypestudios.ovsr.api.factory;

import cn.crtlprototypestudios.ovsr.Ovsr;
import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;
import cn.crtlprototypestudios.ovsr.api.components.ContainerComponent;
import cn.crtlprototypestudios.ovsr.api.components.primitives.*;
import cn.crtlprototypestudios.ovsr.api.event.EventHandlerRegistry;
import cn.crtlprototypestudios.ovsr.api.template.TemplateManager;
import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

public class ComponentFactory {
    private static ComponentFactory INSTANCE;

    private ComponentFactory() {}

    public static ComponentFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComponentFactory();
        }
        return INSTANCE;
    }

    public BaseComponent createComponent(ComponentData data) {
        if (data == null) return null;

        return switch (data.getType().toLowerCase()) {
            case "button" -> createButton(data);
            case "label" -> createLabel(data);
            case "inputbox" -> createInputBox(data);
            case "checkbox" -> createCheckbox(data);
            case "progressbar" -> createProgressBar(data);
            case "container" -> createContainer(data);
            case "scrollcontainer" -> createScrollContainer(data);
            case "slider" -> createSlider(data);
            case "template" -> createFromTemplate(data);
            default -> {
                Ovsr.LOGGER.warn("Unknown component type: {}", data.getType());
                yield null;
            }
        };
    }

    private void bindEvents(BaseComponent component, ComponentData data) {
        // Common events
        bindEventIfExists(component, data, "onClick", "click");
        bindEventIfExists(component, data, "onChange", "change");
        bindEventIfExists(component, data, "onFocus", "focus");
        bindEventIfExists(component, data, "onBlur", "blur");
        bindEventIfExists(component, data, "onKeyPress", "keypress");
    }

    private void bindEventIfExists(BaseComponent component, ComponentData data, String attributeName, String eventType) {
        String handlerId = data.getAttribute(attributeName);
        if (handlerId != null) {
            EventHandlerRegistry.getInstance().bindNamedHandler(component, eventType, handlerId);
        }
    }

    private BaseComponent createButton(ComponentData data) {
        ButtonComponent button = new ButtonComponent(data);

        if (data.hasAttribute("text")) {
            button.setText(Component.literal(data.getAttribute("text")));
        }

        bindEvents(button, data);
        applyCommonAttributes(button, data);
        return button;
    }

    private BaseComponent createLabel(ComponentData data) {
        LabelComponent label = new LabelComponent(data);

        if (data.hasAttribute("text")) {
            label.setText(Component.literal(data.getAttribute("text")));
        }

        applyCommonAttributes(label, data);
        return label;
    }

    private BaseComponent createInputBox(ComponentData data) {
        InputBoxComponent inputBox = new InputBoxComponent(data);

        // Set specific InputBox properties
        if (data.hasAttribute("placeholder")) {
            inputBox.setPlaceholder(data.getAttribute("placeholder"));
        }

        if (data.hasAttribute("max-length")) {
            inputBox.setMaxLength(data.getIntAttribute("max-length", 32));
        }

        if (data.hasAttribute("value")) {
            inputBox.setValue(data.getAttribute("value"));
        }

        inputBox.setPassword(data.getBoolAttribute("password", false));
        inputBox.setEditable(data.getBoolAttribute("editable", true));

        // Set text filter if specified
        if (data.hasAttribute("filter")) {
            String filter = data.getAttribute("filter");
            inputBox.setTextFilter(s -> s.matches(filter));
        }

        applyCommonAttributes(inputBox, data);
        return inputBox;
    }

    private BaseComponent createCheckbox(ComponentData data) {
        CheckboxComponent checkbox = new CheckboxComponent(data);

        if (data.hasAttribute("text")) {
            checkbox.setText(Component.literal(data.getAttribute("text")));
        }

        checkbox.setChecked(data.getBoolAttribute("checked", false));

        if (data.hasAttribute("style")) {
            checkbox.setStyle(data.getAttribute("style"));
        }

        applyCommonAttributes(checkbox, data);
        return checkbox;
    }

    private BaseComponent createProgressBar(ComponentData data) {
        ProgressBarComponent progressBar = new ProgressBarComponent(data);

        progressBar.setProgress(data.getFloatAttribute("progress", 0f));
        progressBar.setShowPercentage(data.getBoolAttribute("show-percentage", false));

        if (data.hasAttribute("style")) {
            progressBar.setStyle(data.getAttribute("style"));
        }

        if (data.hasAttribute("fill-color")) {
            progressBar.setFillColor(Integer.parseInt(data.getAttribute("fill-color"), 16));
        }

        if (data.hasAttribute("background-color")) {
            progressBar.setBackgroundColor(Integer.parseInt(data.getAttribute("background-color"), 16));
        }

        applyCommonAttributes(progressBar, data);
        return progressBar;
    }

    private BaseComponent createContainer(ComponentData data) {
        ContainerComponent container = new ContainerComponent(data) {
            @Override
            protected void updateLayout() {

            }

            @Override
            public boolean onMouseScroll(double mouseX, double mouseY, double delta) {
                return false;
            }
        };

        container.setPadding(data.getIntAttribute("padding", 0));

        if (data.hasAttribute("background-color")) {
            container.setBackgroundColor(Integer.parseInt(data.getAttribute("background-color"), 16));
        }

        // Create child components
        for (ComponentData childData : data.getChildren()) {
            BaseComponent child = createComponent(childData);
            if (child != null) {
                container.addChild(child);
            }
        }

        applyCommonAttributes(container, data);
        return container;
    }

    private BaseComponent createScrollContainer(ComponentData data) {
        ScrollContainerComponent container = new ScrollContainerComponent(data);

        container.setPadding(data.getIntAttribute("padding", 0));
        container.setHorizontalScroll(data.getBoolAttribute("horizontal-scroll", false));
        container.setVerticalScroll(data.getBoolAttribute("vertical-scroll", true));

        if (data.hasAttribute("background-color")) {
            container.setBackgroundColor(Integer.parseInt(data.getAttribute("background-color"), 16));
        }

        // Create child components
        for (ComponentData childData : data.getChildren()) {
            BaseComponent child = createComponent(childData);
            if (child != null) {
                container.addChild(child);
            }
        }

        applyCommonAttributes(container, data);
        return container;
    }

    private BaseComponent createSlider(ComponentData data) {
        SliderComponent slider = new SliderComponent(
                data.getIntAttribute("x", 0),
                data.getIntAttribute("y", 0),
                data.getIntAttribute("width", 100),
                data.getIntAttribute("height", 20)
        );

        slider.setMinValue(data.getFloatAttribute("min", 0f));
        slider.setMaxValue(data.getFloatAttribute("max", 1f));
        slider.setValue(data.getFloatAttribute("value", 0.5f));
        slider.setStep(data.getFloatAttribute("step", 0.01f));
        slider.setVertical(data.getBoolAttribute("vertical", false));
        slider.setShowValue(data.getBoolAttribute("show-value", false));

        applyCommonAttributes(slider, data);
        return slider;
    }

    // Helper method for color parsing
    private int parseColor(String colorStr) {
        if (colorStr == null) return 0xFFFFFFFF;
        if (colorStr.startsWith("#")) {
            colorStr = colorStr.substring(1);
        }
        try {
            return Integer.parseInt(colorStr, 16);
        } catch (NumberFormatException e) {
            Ovsr.LOGGER.warn("Invalid color format: {}", colorStr);
            return 0xFFFFFFFF;
        }
    }

    // Helper method for event handler binding
    private void bindEventHandler(BaseComponent component, ComponentData data, String eventName) {
        if (data.hasAttribute(eventName)) {
            String handlerId = data.getAttribute(eventName);
            // You'll need to implement an event handler registry system
            // EventHandlerRegistry.getInstance().bind(component, eventName, handlerId);
        }
    }

    private BaseComponent createFromTemplate(ComponentData data) {
        String templateId = data.getAttribute("id");
        if (templateId == null) {
            Ovsr.LOGGER.error("Template component missing id attribute");
            return null;
        }

        Map<String, String> parameters = new HashMap<>();
        for (Map.Entry<String, String> entry : data.getAttributes().entrySet()) {
            if (!entry.getKey().equals("id")) {
                parameters.put(entry.getKey(), entry.getValue());
            }
        }

        ComponentData instanceData = TemplateManager.getInstance()
                .instantiateTemplate(templateId, parameters);

        return instanceData != null ? createComponent(instanceData) : null;
    }

    private void applyCommonAttributes(BaseComponent component, ComponentData data) {
        if (data.hasAttribute("id")) {
            component.setId(data.getAttribute("id"));
        }

        component.setVisible(data.getBoolAttribute("visible", true));
        component.setEnabled(data.getBoolAttribute("enabled", true));

        if (data.hasAttribute("tooltip")) {
            component.setTooltip(Component.literal(data.getAttribute("tooltip")));
        }
    }
}
