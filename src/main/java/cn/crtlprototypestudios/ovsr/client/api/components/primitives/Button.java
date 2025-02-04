package cn.crtlprototypestudios.ovsr.client.api.components.primitives;

import cn.crtlprototypestudios.ovsr.client.api.components.Component;
import cn.crtlprototypestudios.ovsr.client.api.components.ComponentCallback;
import imgui.ImGui;

public class Button extends Component {
    @Override
    protected void render() {
        String text = props.get("text", "");
        if (ImGui.button(text)) {
            ComponentCallback onClick = props.getCallback("onClick");
            if (onClick != null) {
                onClick.invoke();
            }
        }
    }
}
