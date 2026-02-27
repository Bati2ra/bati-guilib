package net.bati.guilib.layout;

public class LayoutContext {
    private static final ThreadLocal<Boolean> COMPUTING =
            ThreadLocal.withInitial(() -> false);

    public static void begin() {
        COMPUTING.set(true);
    }

    public static void end() {
        COMPUTING.set(false);
    }

    public static boolean isComputing() {
        return COMPUTING.get();
    }
}
