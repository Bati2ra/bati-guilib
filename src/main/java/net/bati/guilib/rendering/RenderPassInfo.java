package net.bati.guilib.rendering;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Context passed during the render pass. Avoids long parameter lists.
 */
public final class RenderPassInfo {
    public final GuiGraphics graphics;
    public final float mouseX;
    public final float mouseY;
    public final float delta;

    public RenderPassInfo(GuiGraphics graphics, float mouseX, float mouseY, float delta) {
        this.graphics = graphics;
        this.mouseX   = mouseX;
        this.mouseY   = mouseY;
        this.delta    = delta;
    }
}