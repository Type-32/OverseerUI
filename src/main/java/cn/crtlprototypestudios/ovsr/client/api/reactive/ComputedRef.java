package cn.crtlprototypestudios.ovsr.client.api.reactive;

import java.util.*;
import java.util.function.Supplier;

public class ComputedRef<T> implements Signal<T> {
    private final Supplier<T> computer;
    private T cachedValue;
    private boolean dirty = true;
    private final Set<Signal<?>> dependencies = new HashSet<>();
    private final List<Runnable> subscribers = new ArrayList<>();
    private final Effect effect;

    public ComputedRef(Supplier<T> computer) {
        this.computer = computer;
        this.effect = new Effect(() -> {
            T newValue = compute();
            if (!Objects.equals(cachedValue, newValue)) {
                cachedValue = newValue;
                notifySubscribers();
            }
        });
    }

    public static <T> ComputedRef<T> of(Supplier<T> computer) {
        return new ComputedRef<>(computer);
    }

    @Override
    public T get() {
        ReactiveScope.track(this);
        if (dirty) {
            cachedValue = compute();
            dirty = false;
        }
        return cachedValue;
    }

    @Override
    public void set(T value) {
        throw new UnsupportedOperationException("Cannot set the value of a computed reference");
    }

    private T compute() {
        return effect.execute(computer);
    }

    @Override
    public void subscribe(Runnable subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void unsubscribe(Runnable subscriber) {
        subscribers.remove(subscriber);
    }

    private void notifySubscribers() {
        for (Runnable subscriber : new ArrayList<>(subscribers)) {
            subscriber.run();
        }
    }
}
