package cn.crtlprototypestudios.ovsr.client.api.components.primitives;

import cn.crtlprototypestudios.ovsr.client.api.components.Component;
import cn.crtlprototypestudios.ovsr.client.api.reactive.Ref;

public class Conditional extends Component {
    @Override
    protected void render() {
        Ref<Boolean> when = props.getRef("when");
        if (when.get()) {
            children.forEach(Component::internalRender);
        }
    }
}
