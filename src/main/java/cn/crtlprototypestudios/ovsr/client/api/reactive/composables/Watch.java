package cn.crtlprototypestudios.ovsr.client.api.reactive.composables;

import cn.crtlprototypestudios.ovsr.client.api.reactive.Effect;
import cn.crtlprototypestudios.ovsr.client.api.reactive.Signal;

import java.util.function.Consumer;

public class Watch {
    public static <T> Effect watch(Signal<T> signal, Consumer<T> callback) {
        return new Effect(() -> callback.accept(signal.get()));
    }

    public static <T> Effect watch(Signal<T> signal, Consumer<T> callback, boolean immediate) {
        Effect effect = new Effect(() -> callback.accept(signal.get()));
        if (immediate) {
            callback.accept(signal.get());
        }
        return effect;
    }

    public static <T> Effect watchEffect(Runnable effect) {
        return new Effect(effect);
    }

    public static <T, R> Effect watchDeep(Signal<T> signal, Consumer<T> callback, int debounceMs) {
        long[] lastCall = {0};
        return new Effect(() -> {
            long now = System.currentTimeMillis();
            if (now - lastCall[0] >= debounceMs) {
                lastCall[0] = now;
                callback.accept(signal.get());
            }
        });
    }
}
