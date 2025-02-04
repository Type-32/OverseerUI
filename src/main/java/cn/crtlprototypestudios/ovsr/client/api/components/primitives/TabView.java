package cn.crtlprototypestudios.ovsr.client.api.components.primitives;

import cn.crtlprototypestudios.ovsr.client.api.components.Component;
import imgui.ImGui;

public class TabView extends Component {
    @Override
    protected void render() {
        if (ImGui.beginTabBar("##tabs")) {
            slots.forEach((name, slot) -> {
                if (ImGui.beginTabItem(name)) {
                    slot.render();
                    ImGui.endTabItem();
                }
            });
            ImGui.endTabBar();
        }
    }
}
