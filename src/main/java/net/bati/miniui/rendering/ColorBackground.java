package net.bati.miniui.rendering;

import net.bati.miniui.layout.ComputedLayout;
import net.minecraft.client.gui.GuiGraphics;

public class ColorBackground implements BackgroundRenderer {

    private final int color;

    public ColorBackground(int color) {
        this.color = color;
    }

    @Override
    public void render(GuiGraphics gfx, ComputedLayout layout) {
        ComputedLayout.Bounds b = layout.getBorderBounds();

        float x = b.getX();
        float y = b.getY();
        float w = b.getWidth();
        float h = b.getHeight();

        float opacity = layout.getOpacity();

        int a = (int) ((color >> 24 & 0xFF) * opacity);
        int col = (a << 24) | (color & 0x00FFFFFF);

        gfx.fill(
                (int) x,
                (int) y,
                (int) (x + w),
                (int) (y + h),
                col
        );
    }
}