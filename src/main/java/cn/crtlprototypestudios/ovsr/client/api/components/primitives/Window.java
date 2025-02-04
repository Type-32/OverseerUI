package cn.crtlprototypestudios.ovsr.client.api.components.primitives;

import cn.crtlprototypestudios.ovsr.client.api.components.Component;
import cn.crtlprototypestudios.ovsr.client.api.components.ComponentCallback;
import cn.crtlprototypestudios.ovsr.client.api.reactive.Ref;
import imgui.ImGui;
import imgui.type.ImBoolean;

public class Window extends Component {
    private final ImBoolean isOpen = new ImBoolean(true);

    @Override
    protected void render() {
        String title = props.get("title", "Window");
        int flags = props.get("flags", 0);

        if (ImGui.begin(title, isOpen, flags)) {
            // Save window position
            lastX = ImGui.getWindowPosX();
            lastY = ImGui.getWindowPosY();

            // Create a child window to contain the content
            ImGui.beginChild("##content", ImGui.getWindowWidth(), ImGui.getWindowHeight(), false);

            // Render children synchronously
            synchronized(children) {
                for (Component child : children) {
                    child.internalRender();
                }
            }

            ImGui.endChild();
        }
        ImGui.end();

        if (!isOpen.get()) {
            ComponentCallback onClose = props.getCallback("onClose");
            if (onClose != null) {
                onClose.invoke();
            }
        }
    }
}


