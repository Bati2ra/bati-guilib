package net.bati.guilib.layout;

/**
 * Immutable context passed down during the layout pass.
 * Similar to GeckoLib's RenderPassInfo – avoids long parameter lists.
 */
public final class LayoutPassInfo {

    /** Width available for children (parent's content-box width, unscaled). */
    public final float availableWidth;
    /** Height available for children (parent's content-box height, unscaled). */
    public final float availableHeight;

    /** Absolute screen X of the parent's content-box origin. */
    public final float contentScreenX;
    /** Absolute screen Y of the parent's content-box origin. */
    public final float contentScreenY;

    /** Accumulated scale factor. */
    public final float scale;
    /** Accumulated opacity [0,1]. */
    public final float opacity;
    /** Base z-index from the parent. */
    public final int zIndex;

    public LayoutPassInfo(float availableWidth, float availableHeight,
                          float contentScreenX, float contentScreenY,
                          float scale, float opacity, int zIndex) {
        this.availableWidth  = availableWidth;
        this.availableHeight = availableHeight;
        this.contentScreenX  = contentScreenX;
        this.contentScreenY  = contentScreenY;
        this.scale           = scale;
        this.opacity         = opacity;
        this.zIndex          = zIndex;
    }

    /** Root-level pass info (full screen). */
    public static LayoutPassInfo root(float screenW, float screenH) {
        return new LayoutPassInfo(screenW, screenH, 0, 0, 1f, 1f, 0);
    }

    /** Derived pass for a child given its parent's computed layout. */
    public LayoutPassInfo deriveForChild(ComputedLayout parentLayout) {
        ComputedLayout.Bounds content = parentLayout.getContentBounds();
        return new LayoutPassInfo(
                content.getWidth() / parentLayout.getScale(),
                content.getHeight() / parentLayout.getScale(),
                content.getX(),
                content.getY(),
                parentLayout.getScale(),
                parentLayout.getOpacity(),
                parentLayout.getComputedZIndex()
        );
    }

    public LayoutPassInfo withAvailable(float w, float h) {
        return new LayoutPassInfo(w, h, contentScreenX, contentScreenY, scale, opacity, zIndex);
    }
}