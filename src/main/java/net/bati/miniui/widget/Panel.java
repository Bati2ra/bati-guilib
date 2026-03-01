package net.bati.miniui.widget;

import net.bati.miniui.layout.MeasureResult;

/**
 * A simple container that positions children using their own
 * {@link net.bati.miniui.layout.LayoutConstraints} (alignment + offsets).
 * Does not apply any flex algorithm.
 */
public class Panel extends Widget {

    private float minWidth  = 0f;
    private float minHeight = 0f;

    public Panel(String id) { super(id); }

    public Panel setMinSize(float w, float h) { minWidth = w; minHeight = h; invalidateLayout(); return this; }

    public Panel add(Widget child) { addChild(child); return this; }

    @Override
    protected MeasureResult measureContent(float availableWidth, float availableHeight) {
        // Natural size = bounding box of all children
        float maxW = minWidth, maxH = minHeight;
        for (Widget child : getChildren()) {
            if (!child.isVisible()) continue;
            MeasureResult m = child.measure(availableWidth, availableHeight);
            maxW = Math.max(maxW, m.width()  + child.getBoxModel().getMargin().horizontal());
            maxH = Math.max(maxH, m.height() + child.getBoxModel().getMargin().vertical());
        }
        return new MeasureResult(maxW, maxH);
    }
    // layoutChildren is inherited from Widget (each child lays itself out with alignment)
}


