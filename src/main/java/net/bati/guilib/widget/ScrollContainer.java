package net.bati.guilib.widget;

import net.bati.guilib.layout.LayoutPassInfo;
import net.bati.guilib.layout.MeasureResult;
import net.bati.guilib.rendering.RenderPassInfo;
import net.minecraft.client.gui.GuiGraphics;

/**
 * A container that scrolls its single content child when it overflows.
 * Place any widget (e.g. a FlexContainer column) as the sole child.
 */
public class ScrollContainer extends Widget {

    private float scrollOffset = 0f;
    private float maxScroll    = 0f;
    private float scrollSpeed  = 20f;
    private boolean showScrollbar = true;

    // Scrollbar appearance
    private int scrollbarColor     = 0xFF555555;
    private int scrollbarThumbColor= 0xFFAAAAAA;
    private int scrollbarWidth     = 6;

    public ScrollContainer(String id) {
        super(id);
    }

    public ScrollContainer add(Widget content) {
        clearChildren();
        addChild(content);
        return this;
    }

    public ScrollContainer setScrollSpeed(float s)    { this.scrollSpeed   = s; return this; }
    public ScrollContainer showScrollbar(boolean show){ this.showScrollbar  = show; return this; }

    public float getScrollOffset() { return scrollOffset; }

    @Override
    protected MeasureResult measureContent(float aw, float ah) {
        // ScrollContainer's natural size is 0×0 — it is always sized by its
        // parent via LayoutConstraints or flexGrow. Returning `ah` here would
        // inflate the flex measure pass so parent thinks this widget already
        // consumes all vertical space, leaving no room for siblings (e.g. footer).
        return MeasureResult.ZERO;
    }

    @Override
    protected void layoutChildren(LayoutPassInfo childPass) {
        if (children.isEmpty()) return;

        Widget content = children.get(0);

        // Reserve scrollbar column when it is enabled (always, to keep layout stable).
        // This prevents cards from expanding into the scrollbar area.
        float contentW = showScrollbar
                ? childPass.availableWidth - scrollbarWidth
                : childPass.availableWidth;
        float viewportH = childPass.availableHeight;

        // Measure content with unlimited height so we can detect overflow.
        // Use a large-but-finite value so fillParent constraints don't go infinite.
        float unlimitedH = viewportH * 10_000f;
        MeasureResult nat = content.measure(contentW, unlimitedH);

        // Resolve final content size.
        // Width: always fill the available content width (scroll is horizontal-fixed).
        // Height: use natural height for scrolling; cap fillParent to avoid infinity.
        float resolvedW = contentW;
        float resolvedH = content.getConstraints().resolveHeight(unlimitedH, nat.height());
        if (resolvedH >= unlimitedH) resolvedH = nat.height();

        maxScroll = Math.max(0, resolvedH - viewportH);
        scrollOffset = Math.min(scrollOffset, maxScroll);

        // Layout content; offset the Y origin to implement scrolling
        LayoutPassInfo scrolledPass = new LayoutPassInfo(
                contentW, viewportH,
                childPass.contentScreenX,
                childPass.contentScreenY - scrollOffset * childPass.scale,
                childPass.scale, childPass.opacity, childPass.zIndex
        );
        content.layoutAbsolute(scrolledPass, resolvedW, resolvedH, 0f, 0f);
    }

    @Override
    protected void renderChildren(RenderPassInfo rp) {
        if (children.isEmpty()) return;
        if (computedLayout == null) return;

        var bounds = computedLayout.getPaddingBounds();
        var gfx = rp.graphics;

        // Clip content to the viewport (exclude scrollbar column)
        float sbW = showScrollbar ? scrollbarWidth : 0;
        gfx.enableScissor(
                (int) bounds.getX(),
                (int) bounds.getY(),
                (int)(bounds.getRight() - sbW),
                (int) bounds.getBottom()
        );

        super.renderChildren(rp);

        gfx.disableScissor();

        if (showScrollbar && maxScroll > 0) {
            renderScrollbar(rp);
        }
    }

    private void renderScrollbar(RenderPassInfo rp) {
        if (computedLayout == null) return;
        var bounds = computedLayout.getPaddingBounds();
        GuiGraphics gfx = rp.graphics;

        int bx = (int)(bounds.getRight() - scrollbarWidth);
        int by = (int)bounds.getY();
        int bh = (int)bounds.getHeight();

        // Track
        gfx.fill(bx, by, bx + scrollbarWidth, by + bh, scrollbarColor);

        // Thumb
        float viewRatio   = bh / (bh + maxScroll);
        int thumbH        = Math.max(16, (int)(bh * viewRatio));
        float scrollRatio = maxScroll > 0 ? scrollOffset / maxScroll : 0;
        int thumbY        = by + (int)((bh - thumbH) * scrollRatio);

        gfx.fill(bx, thumbY, bx + scrollbarWidth, thumbY + thumbH, scrollbarThumbColor);
    }

    /**
     * Only propagate pointer events to children if the point is inside the
     * visible viewport. This prevents widgets that are scrolled out of view
     * (clipped by the scissor) from stealing clicks from widgets drawn on top.
     */
    private boolean isInViewport(double mx, double my) {
        if (computedLayout == null) return false;
        // Use paddingBounds minus the scrollbar column — matches the scissor region
        var b = computedLayout.getPaddingBounds();
        float sbW = showScrollbar ? scrollbarWidth : 0;
        return mx >= b.getX() && mx <= b.getRight() - sbW
                && my >= b.getY() && my <= b.getBottom();
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (!visible || !enabled || computedLayout == null) return false;
        if (!isInViewport(mx, my)) return false;
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (!visible || !enabled || computedLayout == null) return false;
        if (!isInViewport(mx, my)) return false;
        return super.mouseReleased(mx, my, button);
    }


    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (!visible || !enabled) return false;
        if (computedLayout != null && !isInViewport(mx, my)) return false;
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double amount) {
        if (!visible || computedLayout == null) return false;
        if (!isInViewport(mx, my)) return false;

        // Give children a chance to consume the scroll first
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseScrolled(mx, my, amount)) return true;
        }

        scrollOffset -= (float) amount * scrollSpeed;
        scrollOffset  = Math.max(0, Math.min(maxScroll, scrollOffset));
        invalidateLayout();
        return true;
    }
}