package cn.crtlprototypestudios.ovsr.client.api.reactive;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class Effect {
    private final Runnable effect;
    private Set<Signal<?>> dependencies = new HashSet<>();
    private boolean active = true;

    public Effect(Runnable effect) {
        this.effect = effect;
        execute();
    }

    public void execute() {
        if (!active) return;

        cleanup();
        ReactiveScope.pushEffect(this);
        try {
            effect.run();
        } finally {
            ReactiveScope.popEffect();
        }
    }

    public <T> T execute(Supplier<T> supplier) {
        cleanup();
        ReactiveScope.pushEffect(this);
        try {
            return supplier.get();
        } finally {
            ReactiveScope.popEffect();
        }
    }

    void trackDependency(Signal<?> signal) {
        dependencies.add(signal);
        signal.subscribe(this::execute);
    }

    private void cleanup() {
        // Remove old dependencies
        for (Signal<?> dep : dependencies) {
            dep.unsubscribe(this::execute);
        }
        dependencies.clear();
    }

    public void stop() {
        active = false;
        cleanup();
    }
}
