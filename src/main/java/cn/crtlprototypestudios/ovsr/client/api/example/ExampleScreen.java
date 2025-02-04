package cn.crtlprototypestudios.ovsr.client.api.example;

import cn.crtlprototypestudios.ovsr.client.api.components.Component;
import cn.crtlprototypestudios.ovsr.client.api.components.primitives.*;
import cn.crtlprototypestudios.ovsr.client.api.reactive.Ref;
import cn.crtlprototypestudios.ovsr.client.api.reactive.composables.UseState;
import cn.crtlprototypestudios.ovsr.client.api.screen.OverseerScreen;
import imgui.flag.ImGuiWindowFlags;

public class ExampleScreen extends OverseerScreen {
    private final Ref<String> username = UseState.useState("username", "", String.class);
    private final Ref<String> password = UseState.useState("password", "", String.class);

    protected ExampleScreen() {
        super(net.minecraft.network.chat.Component.literal("Example"), true);
    }

    @Override
    protected Component setup() {
        return new Window()
                .withProps(p -> {
                    p.set("title", "Login");
                    p.set("flags", ImGuiWindowFlags.AlwaysAutoResize);
                })
                .withChildren(
                        new VStack()
                                .withProps(p -> p.set("spacing", 5f))
                                .withChildren(
                                        new Text()
                                                .withProps(p -> p.set("text", "Username:")),
                                        new Input()
                                                .withProps(p -> {
                                                    p.setRef("modelValue", username);
                                                    p.set("hint", "Enter username");
                                                }),
                                        new Text()
                                                .withProps(p -> p.set("text", "Password:")),
                                        new Input()
                                                .withProps(p -> {
                                                    p.setRef("modelValue", password);
                                                    p.set("hint", "Enter password");
                                                }),
                                        new HStack()
                                                .withProps(p -> p.set("spacing", 10f))
                                                .withChildren(
                                                        new Button()
                                                                .withProps(p -> {
                                                                    p.set("text", "Login");
                                                                }),
                                                        new Button()
                                                                .withProps(p -> {
                                                                    p.set("text", "Cancel");
                                                                })
                                                )
                                )
                );
    }
}
