package net.bati.guilib.rendering;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/**
 * Renders a texture using nine-slice scaling so corners/borders aren't distorted.
 */
public final class NineSliceRenderer {

    private final Identifier texture;
    private final int textureWidth;
    private final int textureHeight;

    // Source UV regions
    private final int sliceTop;
    private final int sliceRight;
    private final int sliceBottom;
    private final int sliceLeft;

    public NineSliceRenderer(Identifier texture,
                             int textureWidth, int textureHeight,
                             int sliceTop, int sliceRight,
                             int sliceBottom, int sliceLeft) {
        this.texture       = texture;
        this.textureWidth  = textureWidth;
        this.textureHeight = textureHeight;
        this.sliceTop      = sliceTop;
        this.sliceRight    = sliceRight;
        this.sliceBottom   = sliceBottom;
        this.sliceLeft     = sliceLeft;
    }

    /** Uniform slices on all four sides. */
    public NineSliceRenderer(Identifier texture, int texW, int texH, int slice) {
        this(texture, texW, texH, slice, slice, slice, slice);
    }

    public void render(GuiGraphics gfx, float x, float y, float w, float h) {
        int ix = (int) x, iy = (int) y, iw = (int) w, ih = (int) h;

        int centerW = Math.max(0, iw - sliceLeft - sliceRight);
        int centerH = Math.max(0, ih - sliceTop  - sliceBottom);

        int srcCenterW = textureWidth  - sliceLeft - sliceRight;
        int srcCenterH = textureHeight - sliceTop  - sliceBottom;

        // Top-left
        blit(gfx, ix,                    iy,                    sliceLeft,   sliceTop,    0,               0,               sliceLeft,  sliceTop);
        // Top-right
        blit(gfx, ix + sliceLeft + centerW, iy,                 sliceRight,  sliceTop,    textureWidth - sliceRight, 0,    sliceRight, sliceTop);
        // Bottom-left
        blit(gfx, ix,                    iy + sliceTop + centerH, sliceLeft, sliceBottom, 0,               textureHeight - sliceBottom, sliceLeft, sliceBottom);
        // Bottom-right
        blit(gfx, ix + sliceLeft + centerW, iy + sliceTop + centerH, sliceRight, sliceBottom,
                textureWidth - sliceRight, textureHeight - sliceBottom, sliceRight, sliceBottom);

        // Top center
        if (centerW > 0) blit(gfx, ix + sliceLeft, iy, centerW, sliceTop, sliceLeft, 0, srcCenterW, sliceTop);
        // Bottom center
        if (centerW > 0) blit(gfx, ix + sliceLeft, iy + sliceTop + centerH, centerW, sliceBottom,
                sliceLeft, textureHeight - sliceBottom, srcCenterW, sliceBottom);
        // Middle left
        if (centerH > 0) blit(gfx, ix, iy + sliceTop, sliceLeft, centerH, 0, sliceTop, sliceLeft, srcCenterH);
        // Middle right
        if (centerH > 0) blit(gfx, ix + sliceLeft + centerW, iy + sliceTop, sliceRight, centerH,
                textureWidth - sliceRight, sliceTop, sliceRight, srcCenterH);
        // Center
        if (centerW > 0 && centerH > 0)
            blit(gfx, ix + sliceLeft, iy + sliceTop, centerW, centerH, sliceLeft, sliceTop, srcCenterW, srcCenterH);
    }

    private void blit(GuiGraphics gfx,
                      int dstX, int dstY, int dstW, int dstH,
                      int srcX, int srcY, int srcW, int srcH) {
        if (dstW <= 0 || dstH <= 0) return;
        gfx.blit(RenderPipelines.GUI_TEXTURED,
                texture,
                dstX, dstY,
                (float) srcX, (float) srcY,
                dstW, dstH,
                srcW, srcH,
                textureWidth, textureHeight,
                -1);
    }
}