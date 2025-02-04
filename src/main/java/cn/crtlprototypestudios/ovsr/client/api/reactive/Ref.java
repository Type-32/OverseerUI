package cn.crtlprototypestudios.ovsr.client.api.reactive;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ref<T> implements Signal<T> {
    private T value;
    private final List<Runnable> subscribers = new ArrayList<>();

    public Ref(T initialValue) {
        this.value = initialValue;
    }

    public static <T> Ref<T> of(T initialValue) {
        return new Ref<>(initialValue);
    }

    @Override
    public T get() {
        ReactiveScope.track(this);
        return value;
    }

    @Override
    public void set(T newValue) {
        if (!Objects.equals(value, newValue)) {
            T oldValue = this.value;
            this.value = newValue;
            notifySubscribers(oldValue, newValue);
        }
    }

    @Override
    public void subscribe(Runnable subscriber) {
        if (!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
        }
    }

    @Override
    public void unsubscribe(Runnable subscriber) {
        subscribers.remove(subscriber);
    }

    protected void notifySubscribers(T oldValue, T newValue) {
        for (Runnable subscriber : new ArrayList<>(subscribers)) {
            subscriber.run();
        }
    }
}
