package cn.crtlprototypestudios.ovsr.client.mixin;

import cn.crtlprototypestudios.ovsr.Ovsr;
import cn.crtlprototypestudios.ovsr.client.impl.render.ImGuiManager;
import imgui.ImGui;
import net.minecraft.client.KeyboardHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void onKeyPress(long window, int key, int scancode, int action, int mods, CallbackInfo ci) {
//        Ovsr.LOGGER.info("Should cancel game keyboard inputs: {} {} {}, {}", Ovsr.shouldCancelGameKeyboardInputs(), ImGui.isAnyItemActive(), ImGui.isAnyItemFocused(), key);
        if (Ovsr.shouldCancelGameKeyboardInputs()) {
            ci.cancel();
        }
    }

    @Inject(method="setup", at = @At("TAIL"))
    public void setup(long l, CallbackInfo ci) {
        ImGuiManager.onGlfwInit(l);
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    public void charTyped(long l, int i, int j, CallbackInfo ci) {
//        Ovsr.LOGGER.info("Should cancel game keyboard inputs: {} {} {}", Ovsr.shouldCancelGameKeyboardInputs(), ImGui.isAnyItemActive(), ImGui.isAnyItemFocused());
        if (Ovsr.shouldCancelGameKeyboardInputs())
            ci.cancel();
    }
}