package net.bati.guilib.gui.interfaces;

import net.minecraft.client.util.math.MatrixStack;
@FunctionalInterface
public interface DrawableCallback {
    void draw(MatrixStack matrices, int x, int y, float delta);
}
