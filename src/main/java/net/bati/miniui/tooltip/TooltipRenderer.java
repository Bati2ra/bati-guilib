package net.bati.miniui.tooltip;

import net.bati.miniui.widget.Widget;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

/**
 * Manages tooltip display for a screen. Owned by {@link net.bati.miniui.screen.ModernScreen}.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Track which widget is hovered and for how long</li>
 *   <li>Enforce a configurable show delay (default 400ms)</li>
 *   <li>Ask the hovered widget's {@link TooltipProvider} for the tooltip each frame</li>
 *   <li>Render the tooltip at the correct screen position</li>
 * </ul>
 *
 * <h3>Position</h3>
 * The tooltip appears below-right of the cursor by default. If it would overflow
 * the screen, it flips to the other side. Final clamping is also done inside
 * {@link Tooltip#render} as a safety net.
 */
public final class TooltipRenderer {

    // ─── Config ───────────────────────────────────────────────────────────────

    /** Milliseconds of hover before the tooltip appears. */
    private int showDelayMs = 0;

    /** Offset from the mouse cursor to the tooltip top-left corner. */
    private int cursorOffsetX = 12;
    private int cursorOffsetY = -4;
    /** Minimum scale before we give up fitting the tooltip on screen. */
    private float minScale      = 0.5f;
    /** Margin kept between the tooltip and the screen edge in pixels. */
    private int   screenMargin  = 4;

    // ─── State ────────────────────────────────────────────────────────────────

    private @Nullable Widget  trackedWidget  = null;
    private           long    hoverStartTime = 0L;
    private           boolean visible        = false;

    // ─── Fluent config ────────────────────────────────────────────────────────

    public TooltipRenderer showDelay(int ms)     { this.showDelayMs   = ms; return this; }
    public TooltipRenderer cursorOffset(int x, int y) {
        this.cursorOffsetX = x;
        this.cursorOffsetY = y;
        return this;
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    /**
     * Called every frame from {@link net.bati.miniui.screen.ModernScreen#render}
     * after hover tracking is updated.
     *
     * @param hoveredWidget the widget currently under the mouse, or {@code null}
     */
    public void update(@Nullable Widget hoveredWidget) {
        if (hoveredWidget != trackedWidget) {
            trackedWidget  = hoveredWidget;
            hoverStartTime = System.currentTimeMillis();
            visible        = false;
        }
        if (!visible && trackedWidget != null) {
            if (System.currentTimeMillis() - hoverStartTime >= showDelayMs) visible = true;
        }
    }

    // ─── Render ───────────────────────────────────────────────────────────────

    /**
     * Render the active tooltip if conditions are met. Call this at the very
     * end of the screen render so the tooltip appears above everything else.
     */
    public void render(GuiGraphics gfx, float mouseX, float mouseY,
                       int screenW, int screenH) {
        if (!visible || trackedWidget == null) return;

        TooltipProvider provider = trackedWidget.getTooltipProvider();
        if (provider == null) return;

        Tooltip tooltip = provider.get(mouseX, mouseY);
        if (tooltip == null) return;

        // Usable screen area (minus margin on all sides)
        int usableW = screenW - screenMargin * 2;
        int usableH = screenH - screenMargin * 2;

        // Content width = natural max width, capped to usable screen width
        int contentW = Math.min(tooltip.getMaxWidth(), usableW);

        // Measure at this content width
        int[] size   = tooltip.measure(contentW);
        int   outerW = size[0];
        int   outerH = size[1];

        // ── Resolve scale ─────────────────────────────────────────────────────
        // Only scale down if the tooltip is larger than the usable area at
        // pivot (1,1) — i.e. it can't fit anywhere on screen at 1:1.
        float scale = 1f;
        if (outerW > usableW || outerH > usableH) {
            float scaleX = (float) usableW / outerW;
            float scaleY = (float) usableH / outerH;
            scale = Math.max(minScale, Math.min(scaleX, scaleY));

            // Recompute logical size after scale — entries still measure in
            // logical pixels, but we need to know how much screen space they use.
            outerW = (int) (outerW * scale);
            outerH = (int) (outerH * scale);
        }

        // ── Resolve pivot ─────────────────────────────────────────────────────
        // Candidate top-left at pivot (0,0): below-right of cursor.
        float anchorX = mouseX + cursorOffsetX;
        float anchorY = mouseY + cursorOffsetY;

        // How far would the right/bottom edge overflow the usable area?
        // overflowX > 0 means the tooltip sticks out to the right.
        float overflowX = (anchorX + outerW) - (screenW - screenMargin);
        float overflowY = (anchorY + outerH) - (screenH - screenMargin);

        // pivotX = how much of outerW we shift left to compensate overflow.
        // Clamped to [0,1] — 0 = tooltip fully right of anchor, 1 = fully left.
        float pivotX = overflowX > 0 ? Math.min(1f, overflowX / outerW) : 0f;
        float pivotY = overflowY > 0 ? Math.min(1f, overflowY / outerH) : 0f;

        int rx = Math.round(anchorX - outerW * pivotX);
        int ry = Math.round(anchorY - outerH * pivotY);

        // Final clamp to margin — safety net, should rarely trigger
        rx = Math.max(screenMargin, Math.min(rx, screenW - screenMargin - outerW));
        ry = Math.max(screenMargin, Math.min(ry, screenH - screenMargin - outerH));

        tooltip.render(gfx, rx, ry, contentW, scale);
    }

    /** Force-hide the tooltip — e.g. when the screen loses focus. */
    public void hide() {
        trackedWidget  = null;
        visible        = false;
    }
}