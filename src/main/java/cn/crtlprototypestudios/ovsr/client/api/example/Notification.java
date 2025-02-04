package cn.crtlprototypestudios.ovsr.client.api.example;

public class Notification {
    public float getAlpha() {
        return 0.8f;
    }

    public String getMessage() {
        return "Notification";
    }

    public boolean hasActions() {
        return true;
    }

    public void accept() {
        // do something
    }

    public void deny() {
        // do something
    }

    public boolean isExpired() {
        return false;
    }
}
