package cn.crtlprototypestudios.ovsr.mixin;

import cn.crtlprototypestudios.ovsr.impl.render.ImGuiManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderSystem.class, remap = false)
public class TailRenderMixin {
    @Inject(at = @At("HEAD"), method="flipFrame")
    private static void runTickTail(CallbackInfo ci) {
        ImGuiManager.onFrameRender();
    }
}
