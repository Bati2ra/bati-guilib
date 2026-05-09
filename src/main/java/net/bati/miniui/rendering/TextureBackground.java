package net.bati.miniui.rendering;

import net.bati.miniui.layout.ComputedLayout;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/**
 * Renders a texture (or a region of a texture atlas) as a widget background.
 *
 * <h3>Parameters explained</h3>
 * <pre>
 *   texture          — the texture file on disk
 *   texW / texH      — total texture size in pixels (used to normalize UVs internally)
 *   srcU / srcV      — top-left corner of the region to sample, in texture pixels
 *   srcW / srcH      — size of the region to sample, in texture pixels
 *
 * Example — full texture (default):
 *   texW=256, texH=256, srcU=0, srcV=0, srcW=256, srcH=256
 *
 * Example — icon at offset (32,0) of size 18×18 inside a 256×256 atlas:
 *   texW=256, texH=256, srcU=32, srcV=0, srcW=18, srcH=18
 * </pre>
 */
public class TextureBackground implements BackgroundRenderer {

    private final Identifier texture;

    // Total texture dimensions — used to normalize UVs
    private final int texW;
    private final int texH;

    // Region to sample within the texture
    private final float srcU;
    private final float srcV;
    private final int   srcW;
    private final int   srcH;

    // Optional tint — -1 = no tint (white / full color)
    private final int tint;

    // ─── Constructor ──────────────────────────────────────────────────────────

    private TextureBackground(Identifier texture,
                              int texW, int texH,
                              float srcU, float srcV,
                              int srcW, int srcH,
                              int tint) {
        this.texture = texture;
        this.texW    = texW;
        this.texH    = texH;
        this.srcU    = srcU;
        this.srcV    = srcV;
        this.srcW    = srcW;
        this.srcH    = srcH;
        this.tint    = tint;
    }

    // ─── Factories ────────────────────────────────────────────────────────────

    /**
     * Use the full texture, no tint.
     * Equivalent to the original constructor behavior.
     */
    public static TextureBackground of(Identifier texture, int texW, int texH) {
        return new TextureBackground(texture, texW, texH, 0, 0, texW, texH, -1);
    }

    /**
     * Sample a specific region of a texture atlas.
     *
     * @param texture  the texture file
     * @param texW     total atlas width in pixels
     * @param texH     total atlas height in pixels
     * @param srcU     left edge of the region in texture pixels
     * @param srcV     top edge of the region in texture pixels
     * @param srcW     width of the region in texture pixels
     * @param srcH     height of the region in texture pixels
     */
    public static TextureBackground region(Identifier texture,
                                           int texW, int texH,
                                           float srcU, float srcV,
                                           int srcW, int srcH) {
        return new TextureBackground(texture, texW, texH, srcU, srcV, srcW, srcH, -1);
    }

    // ─── Builder-style modifiers ──────────────────────────────────────────────

    /** Return a copy with the given tint color (ARGB). Use -1 for no tint. */
    public TextureBackground withTint(int argb) {
        return new TextureBackground(texture, texW, texH, srcU, srcV, srcW, srcH, argb);
    }

    /** Return a copy sampling from a different UV origin. */
    public TextureBackground withUV(float u, float v) {
        return new TextureBackground(texture, texW, texH, u, v, srcW, srcH, tint);
    }

    /** Return a copy with a different source region size. */
    public TextureBackground withSrcSize(int w, int h) {
        return new TextureBackground(texture, texW, texH, srcU, srcV, w, h, tint);
    }

    // ─── Render ───────────────────────────────────────────────────────────────

    @Override
    public void render(GuiGraphics gfx, ComputedLayout layout) {
        if (texture == null) return;

        ComputedLayout.Bounds b = layout.getBorderBounds();

        gfx.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                (int) b.getX(),      // dst X on screen
                (int) b.getY(),      // dst Y on screen
                srcU,                // src U (texture pixel offset)
                srcV,                // src V (texture pixel offset)
                (int) b.getWidth(),  // dst width on screen
                (int) b.getHeight(), // dst height on screen
                srcW,                // src region width in texture pixels
                srcH,                // src region height in texture pixels
                texW,                // full texture width (for UV normalization)
                texH,                // full texture height (for UV normalization)
                tint                 // -1 = no tint
        );
    }
}