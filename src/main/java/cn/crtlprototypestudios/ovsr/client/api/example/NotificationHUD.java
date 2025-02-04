package cn.crtlprototypestudios.ovsr.client.api.example;

import cn.crtlprototypestudios.ovsr.client.api.OverseerHUD;
import cn.crtlprototypestudios.ovsr.client.impl.theme.ImGuiDarkTheme;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NotificationHUD extends OverseerHUD.HUDElement {
    private final List<Notification> notifications = new ArrayList<>();

    public NotificationHUD() {
        super("notifications", new ImGuiDarkTheme());
    }

    @Override
    protected boolean shouldRender(Minecraft mc) {
        // Call parent check first
        if (!super.shouldRender(mc)) return false;

        // Add custom conditions
        return !notifications.isEmpty();
    }

    @Override
    protected void renderContent() {
        Iterator<Notification> iterator = notifications.iterator();
        while (iterator.hasNext()) {
            Notification notification = iterator.next();
            if (notification.isExpired()) {
                iterator.remove();
                continue;
            }

            // Draw notification
            ImGui.pushStyleVar(ImGuiStyleVar.Alpha, notification.getAlpha());
            ImGui.text(notification.getMessage());

            if (notification.hasActions()) {
                ImGui.sameLine();
                if (ImGui.button("Accept")) {
                    notification.accept();
                    iterator.remove();
                    continue;
                }
                ImGui.sameLine();
                if (ImGui.button("Deny")) {
                    notification.deny();
                    iterator.remove();
                    continue;
                }
            }

            ImGui.popStyleVar();
            ImGui.separator();
        }
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }
}
