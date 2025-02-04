package cn.crtlprototypestudios.ovsr.client.api.reactive;

import java.util.Stack;

public class ReactiveScope {
    private static final Stack<Effect> effectStack = new Stack<>();

    public static void track(Signal<?> signal) {
        if (!effectStack.isEmpty()) {
            Effect currentEffect = effectStack.peek();
            currentEffect.trackDependency(signal);
        }
    }

    static void pushEffect(Effect effect) {
        effectStack.push(effect);
    }

    static void popEffect() {
        effectStack.pop();
    }
}

