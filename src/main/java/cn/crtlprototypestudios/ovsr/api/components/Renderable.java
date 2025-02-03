package cn.crtlprototypestudios.ovsr.api.components;

import net.minecraft.client.gui.GuiGraphics;

public interface Renderable {
    void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);
}
