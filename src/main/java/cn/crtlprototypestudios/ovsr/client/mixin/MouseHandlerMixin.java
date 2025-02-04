package cn.crtlprototypestudios.ovsr.client.mixin;

import cn.crtlprototypestudios.ovsr.client.impl.render.ViewportScaling;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.Window;
import imgui.ImGui;
import net.minecraft.client.MouseHandler;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
//    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
//    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
//        if (ImGui.getIO().getWantCaptureMouse()) {
//            ci.cancel();
//        }
//    }
    @WrapMethod(method = "onMove")
    public void onMove(long l, double d, double e, Operation<Void> original) {
        Vector2d scaled = ViewportScaling.unscalePoint(d, e);
        original.call(l, scaled.x, scaled.y);
    }

    @WrapOperation(method = {"grabMouse", "releaseMouse"}, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;getScreenWidth()I"))
    public int calculateDoubledCentreX(Window instance, Operation<Integer> original) {
        return (ViewportScaling.X_OFFSET + (ViewportScaling.WIDTH/2)) * 2;
    }

    @WrapOperation(method = {"grabMouse", "releaseMouse"}, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;getScreenHeight()I"))
    public int calculateDoubledCentreY(Window instance, Operation<Integer> original) {
        return (ViewportScaling.Y_OFFSET + (ViewportScaling.HEIGHT/2)) * 2;
    }
}
