package net.bati.guilib.widget;

import net.bati.guilib.layout.MeasureResult;
import net.bati.guilib.rendering.RenderPassInfo;

/** A thin separator line (horizontal or vertical). */
public class Separator extends Widget {

    private final boolean horizontal;
    private final int     thickness;
    private       int     color = 0xFF555555;

    public Separator(String id, boolean horizontal, int thickness) {
        super(id);
        this.horizontal = horizontal;
        this.thickness  = thickness;
    }

    public static Separator horizontal(String id) { return new Separator(id, true, 1); }
    public static Separator vertical(String id)   { return new Separator(id, false, 1); }

    public Separator setColor(int c) { this.color = c; return this; }

    @Override
    protected MeasureResult measureContent(float aw, float ah) {
        // A separator's natural size is just its thickness × thickness.
        // fillParent/STRETCH is handled via LayoutConstraints or alignItems=STRETCH
        // at layout time — not by inflating the measured size here.
        if (horizontal) return new MeasureResult(0, thickness); // width fills via constraint
        else            return new MeasureResult(thickness, 0); // height fills via constraint
    }

    @Override
    protected void renderContent(RenderPassInfo rp) {
        if (computedLayout == null) return;
        var b = computedLayout.getBorderBounds();
        rp.graphics.fill((int)b.getX(), (int)b.getY(),
                (int)(b.getX()+b.getWidth()), (int)(b.getY()+b.getHeight()), color);
    }
}