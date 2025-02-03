package cn.crtlprototypestudios.ovsr.api.xml;

import net.minecraft.network.chat.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

public class ComponentData {
    private final Element element;
    private final Map<String, String> attributes;
    private final Map<String, Object> properties;

    public ComponentData(Element element) {
        this.element = element;
        this.attributes = new HashMap<>();
        this.properties = new HashMap<>();

        // Parse attributes if element is not null
        if (element != null && element.hasAttributes()) {
            var attrs = element.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                var attr = attrs.item(i);
                attributes.put(attr.getNodeName(), attr.getNodeValue());
            }
        }
    }

    // New helper methods for XML parsing
    public Element getChildElement(String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes != null && nodes.getLength() > 0) {
            Node node = nodes.item(0);
            if (node instanceof Element) {
                return (Element) node;
            }
        }
        return null;
    }

    public String getChildText(String tagName) {
        Element child = getChildElement(tagName);
        return child != null ? child.getTextContent() : null;
    }

    public String getChildAttribute(String tagName, String attributeName) {
        Element child = getChildElement(tagName);
        return child != null ? child.getAttribute(attributeName) : null;
    }

    public Component parseTextComponent(String tagName) {
        Element textElement = getChildElement(tagName);
        if (textElement != null) {
            boolean translatable = "true".equals(textElement.getAttribute("translatable"));
            String textContent = textElement.getTextContent();
            return translatable ?
                    Component.translatable(textContent) :
                    Component.literal(textContent);
        }
        return null;
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }

    public String getAttribute(String name, String defaultValue) {
        return attributes.getOrDefault(name, defaultValue);
    }

    public int getIntAttribute(String name, int defaultValue) {
        try {
            return Integer.parseInt(getAttribute(name));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public float getFloatAttribute(String name, float defaultValue) {
        try {
            return Float.parseFloat(getAttribute(name));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBoolAttribute(String name, boolean defaultValue) {
        var value = getAttribute(name);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key) {
        return (T) properties.get(key);
    }

    public Element getElement() {
        return element;
    }
}
