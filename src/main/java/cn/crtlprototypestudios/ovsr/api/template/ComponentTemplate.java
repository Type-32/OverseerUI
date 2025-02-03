package cn.crtlprototypestudios.ovsr.api.template;

import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComponentTemplate {
    private final ComponentData templateData;
    private final Set<String> parameters;
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\{\\{([^}]+)}}");

    public ComponentTemplate(ComponentData templateData) {
        this.templateData = templateData;
        this.parameters = extractParameters(templateData);
    }

    private Set<String> extractParameters(ComponentData data) {
        Set<String> params = new HashSet<>();
        Element element = data.getElement();

        // Extract parameters from attributes
        if (element.hasAttributes()) {
            for (int i = 0; i < element.getAttributes().getLength(); i++) {
                Node attr = element.getAttributes().item(i);
                findParameters(attr.getNodeValue(), params);
            }
        }

        // Extract parameters from child elements recursively
        for (ComponentData child : data.getChildren()) {
            params.addAll(extractParameters(child));
        }

        return params;
    }

    private void findParameters(String text, Set<String> params) {
        if (text == null) return;
        Matcher matcher = PARAMETER_PATTERN.matcher(text);
        while (matcher.find()) {
            params.add(matcher.group(1));
        }
    }

    public ComponentData createInstance(Map<String, String> parameters) {
        return cloneWithParameters(templateData, parameters);
    }

    private ComponentData cloneWithParameters(ComponentData original, Map<String, String> parameters) {
        Element element = (Element) original.getElement().cloneNode(true);

        // Replace parameters in attributes
        if (element.hasAttributes()) {
            for (int i = 0; i < element.getAttributes().getLength(); i++) {
                Node attr = element.getAttributes().item(i);
                String value = attr.getNodeValue();
                attr.setNodeValue(replaceParameters(value, parameters));
            }
        }

        // Create new ComponentData with replaced parameters
        ComponentData cloned = new ComponentData(element);

        return cloned;
    }

    private String replaceParameters(String text, Map<String, String> parameters) {
        if (text == null) return null;

        Matcher matcher = PARAMETER_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String paramName = matcher.group(1);
            String replacement = parameters.getOrDefault(paramName, "");
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public static ComponentTemplate fromXML(String xml) throws Exception {
        ComponentData data = ComponentData.fromXML(xml);
        return new ComponentTemplate(data);
    }
}
