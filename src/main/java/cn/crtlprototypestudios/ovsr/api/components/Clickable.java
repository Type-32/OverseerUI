package cn.crtlprototypestudios.ovsr.api.components;

public interface Clickable {
    boolean onClick(double mouseX, double mouseY, int button);
    boolean onRelease(double mouseX, double mouseY, int button);
}