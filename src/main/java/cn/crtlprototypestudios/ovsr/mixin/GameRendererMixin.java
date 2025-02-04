package cn.crtlprototypestudios.ovsr.mixin;

import cn.crtlprototypestudios.ovsr.impl.render.ImGuiManager;
import cn.crtlprototypestudios.ovsr.impl.render.ViewportScaling;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    
}
