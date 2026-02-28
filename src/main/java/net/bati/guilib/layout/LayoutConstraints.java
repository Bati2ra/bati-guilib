package net.bati.guilib.layout;

/**
 * Per-widget layout configuration: size constraints, alignment and z-offset.
 */
public final class LayoutConstraints {

    public static final LayoutConstraints DEFAULT = new LayoutConstraints(
            null, null,
            -1f,
            Alignment.TOP_LEFT,
            0f, 0f,
            0
    );

    private final SizeConstraint width;
    private final SizeConstraint height;
    /**
     * Aspect ratio (width / height). When set (> 0), the height is derived from
     * the resolved width after all other constraints are applied, so the widget
     * is always proportional regardless of how the flex algorithm sized it.
     * A value of 1f = square, 16/9f = widescreen, etc.
     * Ignored when < 0 (not set).
     */
    private final float aspectRatio;
    private final Alignment alignment;
    private final float offsetX;
    private final float offsetY;
    private final int zIndex;

    private LayoutConstraints(SizeConstraint width, SizeConstraint height,
                              float aspectRatio,
                              Alignment alignment,
                              float offsetX, float offsetY,
                              int zIndex) {
        this.width       = width;
        this.height      = height;
        this.aspectRatio = aspectRatio;
        this.alignment   = alignment;
        this.offsetX     = offsetX;
        this.offsetY     = offsetY;
        this.zIndex      = zIndex;
    }

    // ─── Fluent builders ──────────────────────────────────────────────────────

    public LayoutConstraints withWidth(SizeConstraint w)  { return new LayoutConstraints(w,     height,    aspectRatio, alignment, offsetX, offsetY, zIndex); }
    public LayoutConstraints withHeight(SizeConstraint h) { return new LayoutConstraints(width, h,         aspectRatio, alignment, offsetX, offsetY, zIndex); }
    public LayoutConstraints withAlignment(Alignment a)   { return new LayoutConstraints(width, height,    aspectRatio, a,         offsetX, offsetY, zIndex); }
    public LayoutConstraints withOffsetX(float x)         { return new LayoutConstraints(width, height,    aspectRatio, alignment, x,       offsetY, zIndex); }
    public LayoutConstraints withOffsetY(float y)         { return new LayoutConstraints(width, height,    aspectRatio, alignment, offsetX, y,       zIndex); }
    public LayoutConstraints withOffset(float x, float y) { return new LayoutConstraints(width, height,    aspectRatio, alignment, x,       y,       zIndex); }
    public LayoutConstraints withZIndex(int z)             { return new LayoutConstraints(width, height,    aspectRatio, alignment, offsetX, offsetY, z); }
    /** Set aspect ratio (width/height). e.g. 1f = square, 16/9f = widescreen. */
    public LayoutConstraints withAspectRatio(float ratio)  { return new LayoutConstraints(width, height,    ratio,       alignment, offsetX, offsetY, zIndex); }
    /** Clear aspect ratio. */
    public LayoutConstraints withoutAspectRatio()          { return new LayoutConstraints(width, height,    -1f,         alignment, offsetX, offsetY, zIndex); }

    // ─── Shorthand factory helpers ─────────────────────────────────────────────

    public static LayoutConstraints fillParent() {
        return DEFAULT
                .withWidth(SizeConstraint.fillParent())
                .withHeight(SizeConstraint.fillParent());
    }

    public static LayoutConstraints fixed(float w, float h) {
        return DEFAULT
                .withWidth(SizeConstraint.fixed(w))
                .withHeight(SizeConstraint.fixed(h));
    }

    /** Square with fixed size. */
    public static LayoutConstraints square(float size) {
        return DEFAULT
                .withWidth(SizeConstraint.fixed(size))
                .withAspectRatio(1f);
    }

    // ─── Accessors ────────────────────────────────────────────────────────────

    public SizeConstraint getWidth()      { return width; }
    public SizeConstraint getHeight()     { return height; }
    public float          getAspectRatio(){ return aspectRatio; }
    public boolean        hasAspectRatio(){ return aspectRatio > 0; }
    public Alignment      getAlignment()  { return alignment; }
    public float          getOffsetX()    { return offsetX; }
    public float          getOffsetY()    { return offsetY; }
    public int            getZIndex()     { return zIndex; }

    /**
     * Resolve the border-box width. Falls back to {@code measured} if no
     * width constraint is set.
     */
    public float resolveWidth(float parentAvailable, float measured) {
        return width == null ? measured : width.resolve(parentAvailable, measured);
    }

    public float resolveHeight(float parentAvailable, float measured) {
        return height == null ? measured : height.resolve(parentAvailable, measured);
    }

    /**
     * If aspectRatio is set, derive height from the already-resolved border-box width.
     * Otherwise return {@code resolvedH} unchanged.
     */
    public float applyAspectRatio(float resolvedW, float resolvedH) {
        return hasAspectRatio() ? resolvedW / aspectRatio : resolvedH;
    }
}