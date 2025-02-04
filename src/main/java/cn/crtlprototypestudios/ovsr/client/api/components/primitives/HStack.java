package cn.crtlprototypestudios.ovsr.client.api.components.primitives;

import cn.crtlprototypestudios.ovsr.client.api.components.Component;
import imgui.ImGui;

public class HStack extends Component {
    @Override
    protected void render() {
        float spacing = props.get("spacing", 0f);
        float currentX = ImGui.getCursorPosX();

        synchronized(children) {
            for (int i = 0; i < children.size(); i++) {
//                ImGui.setCursorPosX(currentX);
                children.get(i).internalRender();
                if (i < children.size() - 1) {
                    currentX = ImGui.getCursorPosX() + spacing;
                    ImGui.sameLine();
                }
            }
        }
    }
}
