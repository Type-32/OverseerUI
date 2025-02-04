package cn.crtlprototypestudios.ovsr.client.api.reactive;

public interface Signal<T> {
    T get();
    void set(T value);
    void subscribe(Runnable subscriber);
    void unsubscribe(Runnable subscriber);
}
