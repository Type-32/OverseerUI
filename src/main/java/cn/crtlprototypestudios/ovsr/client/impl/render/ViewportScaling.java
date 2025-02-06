package cn.crtlprototypestudios.ovsr.client.impl.render;

import com.mojang.blaze3d.platform.Window;
import imgui.internal.ImGuiDockNode;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector2d;

@OnlyIn(Dist.CLIENT)
public class ViewportScaling {
    public static int X_OFFSET = 0;
    public static int Y_OFFSET = 0;
    public static int Y_TOP_OFFSET = 0;
    public static int WIDTH = 0;
    public static int HEIGHT = 0;

    public static boolean DISABLE_POST_PROCESSORS = false;

    private static Window getGameWindow() {
        return Minecraft.getInstance().getWindow();
    }

    public static Vector2d scalePoint(Vector2d point) {
        return scalePoint(point.x, point.y);
    }

    public static Vector2d scalePoint(double x, double y) {
        Window window = getGameWindow();

        float xScale = (float) WIDTH / window.getScreenWidth();
        float yScale = (float) HEIGHT / window.getScreenHeight();

        x *= xScale;
        y *= yScale;

        x += X_OFFSET;
        y += Y_OFFSET;

        return new Vector2d(x, y);
    }

    public static Vector2d unscalePoint(double x, double y) {
        Window window = getGameWindow();

        float xScale = (float) WIDTH / window.getScreenWidth();
        float yScale = (float) HEIGHT / window.getScreenHeight();

        x -= X_OFFSET;
        y -= Y_OFFSET;

        x /= xScale;
        y /= yScale;

        return new Vector2d(x, y);
    }

    public static Vector2d scaleWidthHeight(double width, double height) {
        Window window = getGameWindow();

        float x_scale = (float) WIDTH / window.getScreenWidth();
        float y_scale = (float) HEIGHT / window.getScreenHeight();

        width *= x_scale;
        height *= y_scale;

        return new Vector2d(width, height);
    }

    public static boolean isChanged() {
        Window window = getGameWindow();
        return !(window.getWidth() == WIDTH &&
                window.getHeight() == HEIGHT &&
                X_OFFSET == 0 &&
                Y_OFFSET == 0);
    }

    public static void update() {
        DISABLE_POST_PROCESSORS = isChanged();
    }

    public static void updateFromDockNode(ImGuiDockNode node) {
        Window window = getGameWindow();
        X_OFFSET = (int) node.getPosX() - window.getX();
        Y_OFFSET = (int) node.getPosY() - window.getY();
        Y_TOP_OFFSET = (int) (window.getHeight() - ((node.getPosY() - window.getY()) + node.getSizeY()));
        WIDTH = (int) node.getSizeX();
        HEIGHT = (int) node.getSizeY();
        update();
    }
}
