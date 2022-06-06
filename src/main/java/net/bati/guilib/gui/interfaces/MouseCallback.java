package net.bati.guilib.gui.interfaces;
@FunctionalInterface
public interface MouseCallback {
    abstract void call(double x, double y, int mouseButton);
}
