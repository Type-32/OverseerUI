package cn.crtlprototypestudios.ovsr.client.api.screen;

import cn.crtlprototypestudios.ovsr.client.api.components.OverseerComponent;
import cn.crtlprototypestudios.ovsr.client.impl.screen.ImGuiScreen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class OverseerScreen extends ImGuiScreen {
    private final List<OverseerComponent> components = new ArrayList<>();

    protected OverseerScreen(Component component, boolean closeWhenNoWindows) {
        super(component, closeWhenNoWindows);
    }

    protected cn.crtlprototypestudios.ovsr.client.api.components.Component setup() {
        return null;
    }
}
