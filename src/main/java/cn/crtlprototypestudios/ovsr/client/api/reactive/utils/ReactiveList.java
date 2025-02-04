package cn.crtlprototypestudios.ovsr.client.api.reactive.utils;

import cn.crtlprototypestudios.ovsr.client.api.reactive.Ref;

import java.util.ArrayList;
import java.util.List;

public class ReactiveList<T> extends ArrayList<T> {
    private final List<T> internal = new ArrayList<>();
    private final Ref<Integer> size = new Ref<>(0);

    @Override
    public boolean add(T element) {
        boolean result = internal.add(element);
        size.set(internal.size());
        return result;
    }

    @Override
    public void clear() {
        internal.clear();
        size.set(0);
    }

    public Ref<Integer> useSize() {
        return size;
    }

    public Ref<List<T>> useSnapshot() {
        return new Ref<>(new ArrayList<>(internal));
    }
}
