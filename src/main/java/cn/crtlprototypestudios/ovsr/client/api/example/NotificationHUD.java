package cn.crtlprototypestudios.ovsr.client.api.example;

import cn.crtlprototypestudios.ovsr.client.api.OverseerHUD;
import cn.crtlprototypestudios.ovsr.client.api.OverseerUtility;
import cn.crtlprototypestudios.ovsr.client.impl.theme.ImGuiDarkTheme;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationHUD extends OverseerHUD.HUDElement {
    private final List<Notification> notifications = new CopyOnWriteArrayList<>();

    public NotificationHUD() {
        super("notifications", new ImGuiDarkTheme());
        removeFlags(ImGuiWindowFlags.NoInputs);
        addFlags(ImGuiWindowFlags.NoMove);
        setAlignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP)
                .setOffset(-10, 10);
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
        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.get(i);

            if (notification.isExpired()) {
                notifications.remove(notification);
                continue;
            }

            ImGui.pushID(i); // Push unique ID for this notification group
            ImGui.pushStyleVar(ImGuiStyleVar.Alpha, notification.getAlpha());

            ImGui.text(notification.getMessage());

            if (notification.hasActions()) {
                ImGui.sameLine();
                if (ImGui.button(OverseerUtility.hiddenIndexString("Accept", i))) { // Add index to button label
                    notification.accept();
                    notifications.remove(notification);
                    ImGui.popStyleVar();
                    ImGui.popID();
                    continue;
                }
                ImGui.sameLine();
                if (ImGui.button(OverseerUtility.hiddenIndexString("Deny", i))) { // Add index to button label
                    notification.deny();
                    notifications.remove(notification);
                    ImGui.popStyleVar();
                    ImGui.popID();
                    continue;
                }
            }

            ImGui.popStyleVar();
            ImGui.popID();
            ImGui.separator();
        }
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }
}
