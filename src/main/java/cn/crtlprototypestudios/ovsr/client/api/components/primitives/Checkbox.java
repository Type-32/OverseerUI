package cn.crtlprototypestudios.ovsr.client.api.components.primitives;

import cn.crtlprototypestudios.ovsr.client.api.components.Component;
import cn.crtlprototypestudios.ovsr.client.api.reactive.Ref;
import imgui.ImGui;
import imgui.type.ImBoolean;

public class Checkbox extends Component {
    private ImBoolean buffer;

    @Override
    protected void onMounted() {
        Ref<Boolean> checked = props.getRef("checked");
        buffer = new ImBoolean(checked != null ? checked.get() : false);
    }

    @Override
    protected void render() {
        String label = props.get("label", "");

        if (ImGui.checkbox(label, buffer)) {
            Ref<Boolean> checked = props.getRef("checked");
            if (checked != null) {
                checked.set(buffer.get());
            }
        }
    }
}

