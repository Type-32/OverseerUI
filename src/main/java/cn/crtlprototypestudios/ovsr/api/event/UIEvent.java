package cn.crtlprototypestudios.ovsr.api.event;

import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;

public class UIEvent {
    private final BaseComponent source;
    private final String eventType;
    private boolean consumed;
    private Object data;

    public UIEvent(BaseComponent source, String eventType) {
        this.source = source;
        this.eventType = eventType;
    }

    public UIEvent(BaseComponent source, String eventType, Object data) {
        this(source, eventType);
        this.data = data;
    }

    public BaseComponent getSource() {
        return source;
    }

    public String getEventType() {
        return eventType;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void consume() {
        this.consumed = true;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
