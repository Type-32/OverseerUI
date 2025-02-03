package cn.crtlprototypestudios.ovsr.api.template;

import cn.crtlprototypestudios.ovsr.Ovsr;
import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;

import java.util.HashMap;
import java.util.Map;

public class TemplateManager {
    private static TemplateManager INSTANCE;

    private final Map<String, ComponentTemplate> templates;

    private TemplateManager() {
        this.templates = new HashMap<>();
    }

    public static TemplateManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TemplateManager();
        }
        return INSTANCE;
    }

    public void registerTemplate(String id, ComponentTemplate template) {
        templates.put(id, template);
        Ovsr.LOGGER.debug("Registered template: {}", id);
    }

    public void registerTemplateFromXML(String id, String xml) {
        try {
            ComponentTemplate template = ComponentTemplate.fromXML(xml);
            registerTemplate(id, template);
        } catch (Exception e) {
            Ovsr.LOGGER.error("Failed to register template from XML: {}", id, e);
        }
    }

    public ComponentData instantiateTemplate(String templateId, Map<String, String> parameters) {
        ComponentTemplate template = templates.get(templateId);
        if (template == null) {
            Ovsr.LOGGER.error("Template not found: {}", templateId);
            return null;
        }
        return template.createInstance(parameters);
    }
}
