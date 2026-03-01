package net.bati.miniui.layout;

/**
 * CSS-like box model: margin > border > padding > content
 */
public final class BoxModel {
    public static final BoxModel EMPTY = new BoxModel(
            EdgeInsets.ZERO, EdgeInsets.ZERO, EdgeInsets.ZERO, 0f, 0f
    );

    private final EdgeInsets margin;
    private final EdgeInsets border;
    private final EdgeInsets padding;
    private final float contentWidth;
    private final float contentHeight;

    private BoxModel(EdgeInsets margin, EdgeInsets border, EdgeInsets padding,
                     float contentWidth, float contentHeight) {
        this.margin  = margin;
        this.border  = border;
        this.padding = padding;
        this.contentWidth  = contentWidth;
        this.contentHeight = contentHeight;
    }

    // ─── Fluent builders ──────────────────────────────────────────────────────

    public static BoxModel of(EdgeInsets margin, EdgeInsets border, EdgeInsets padding) {
        return new BoxModel(margin, border, padding, 0f, 0f);
    }

    public BoxModel withContentWidth(float w)  { return new BoxModel(margin, border, padding, w, contentHeight); }
    public BoxModel withContentHeight(float h) { return new BoxModel(margin, border, padding, contentWidth, h); }
    public BoxModel withMargin(EdgeInsets m)   { return new BoxModel(m, border, padding, contentWidth, contentHeight); }
    public BoxModel withBorder(EdgeInsets b)   { return new BoxModel(margin, b, padding, contentWidth, contentHeight); }
    public BoxModel withPadding(EdgeInsets p)  { return new BoxModel(margin, border, p, contentWidth, contentHeight); }

    // ─── Accessors ────────────────────────────────────────────────────────────

    public EdgeInsets getMargin()  { return margin; }
    public EdgeInsets getBorder()  { return border; }
    public EdgeInsets getPadding() { return padding; }

    public float getContentWidth()  { return contentWidth; }
    public float getContentHeight() { return contentHeight; }

    // border-box (content + padding + border)
    public float getBorderBoxWidth()  { return contentWidth  + padding.horizontal() + border.horizontal(); }
    public float getBorderBoxHeight() { return contentHeight + padding.vertical()   + border.vertical(); }

    // total (border-box + margin)
    public float getTotalWidth()  { return getBorderBoxWidth()  + margin.horizontal(); }
    public float getTotalHeight() { return getBorderBoxHeight() + margin.vertical(); }
}