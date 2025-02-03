package cn.crtlprototypestudios.ovsr.api.event;

@FunctionalInterface
public interface EventHandler {
    void handle(UIEvent event);
}
