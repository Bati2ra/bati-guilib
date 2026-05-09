package net.bati.miniui.rendering;

import net.bati.miniui.layout.ComputedLayout;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public class NineSliceBackground implements BackgroundRenderer {

    private final Identifier texture;
    private final int texW;
    private final int texH;

    private final int sliceTop;
    private final int sliceRight;
    private final int sliceBottom;
    private final int sliceLeft;

    private final NineSliceRenderer renderer;

    public NineSliceBackground(
            Identifier texture,
            int texW,
            int texH,
            int sliceTop,
            int sliceRight,
            int sliceBottom,
            int sliceLeft
    ) {
        this.texture = texture;
        this.texW = texW;
        this.texH = texH;

        this.sliceTop = sliceTop;
        this.sliceRight = sliceRight;
        this.sliceBottom = sliceBottom;
        this.sliceLeft = sliceLeft;

        this.renderer = new NineSliceRenderer(
                texture,
                texW,
                texH,
                sliceTop,
                sliceRight,
                sliceBottom,
                sliceLeft
        );
    }

    @Override
    public void render(GuiGraphics gfx, ComputedLayout layout) {
        if (texture == null) return;

        ComputedLayout.Bounds b = layout.getBorderBounds();

        float x = b.getX();
        float y = b.getY();
        float w = b.getWidth();
        float h = b.getHeight();

        renderer.render(gfx, x, y, w, h);
    }
}