package cn.crtlprototypestudios.ovsr.api.xml;

import cn.crtlprototypestudios.ovsr.Ovsr;
import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;
import cn.crtlprototypestudios.ovsr.api.components.ContainerComponent;
import net.minecraft.resources.ResourceLocation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UIParser {
    private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();
    private final Map<String, Document> cachedTemplates = new HashMap<>();

    public BaseComponent parse(ResourceLocation location) {
        try {
            String path = String.format("/assets/%s/ui/%s.xml", location.getNamespace(), location.getPath());
            try (InputStream is = Ovsr.class.getResourceAsStream(path)) {
                if (is == null) {
                    throw new RuntimeException("UI template not found: " + location);
                }

                DocumentBuilder builder = FACTORY.newDocumentBuilder();
                Document doc = builder.parse(is);
                doc.getDocumentElement().normalize();

                // Cache templates if any
                NodeList templates = doc.getElementsByTagName("template");
                for (int i = 0; i < templates.getLength(); i++) {
                    Element template = (Element) templates.item(i);
                    String name = template.getAttribute("name");
                    cachedTemplates.put(name, doc);
                }

                // Parse components
                Element root = (Element) doc.getElementsByTagName("components").item(0);
                return parseComponent(root.getFirstChild());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse UI template: " + location, e);
        }
    }

    private BaseComponent parseComponent(org.w3c.dom.Node node) {
        if (!(node instanceof Element element)) {
            return null;
        }

        String tagName = element.getTagName();
        var factory = ComponentRegistry.get(tagName);
        if (factory == null) {
            throw new RuntimeException("Unknown component type: " + tagName);
        }

        ComponentData data = new ComponentData(element);
        BaseComponent component = factory.apply(data);

        // Handle children if it's a container
        if (component instanceof ContainerComponent container) {
            NodeList children = element.getElementsByTagName("children").item(0).getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                BaseComponent child = parseComponent(children.item(i));
                if (child != null) {
                    container.addChild(child);
                }
            }
        }

        return component;
    }

    public Document getTemplate(String name) {
        return cachedTemplates.get(name);
    }
}
