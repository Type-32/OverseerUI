package cn.crtlprototypestudios.ovsr.client.api.components.primitives;

import cn.crtlprototypestudios.ovsr.client.api.components.Component;
import cn.crtlprototypestudios.ovsr.client.api.reactive.Ref;
import imgui.ImGui;

import java.util.List;
import java.util.function.Function;

public class Dropdown<T> extends Component {
    private final Ref<T> selectedValue;
    private final List<T> items;

    public Dropdown(Ref<T> value, List<T> items) {
        this.selectedValue = value;
        this.items = items;
    }

    @Override
    protected void render() {
        String label = props.get("label", "");
        Function<T, String> formatter = props.get("formatter", Object::toString);

        if (ImGui.beginCombo(label, formatter.apply(selectedValue.get()))) {
            for (T item : items) {
                boolean isSelected = item.equals(selectedValue.get());
                if (ImGui.selectable(formatter.apply(item), isSelected)) {
                    selectedValue.set(item);
                }
                if (isSelected) {
                    ImGui.setItemDefaultFocus();
                }
            }
            ImGui.endCombo();
        }
    }
}
