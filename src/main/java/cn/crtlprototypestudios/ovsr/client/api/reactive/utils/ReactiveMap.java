package cn.crtlprototypestudios.ovsr.client.api.reactive.utils;

import cn.crtlprototypestudios.ovsr.client.api.reactive.ComputedRef;
import cn.crtlprototypestudios.ovsr.client.api.reactive.Ref;

import java.util.*;
import java.util.function.*;

public class ReactiveMap<K, V> implements Map<K, V> {
    private final Map<K, V> internal = new HashMap<>();
    private final Ref<Integer> size = new Ref<>(0);
    private final Ref<Boolean> isEmpty = new Ref<>(true);
    private final List<BiConsumer<K, V>> onPutListeners = new ArrayList<>();
    private final List<BiConsumer<K, V>> onRemoveListeners = new ArrayList<>();

    // Basic Map operations with reactivity
    @Override
    public V put(K key, V value) {
        V oldValue = internal.put(key, value);
        updateReactiveState();
        notifyPutListeners(key, value);
        return oldValue;
    }

    @Override
    public V remove(Object key) {
        V removedValue = internal.remove(key);
        if (removedValue != null) {
            updateReactiveState();
            notifyRemoveListeners((K) key, removedValue);
        }
        return removedValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        internal.putAll(m);
        updateReactiveState();
        m.forEach(this::notifyPutListeners);
    }

    @Override
    public void clear() {
        Map<K, V> old = new HashMap<>(internal);
        internal.clear();
        updateReactiveState();
        old.forEach(this::notifyRemoveListeners);
    }

    // Reactive getters
    @Override
    public V get(Object key) {
        return internal.get(key);
    }

    public Ref<V> getReactive(K key) {
        return new Ref<V>(null) {
            @Override
            public V get() {
                return internal.get(key);
            }

            @Override
            public void set(V value) {
                put(key, value);
            }
        };
    }

    // Reactive properties
    public Ref<Integer> useSize() {
        return size;
    }

    public Ref<Boolean> useIsEmpty() {
        return isEmpty;
    }

    public ComputedRef<Set<K>> useKeys() {
        return ComputedRef.of(this::keySet);
    }

    public ComputedRef<Collection<V>> useValues() {
        return ComputedRef.of(this::values);
    }

    public ComputedRef<Set<Entry<K, V>>> useEntries() {
        return ComputedRef.of(this::entrySet);
    }

    // Event listeners
    public void onPut(BiConsumer<K, V> listener) {
        onPutListeners.add(listener);
    }

    public void onRemove(BiConsumer<K, V> listener) {
        onRemoveListeners.add(listener);
    }

    public void removeOnPutListener(BiConsumer<K, V> listener) {
        onPutListeners.remove(listener);
    }

    public void removeOnRemoveListener(BiConsumer<K, V> listener) {
        onRemoveListeners.remove(listener);
    }

    // Utility methods
    public <R> ReactiveMap<K, R> mapValues(Function<V, R> mapper) {
        ReactiveMap<K, R> result = new ReactiveMap<>();
        forEach((k, v) -> result.put(k, mapper.apply(v)));
        return result;
    }

    public ReactiveMap<K, V> filter(BiFunction<K, V, Boolean> predicate) {
        ReactiveMap<K, V> result = new ReactiveMap<>();
        forEach((k, v) -> {
            if (predicate.apply(k, v)) {
                result.put(k, v);
            }
        });
        return result;
    }

    public void update(K key, Function<V, V> updater) {
        V oldValue = get(key);
        if (oldValue != null) {
            put(key, updater.apply(oldValue));
        }
    }

    public V getOrDefault(Object key, V defaultValue) {
        return internal.getOrDefault(key, defaultValue);
    }

    public V getOrCompute(K key, Supplier<V> supplier) {
        V value = get(key);
        if (value == null) {
            value = supplier.get();
            put(key, value);
        }
        return value;
    }

    // Map interface implementation
    @Override
    public int size() {
        return internal.size();
    }

    @Override
    public boolean isEmpty() {
        return internal.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return internal.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return internal.containsValue(value);
    }

    @Override
    public Set<K> keySet() {
        return internal.keySet();
    }

    @Override
    public Collection<V> values() {
        return internal.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return internal.entrySet();
    }

    // Private helper methods
    private void updateReactiveState() {
        size.set(internal.size());
        isEmpty.set(internal.isEmpty());
    }

    private void notifyPutListeners(K key, V value) {
        onPutListeners.forEach(listener -> listener.accept(key, value));
    }

    private void notifyRemoveListeners(K key, V value) {
        onRemoveListeners.forEach(listener -> listener.accept(key, value));
    }

    // Additional utility methods for reactive operations
    public <T> ComputedRef<T> compute(Function<Map<K, V>, T> computer) {
        return ComputedRef.of(() -> computer.apply(this));
    }

    public void transaction(Consumer<ReactiveMap<K, V>> operations) {
        operations.accept(this);
        updateReactiveState();
    }

    // Snapshot utilities
    public Map<K, V> snapshot() {
        return new HashMap<>(internal);
    }

    public void restore(Map<K, V> snapshot) {
        clear();
        putAll(snapshot);
    }
}
