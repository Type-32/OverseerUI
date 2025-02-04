package cn.crtlprototypestudios.ovsr.client.api.components;

import java.util.List;

public abstract class OverseerComponent {
    protected Props props;
    protected List<OverseerComponent> children;

    // Vue-like lifecycle hooks
    protected void onMounted() {}
    protected void onUnmounted() {}

    // Render method that returns ImGui commands
    protected abstract void render();
}
