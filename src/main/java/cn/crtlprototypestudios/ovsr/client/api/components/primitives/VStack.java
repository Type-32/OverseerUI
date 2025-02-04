package cn.crtlprototypestudios.ovsr.client.api.components.primitives;

import cn.crtlprototypestudios.ovsr.client.api.components.Component;
import imgui.ImGui;

public class VStack extends Component {
    @Override
    protected void render() {
        float spacing = props.get("spacing", 0f);
        float currentY = ImGui.getCursorPosY();

        synchronized(children) {
            for (int i = 0; i < children.size(); i++) {
                ImGui.setCursorPosY(currentY);
                children.get(i).internalRender();
                currentY = ImGui.getCursorPosY() + spacing;
            }
        }
    }
}