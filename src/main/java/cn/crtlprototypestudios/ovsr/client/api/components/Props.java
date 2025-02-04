package cn.crtlprototypestudios.ovsr.client.api.components;

import cn.crtlprototypestudios.ovsr.client.api.reactive.Ref;

import java.util.HashMap;
import java.util.Map;

public class Props {
    private final Map<String, Object> values = new HashMap<>();
    private final Map<String, Ref<?>> reactiveValues = new HashMap<>();

    public <T> void set(String key, T value) {
        values.put(key, value);
    }

    public <T> void setRef(String key, Ref<T> value) {
        reactiveValues.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) values.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> Ref<T> getRef(String key) {
        return (Ref<T>) reactiveValues.get(key);
    }

    public <T> T get(String key, T defaultValue) {
        return values.containsKey(key) ? get(key) : defaultValue;
    }

    // Add specific method for callbacks
    public void setCallback(String key, ComponentCallback callback) {
        values.put(key, callback);
    }

    // Add specific getter for callbacks
    public ComponentCallback getCallback(String key) {
        return (ComponentCallback) values.get(key);
    }
}
