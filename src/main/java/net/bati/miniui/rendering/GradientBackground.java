package net.bati.miniui.rendering;

import net.bati.miniui.layout.ComputedLayout;
import net.minecraft.client.gui.GuiGraphics;

public class GradientBackground implements BackgroundRenderer {

    private final int startColor;
    private final int endColor;
    private final GradientDirection direction;

    public GradientBackground(int startColor, int endColor, GradientDirection direction) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.direction = direction;
    }

    @Override
    public void render(GuiGraphics gfx, ComputedLayout layout) {
        ComputedLayout.Bounds b = layout.getBorderBounds();

        float x = b.getX();
        float y = b.getY();
        float w = b.getWidth();
        float h = b.getHeight();

        if (direction == GradientDirection.VERTICAL) {
            gfx.fillGradient(
                    (int)x,
                    (int)y,
                    (int)(x + w),
                    (int)(y + h),
                    startColor,
                    endColor
            );
        }
    }

    public enum GradientDirection {
        VERTICAL
    }
}