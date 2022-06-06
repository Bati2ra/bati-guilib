package net.bati.guilib.gui.interfaces;
@FunctionalInterface
public interface PressableCallback {
    abstract void call(int keyCode, int scanCode, int modifiers);
}
