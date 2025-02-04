package cn.crtlprototypestudios.ovsr.client.mixin;

import cn.crtlprototypestudios.ovsr.client.impl.render.ViewportScaling;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.platform.GlStateManager;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = GlStateManager.class, remap = false)
public class GlStateManagerMixin {

    @WrapMethod(method = "_viewport")
    private static void viewport(int i, int j, int k, int l, Operation<Void> original) {
        if (!ViewportScaling.isChanged()) {
            original.call(i, j, k, l);
            return;
        }
        original.call(ViewportScaling.X_OFFSET, ViewportScaling.Y_TOP_OFFSET, ViewportScaling.WIDTH, ViewportScaling.HEIGHT);
    }

    @WrapMethod(method = "_scissorBox")
    private static void scissorBox(int i, int j, int k, int l, Operation<Void> original) {
        Vector2d xy = ViewportScaling.scalePoint(i, j);
        Vector2d wh = ViewportScaling.scaleWidthHeight(k, l);
        original.call((int) xy.x, (int) xy.y, (int) wh.x, (int) wh.y);
    }

}
