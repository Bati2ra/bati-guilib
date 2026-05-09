package net.bati.miniui.tooltip;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Renders the decorative border around a tooltip.
 *
 * <p>Supports a vertical gradient — top color blends into bottom color — which
 * matches Minecraft's vanilla tooltip style. Both colors can be the same for a
 * flat border.
 *
 * <pre>
 * // Vanilla-style purple gradient border
 * TooltipBorder.gradient(0xFF5000FF, 0xFF28007F)
 *
 * // Flat single-color border
 * TooltipBorder.flat(0xFF888888)
 *
 * // No border
 * TooltipBorder.none()
 * </pre>
 */
public final class TooltipBorder {

    private static final int THICKNESS = 1;

    private final boolean visible;
    private final int     colorTop;
    private final int     colorBottom;

    private TooltipBorder(boolean visible, int colorTop, int colorBottom) {
        this.visible     = visible;
        this.colorTop    = colorTop;
        this.colorBottom = colorBottom;
    }

    // ─── Factories ────────────────────────────────────────────────────────────

    public static TooltipBorder none() {
        return new TooltipBorder(false, 0, 0);
    }

    /** Single color on all sides. */
    public static TooltipBorder flat(int color) {
        return new TooltipBorder(true, color, color);
    }

    /**
     * Vertical gradient — top edge uses {@code colorTop}, bottom edge uses
     * {@code colorBottom}. Left and right edges blend between the two.
     * This matches Minecraft's vanilla tooltip border style.
     */
    public static TooltipBorder gradient(int colorTop, int colorBottom) {
        return new TooltipBorder(true, colorTop, colorBottom);
    }

    /** Vanilla Minecraft tooltip border — purple gradient. */
    public static TooltipBorder vanilla() {
        return gradient(0xFF5000FF, 0xFF28007F);
    }

    // ─── Render ───────────────────────────────────────────────────────────────

    /**
     * Draw the border around the given bounds.
     * {@code x}/{@code y} are the outer top-left corner (including the border itself).
     */
    public void render(GuiGraphics gfx, int x, int y, int w, int h) {
        if (!visible) return;

        int x2 = x + w;
        int y2 = y + h;

        // Top edge — colorTop
        gfx.fill(x, y,  x2, y  + THICKNESS, colorTop);
        // Bottom edge — colorBottom
        gfx.fill(x, y2 - THICKNESS, x2, y2, colorBottom);
        // Left edge — gradient top→bottom
        fillGradient(gfx, x, y + THICKNESS, x + THICKNESS, y2 - THICKNESS, colorTop, colorBottom);
        // Right edge — gradient top→bottom
        fillGradient(gfx, x2 - THICKNESS, y + THICKNESS, x2, y2 - THICKNESS, colorTop, colorBottom);
    }

    /** Thickness in pixels. Useful for callers computing inner padding. */
    public int getThickness() { return visible ? THICKNESS : 0; }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private static void fillGradient(GuiGraphics gfx,
                                     int x1, int y1, int x2, int y2,
                                     int colorTop, int colorBottom) {
        // GuiGraphics.fillGradient is available in 1.21 Fabric
        gfx.fillGradient(x1, y1, x2, y2, colorTop, colorBottom);
    }
}