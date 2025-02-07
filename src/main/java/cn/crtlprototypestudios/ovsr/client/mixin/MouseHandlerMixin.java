package cn.crtlprototypestudios.ovsr.client.mixin;

import cn.crtlprototypestudios.ovsr.client.impl.render.ViewportScaling;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.Window;
import imgui.ImGui;
import net.minecraft.client.MouseHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @ModifyVariable(method = "onMove",
            at = @At("HEAD"),
            ordinal = 0, argsOnly = true)
    private double modifyX(double x) {
        return ViewportScaling.unscalePoint(x, 0).x;
    }

    @ModifyVariable(method = "onMove",
            at = @At("HEAD"),
            ordinal = 1, argsOnly = true)
    private double modifyY(double y) {
        return ViewportScaling.unscalePoint(0, y).y;
    }

    @Redirect(method = {"grabMouse", "releaseMouse"},
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/Window;getScreenWidth()I"))
    public int calculateDoubledCentreX(Window instance) {
        return (ViewportScaling.X_OFFSET + (ViewportScaling.WIDTH/2)) * 2;
    }

    @Redirect(method = {"grabMouse", "releaseMouse"},
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/Window;getScreenHeight()I"))
    public int calculateDoubledCentreY(Window instance) {
        return (ViewportScaling.Y_OFFSET + (ViewportScaling.HEIGHT/2)) * 2;
    }
}
