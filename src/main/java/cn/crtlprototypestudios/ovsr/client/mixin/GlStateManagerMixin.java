package cn.crtlprototypestudios.ovsr.client.mixin;

import cn.crtlprototypestudios.ovsr.client.impl.render.ViewportScaling;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector2d;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(value = GlStateManager.class, remap = false)
public class GlStateManagerMixin {

    @Unique
    private static boolean ovsr$skipViewport = false;
    @Unique
    private static boolean ovsr$skipScissor = false;

    @Inject(method = "_viewport", at = @At("HEAD"), cancellable = true)
    private static void onViewport(int x, int y, int width, int height, CallbackInfo ci) {
        if (ovsr$skipViewport) return; // Skip injected logic during our custom viewport call

        if (ViewportScaling.isChanged()) {
            ovsr$skipViewport = true; // Prevent recursive calls to the mixin
            GlStateManager._viewport(ViewportScaling.X_OFFSET,
                    ViewportScaling.Y_TOP_OFFSET,
                    ViewportScaling.WIDTH,
                    ViewportScaling.HEIGHT);
            ovsr$skipViewport = false;
            ci.cancel();
        }
    }

    @Inject(method = "_scissorBox", at = @At("HEAD"), cancellable = true)
    private static void onScissorBox(int x, int y, int width, int height, CallbackInfo ci) {
        if (ovsr$skipScissor) return; // Skip injected logic during our custom scissor call

        Vector2d xy = ViewportScaling.scalePoint(x, y);
        Vector2d wh = ViewportScaling.scaleWidthHeight(width, height);

        ovsr$skipScissor = true; // Prevent recursive calls to this mixin
        GlStateManager._scissorBox((int) xy.x, (int) xy.y, (int) wh.x, (int) wh.y);
        ovsr$skipScissor = false;

        ci.cancel();
    }

}
