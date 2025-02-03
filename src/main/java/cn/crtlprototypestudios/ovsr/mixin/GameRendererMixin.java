package cn.crtlprototypestudios.ovsr.mixin;

import cn.crtlprototypestudios.ovsr.impl.render.ImGuiManager;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderTail(CallbackInfo ci) {
        ImGuiManager.beginFrame();
        // Render UI components here
        ImGuiManager.endFrame();
    }
}
