package cn.crtlprototypestudios.ovsr.client.api.components.primitives;

import cn.crtlprototypestudios.ovsr.client.api.components.Component;
import cn.crtlprototypestudios.ovsr.client.api.reactive.Ref;
import imgui.ImGui;
import imgui.type.ImString;

public class Input extends Component {
    private final ImString buffer = new ImString(256);
    private Ref<String> modelValue;
    private boolean initialized = false;

    @Override
    protected void render() {
        if (!initialized) {
            modelValue = props.getRef("modelValue");
            if (modelValue != null) {
                buffer.set(modelValue.get());
            }
            initialized = true;
        }

        String hint = props.get("hint", "");
        if (ImGui.inputText("##" + id, buffer)) {
            if (modelValue != null) {
                modelValue.set(buffer.get());
            }
        }

        if (!hint.isEmpty() && buffer.get().isEmpty()) {
            float posX = ImGui.getItemRectMin().x;
            float posY = ImGui.getItemRectMin().y;
            ImGui.setCursorPos(posX + 5, posY + 5);
            ImGui.textDisabled(hint);
        }
    }
}


