package cn.crtlprototypestudios.ovsr.client.api.reactive.composables;

import cn.crtlprototypestudios.ovsr.client.api.reactive.Effect;
import cn.crtlprototypestudios.ovsr.client.api.reactive.Ref;

public class UseState {
    public static <T> Ref<T> useState(String key, T defaultValue, Class<T> type) {
        T initialValue = StateStorage.get(key, type, defaultValue);
        Ref<T> state = new Ref<>(initialValue);

        // Auto-save state changes
        new Effect(() -> {
            StateStorage.set(key, state.get());
        });

        return state;
    }

    public static <T> Ref<T> useTransientState(T defaultValue) {
        return new Ref<>(defaultValue);
    }
}
