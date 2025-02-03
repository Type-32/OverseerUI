package cn.crtlprototypestudios.ovsr.api.reload;

import cn.crtlprototypestudios.ovsr.api.xml.ComponentData;

public class UIDefinition {
    private final String id;
    private final ComponentData rootComponent;
    private long lastModified;

    public UIDefinition(String id, ComponentData rootComponent) {
        this.id = id;
        this.rootComponent = rootComponent;
        this.lastModified = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public ComponentData getRootComponent() {
        return rootComponent;
    }

    public long getLastModified() {
        return lastModified;
    }
}
