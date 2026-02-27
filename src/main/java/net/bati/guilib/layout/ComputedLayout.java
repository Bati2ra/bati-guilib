package net.bati.guilib.layout;

import lombok.Builder;
import lombok.Getter;
import lombok.With;
import net.bati.guilib.utils.Vec2;

/**
 * Immutable result of a single layout pass for one widget.
 *
 * All coordinates are in SCREEN SPACE (absolute pixels).
 * Calculated once per layout pass; cached until invalidated.
 *
 * Content/padding/border bounds are pre-calculated here so
 * getContentBounds() is O(1) during render.
 */
@Getter
@Builder
public class ComputedLayout {

    // --- Position (screen space) ---
    private final float screenX;
    private final float screenY;

    // --- Size (border-box: content + padding + border) ---
    private final float width;
    private final float height;

    // --- Pre-calculated inner bounds (screen space) ---
    // Avoids repeated arithmetic during render
    private final Bounds borderBounds;   // screenX + margin offset
    private final Bounds paddingBounds;  // inside border
    private final Bounds contentBounds;  // inside padding

    // --- Transformations (inherited from parent chain) ---
    @Builder.Default private final float scale   = 1.0f;
    @Builder.Default private final float opacity = 1.0f;

    // --- Stacking ---
    @Builder.Default private final int computedZIndex = 0;

    /** Hit-test: is the screen-space point inside this widget's border-box? */
    public boolean contains(float x, float y) {
        return x >= screenX && x <= screenX + width
                && y >= screenY && y <= screenY + height;
    }

    // ----------------------------------------------------------------
    //  Factory — called by Widget.computeLayout()
    // ----------------------------------------------------------------

    /**
     * Build a ComputedLayout given final screen position, size, and the
     * inherited transformations from the parent chain.
     */
    public static ComputedLayout create(
            float screenX, float screenY,
            float width, float height,
            BoxModel boxModel,
            float inheritedScale,
            float inheritedOpacity,
            int   computedZIndex
    ) {
        float s = inheritedScale;

        // border-box starts outside margin
        float bx = screenX + boxModel.getMargin().getLeft() * s;
        float by = screenY + boxModel.getMargin().getTop()  * s;
        float bw = boxModel.getBorderBoxWidth()  * s;
        float bh = boxModel.getBorderBoxHeight() * s;

        // padding-box starts inside border
        float px = bx + boxModel.getBorder().getLeft() * s;
        float py = by + boxModel.getBorder().getTop()  * s;
        float pw = (boxModel.getBorderBoxWidth()  - boxModel.getBorder().horizontal()) * s;
        float ph = (boxModel.getBorderBoxHeight() - boxModel.getBorder().vertical())   * s;

        // content-box starts inside padding
        float cx = px + boxModel.getPadding().getLeft() * s;
        float cy = py + boxModel.getPadding().getTop()  * s;
        float cw = boxModel.getContentWidth()  * s;
        float ch = boxModel.getContentHeight() * s;

        return ComputedLayout.builder()
                .screenX(screenX)
                .screenY(screenY)
                .width(width * s)
                .height(height * s)
                .borderBounds (new Bounds(bx, by, bw, bh))
                .paddingBounds(new Bounds(px, py, pw, ph))
                .contentBounds(new Bounds(cx, cy, cw, ch))
                .scale(inheritedScale)
                .opacity(inheritedOpacity)
                .computedZIndex(computedZIndex)
                .build();
    }

    // ----------------------------------------------------------------

    @Getter
    public static final class Bounds {
        private final float x, y, width, height;

        public Bounds(float x, float y, float width, float height) {
            this.x = x; this.y = y;
            this.width = width; this.height = height;
        }

        public boolean contains(float px, float py) {
            return px >= x && px <= x + width && py >= y && py <= y + height;
        }

        public boolean intersects(Bounds o) {
            return !(o.x > x + width || o.x + o.width < x
                    || o.y > y + height || o.y + o.height < y);
        }

        public float right()  { return x + width;  }
        public float bottom() { return y + height; }
        public float centerX(){ return x + width  / 2; }
        public float centerY(){ return y + height / 2; }
    }
}