package net.bati.guilib.gui.interfaces;

import net.bati.guilib.utils.Vec2;
import net.minecraft.client.util.Window;
@FunctionalInterface
public interface ScreenPositionCallback {
    Vec2 get(Window window);
}
