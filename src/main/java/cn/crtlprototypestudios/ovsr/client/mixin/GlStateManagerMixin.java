package cn.crtlprototypestudios.ovsr.client.mixin;

import cn.crtlprototypestudios.ovsr.client.impl.render.ViewportScaling;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(value = GlStateManager.class, remap = false)
public class GlStateManagerMixin {

    @Inject(method = "_viewport", at = @At("HEAD"), cancellable = true)
    private static void onViewport(int i, int j, int k, int l, CallbackInfo ci) {
        if (ViewportScaling.isChanged()) {
            GlStateManager._viewport(ViewportScaling.X_OFFSET,
                    ViewportScaling.Y_TOP_OFFSET,
                    ViewportScaling.WIDTH,
                    ViewportScaling.HEIGHT);
            ci.cancel();
        }
    }

    @Inject(method = "_scissorBox", at = @At("HEAD"), cancellable = true)
    private static void onScissorBox(int i, int j, int k, int l, CallbackInfo ci) {
        Vector2d xy = ViewportScaling.scalePoint(i, j);
        Vector2d wh = ViewportScaling.scaleWidthHeight(k, l);
        GlStateManager._scissorBox((int) xy.x, (int) xy.y, (int) wh.x, (int) wh.y);
        ci.cancel();
    }
}
