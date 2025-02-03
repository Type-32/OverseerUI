package cn.crtlprototypestudios.ovsr.api.event;

import cn.crtlprototypestudios.ovsr.Ovsr;
import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;

import java.util.*;

public class EventHandlerRegistry {
    private static EventHandlerRegistry INSTANCE;

    private final Map<String, EventHandler> namedHandlers;
    private final Map<BaseComponent, Map<String, List<EventHandler>>> componentHandlers;

    private EventHandlerRegistry() {
        this.namedHandlers = new HashMap<>();
        this.componentHandlers = new WeakHashMap<>();
    }

    public static EventHandlerRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EventHandlerRegistry();
        }
        return INSTANCE;
    }

    public void registerNamedHandler(String id, EventHandler handler) {
        namedHandlers.put(id, handler);
    }

    public void bindHandler(BaseComponent component, String eventType, EventHandler handler) {
        componentHandlers
                .computeIfAbsent(component, k -> new HashMap<>())
                .computeIfAbsent(eventType, k -> new ArrayList<>())
                .add(handler);
    }

    public void bindNamedHandler(BaseComponent component, String eventType, String handlerId) {
        EventHandler handler = namedHandlers.get(handlerId);
        if (handler != null) {
            bindHandler(component, eventType, handler);
        } else {
            Ovsr.LOGGER.warn("Named handler not found: {}", handlerId);
        }
    }

    public void unbindHandler(BaseComponent component, String eventType, EventHandler handler) {
        Map<String, List<EventHandler>> handlers = componentHandlers.get(component);
        if (handlers != null) {
            List<EventHandler> eventHandlers = handlers.get(eventType);
            if (eventHandlers != null) {
                eventHandlers.remove(handler);
                if (eventHandlers.isEmpty()) {
                    handlers.remove(eventType);
                }
            }
            if (handlers.isEmpty()) {
                componentHandlers.remove(component);
            }
        }
    }

    public void clearComponentHandlers(BaseComponent component) {
        componentHandlers.remove(component);
    }

    public void fireEvent(UIEvent event) {
        Map<String, List<EventHandler>> handlers = componentHandlers.get(event.getSource());
        if (handlers != null) {
            List<EventHandler> eventHandlers = handlers.get(event.getEventType());
            if (eventHandlers != null) {
                for (EventHandler handler : new ArrayList<>(eventHandlers)) {
                    try {
                        handler.handle(event);
                        if (event.isConsumed()) {
                            break;
                        }
                    } catch (Exception e) {
                        Ovsr.LOGGER.error("Error in event handler", e);
                    }
                }
            }
        }
    }
}
