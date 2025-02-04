package cn.crtlprototypestudios.ovsr.client.api.components.primitives;

import cn.crtlprototypestudios.ovsr.client.api.components.Component;
import cn.crtlprototypestudios.ovsr.client.api.reactive.Ref;
import imgui.ImGui;

public class Modal extends Component {
    private final Ref<Boolean> isOpen;

    public Modal(Ref<Boolean> isOpen) {
        this.isOpen = isOpen;
    }

    @Override
    protected void render() {
        if (!isOpen.get()) return;

        String title = props.get("title", "Modal");
        ImGui.openPopup(title);

        if (ImGui.beginPopupModal(title)) {
            children.forEach(Component::internalRender);
            ImGui.endPopup();
        }
    }
}
