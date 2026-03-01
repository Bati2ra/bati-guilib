package net.bati.miniui.layout;

/**
 * Immutable layout result for one widget. Holds absolute screen-space positions
 * for all box model layers, pre-calculated for O(1) access during render.
 */
public final class ComputedLayout {

    // Absolute screen position (top-left of the full box including margin)
    private final float screenX, screenY;
    // Border-box size in screen pixels (already scaled)
    private final float scaledWidth, scaledHeight;

    // Pre-calculated layer bounds (already in screen space)
    private final Bounds borderBounds;   // inside margin
    private final Bounds paddingBounds;  // inside border
    private final Bounds contentBounds;  // inside padding

    private final float scale;
    private final float opacity;
    private final int   computedZIndex;

    private ComputedLayout(float screenX, float screenY,
                           float scaledWidth, float scaledHeight,
                           Bounds borderBounds, Bounds paddingBounds, Bounds contentBounds,
                           float scale, float opacity, int computedZIndex) {
        this.screenX       = screenX;
        this.screenY       = screenY;
        this.scaledWidth   = scaledWidth;
        this.scaledHeight  = scaledHeight;
        this.borderBounds  = borderBounds;
        this.paddingBounds = paddingBounds;
        this.contentBounds = contentBounds;
        this.scale         = scale;
        this.opacity       = opacity;
        this.computedZIndex = computedZIndex;
    }

    /**
     * Build a ComputedLayout by applying the box model layers.
     *
     * @param screenX      absolute screen X (including margin)
     * @param screenY      absolute screen Y (including margin)
     * @param borderBoxW   unscaled border-box width
     * @param borderBoxH   unscaled border-box height
     * @param boxModel     the widget's box model
     * @param scale        accumulated scale
     * @param opacity      accumulated opacity
     * @param zIndex       final z-index
     */
    public static ComputedLayout create(float screenX, float screenY,
                                        float borderBoxW, float borderBoxH,
                                        BoxModel boxModel,
                                        float scale, float opacity, int zIndex) {
        EdgeInsets margin  = boxModel.getMargin();
        EdgeInsets border  = boxModel.getBorder();
        EdgeInsets padding = boxModel.getPadding();

        float bx = screenX + margin.getLeft()  * scale;
        float by = screenY + margin.getTop()   * scale;
        float bw = borderBoxW * scale;
        float bh = borderBoxH * scale;

        float px = bx + border.getLeft()  * scale;
        float py = by + border.getTop()   * scale;
        float pw = bw - border.horizontal() * scale;
        float ph = bh - border.vertical()   * scale;

        float cx = px + padding.getLeft()  * scale;
        float cy = py + padding.getTop()   * scale;
        float cw = pw - padding.horizontal() * scale;
        float ch = ph - padding.vertical()   * scale;

        return new ComputedLayout(
                screenX, screenY,
                bw, bh,
                new Bounds(bx, by, bw, bh),
                new Bounds(px, py, pw, ph),
                new Bounds(cx, cy, cw, ch),
                scale, opacity, zIndex
        );
    }

    // ─── Accessors ────────────────────────────────────────────────────────────

    public float  getScreenX()        { return screenX; }
    public float  getScreenY()        { return screenY; }
    public float  getScaledWidth()    { return scaledWidth; }
    public float  getScaledHeight()   { return scaledHeight; }
    public Bounds getBorderBounds()   { return borderBounds; }
    public Bounds getPaddingBounds()  { return paddingBounds; }
    public Bounds getContentBounds()  { return contentBounds; }
    public float  getScale()          { return scale; }
    public float  getOpacity()        { return opacity; }
    public int    getComputedZIndex() { return computedZIndex; }

    public boolean containsPoint(float px, float py) {
        return borderBounds.contains(px, py);
    }

    // ─── Bounds ───────────────────────────────────────────────────────────────

    public static final class Bounds {
        private final float x, y, width, height;

        public Bounds(float x, float y, float width, float height) {
            this.x = x; this.y = y; this.width = width; this.height = height;
        }

        public float getX()      { return x; }
        public float getY()      { return y; }
        public float getWidth()  { return width; }
        public float getHeight() { return height; }
        public float getRight()  { return x + width; }
        public float getBottom() { return y + height; }

        public boolean contains(float px, float py) {
            return px >= x && px <= x + width && py >= y && py <= y + height;
        }
    }
}