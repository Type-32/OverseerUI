package cn.crtlprototypestudios.ovsr.client.api.components.primitives;

import cn.crtlprototypestudios.ovsr.client.api.components.Component;
import imgui.ImGui;

public class Text extends Component {
    @Override
    protected void render() {
        String text = props.get("text", "");
        ImGui.text(text);
    }
}
