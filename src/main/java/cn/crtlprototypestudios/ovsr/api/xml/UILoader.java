package cn.crtlprototypestudios.ovsr.api.xml;


import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class UILoader {
    private static final UIParser PARSER = new UIParser();

    public static BaseComponent load(String path) {
        return load(new ResourceLocation("ovsr", path));
    }

    public static BaseComponent load(ResourceLocation location) {
        return PARSER.parse(location);
    }

    public static BaseComponent loadTemplate(String templateName, Map<String, Object> parameters) {
        // TODO: Implement template loading with parameter substitution
        return null;
    }
}
