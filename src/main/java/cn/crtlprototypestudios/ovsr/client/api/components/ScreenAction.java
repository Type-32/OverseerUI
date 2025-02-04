package cn.crtlprototypestudios.ovsr.client.api.components;

public class ScreenAction {
    private final Runnable action;
    private boolean executed = false;

    public ScreenAction(Runnable action) {
        this.action = action;
    }

    public void execute() {
        if (!executed) {
            action.run();
            executed = true;
        }
    }
}

