package net.bati.miniui.rendering;

import net.bati.miniui.layout.ComputedLayout;
import net.minecraft.client.gui.GuiGraphics;

public interface BackgroundRenderer {
    void render(GuiGraphics gfx, ComputedLayout layout);
}