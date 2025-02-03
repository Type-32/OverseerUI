package cn.crtlprototypestudios.ovsr.api.xml;

import cn.crtlprototypestudios.ovsr.api.components.BaseComponent;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class ComponentRegistry {
    private static final BiMap<ResourceLocation, Function<ComponentData, BaseComponent>> COMPONENTS = HashBiMap.create();

    public static void register(String name, Function<ComponentData, BaseComponent> factory) {
        register(new ResourceLocation("ovsr", name), factory);
    }

    public static void register(ResourceLocation id, Function<ComponentData, BaseComponent> factory) {
        COMPONENTS.put(id, factory);
    }

    public static Function<ComponentData, BaseComponent> get(String name) {
        return get(new ResourceLocation("ovsr", name));
    }

    public static Function<ComponentData, BaseComponent> get(ResourceLocation id) {
        return COMPONENTS.get(id);
    }

    public static ResourceLocation getId(Function<ComponentData, BaseComponent> factory) {
        return COMPONENTS.inverse().get(factory);
    }
}
