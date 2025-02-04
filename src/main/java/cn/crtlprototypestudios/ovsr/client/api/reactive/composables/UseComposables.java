package cn.crtlprototypestudios.ovsr.client.api.reactive.composables;

import cn.crtlprototypestudios.ovsr.client.api.reactive.Ref;

public class UseComposables {
    public static class UseToggle {
        private final Ref<Boolean> value;

        public UseToggle(boolean initial) {
            this.value = new Ref<>(initial);
        }

        public void toggle() {
            value.set(!value.get());
        }

        public void setTrue() {
            value.set(true);
        }

        public void setFalse() {
            value.set(false);
        }

        public Ref<Boolean> getValue() {
            return value;
        }
    }

    public static class UseCounter {
        private final Ref<Integer> count;

        public UseCounter(int initial) {
            this.count = new Ref<>(initial);
        }

        public void increment() {
            count.set(count.get() + 1);
        }

        public void decrement() {
            count.set(count.get() - 1);
        }

        public void reset() {
            count.set(0);
        }

        public Ref<Integer> getCount() {
            return count;
        }
    }

    public static class UseTimeout {
        private final Ref<Boolean> pending;
        private long timeoutId = -1;

        public UseTimeout() {
            this.pending = new Ref<>(false);
        }

        public void start(Runnable callback, long delay) {
            clear();
            pending.set(true);
            timeoutId = System.currentTimeMillis();

            new Thread(() -> {
                try {
                    Thread.sleep(delay);
                    if (timeoutId == System.currentTimeMillis()) {
                        callback.run();
                        pending.set(false);
                    }
                } catch (InterruptedException e) {
                    // Ignored
                }
            }).start();
        }

        public void clear() {
            timeoutId = -1;
            pending.set(false);
        }

        public Ref<Boolean> isPending() {
            return pending;
        }
    }
}
