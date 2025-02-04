package cn.crtlprototypestudios.ovsr.client.api.components.primitives;

import cn.crtlprototypestudios.ovsr.client.api.components.Component;
import cn.crtlprototypestudios.ovsr.client.api.reactive.Ref;
import imgui.ImGui;

public class Slider extends Component {
    private final float[] buffer = new float[1];
    private Ref<Integer> value;
    private boolean initialized = false;

    @Override
    protected void render() {
        if (!initialized) {
            value = props.getRef("value");
            if (value != null) {
                buffer[0] = value.get();
            }
            initialized = true;
        }

        int min = props.get("min", 0);
        int max = props.get("max", 100);
        String format = props.get("format", "%d");

        if (ImGui.sliderFloat("##" + id, buffer, min, max, format)) {
            if (value != null) {
                value.set((int) buffer[0]);
            }
        }
    }
}


