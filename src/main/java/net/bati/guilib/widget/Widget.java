package net.bati.guilib.widget;

import net.bati.guilib.event.EventHandlers;
import net.bati.guilib.layout.*;
import net.bati.guilib.rendering.Background;
import net.bati.guilib.rendering.RenderPassInfo;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for every UI element.
 *
 * <p>Layout is a three-phase pipeline:
 * <ol>
 *   <li>{@link #measure(float, float)} – report natural border-box size</li>
 *   <li>{@link #layout(LayoutPassInfo)} – compute absolute screen positions</li>
 *   <li>{@link #render(RenderPassInfo)} – draw using computed positions</li>
 * </ol>
 */
public abstract class Widget {

    // ─── Identity ─────────────────────────────────────────────────────────────

    private final String id;

    // ─── State ────────────────────────────────────────────────────────────────

    protected boolean visible  = true;
    protected boolean enabled  = true;
    protected boolean hovered  = false;
    protected boolean focused  = false;

    // ─── Layout config ────────────────────────────────────────────────────────

    protected BoxModel boxModel     = BoxModel.EMPTY;
    protected LayoutConstraints constraints = LayoutConstraints.DEFAULT;
    protected FlexConstraints flex         = FlexConstraints.DEFAULT;

    // ─── Render config ────────────────────────────────────────────────────────

    protected Background background = Background.none();

    // ─── Debug ────────────────────────────────────────────────────────────────

    protected boolean debugBounds = false;

    // ─── Computed (output of layout) ──────────────────────────────────────────

    protected @Nullable ComputedLayout computedLayout;

    // ─── Hierarchy ────────────────────────────────────────────────────────────

    protected @Nullable Widget       parent;
    protected final List<Widget> children = new ArrayList<>();

    // ─── Events ───────────────────────────────────────────────────────────────

    protected final EventHandlers events = new EventHandlers();

    // ─── Dirty flag ───────────────────────────────────────────────────────────

    private boolean layoutDirty = true;

    // ─── Constructor ──────────────────────────────────────────────────────────

    protected Widget(String id) {
        this.id = id;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PHASE 1 – MEASURE
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Measure the natural content size (without padding/border/margin).
     * Available space hints may be used by containers that need to measure their
     * children proportionally (e.g. text wrapping), but they are NOT constraints.
     */
    protected abstract MeasureResult measureContent(float availableWidth, float availableHeight);

    /**
     * Public measure entry-point. Returns the natural border-box size.
     * (content + padding + border; margin is NOT included in the returned value
     *  because the parent is responsible for positioning with margin.)
     */
    public final MeasureResult measure(float availableWidth, float availableHeight) {
        MeasureResult content = measureContent(availableWidth, availableHeight);

        boxModel = boxModel
                .withContentWidth(content.width())
                .withContentHeight(content.height());

        float borderW = boxModel.getBorderBoxWidth();
        float borderH = boxModel.getBorderBoxHeight();

        // If this widget has an aspectRatio, derive height from width so the
        // parent sees the correct size during its own measure pass.
        // We need to resolve the width constraint first to know the actual width.
        float resolvedW = constraints.resolveWidth(availableWidth, borderW);
        if (constraints.hasAspectRatio()) {
            float resolvedH = constraints.applyAspectRatio(resolvedW, borderH);
            return new MeasureResult(borderW, resolvedH);
        }

        return new MeasureResult(borderW, borderH);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PHASE 2 – LAYOUT
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Layout this widget using the context from its parent.
     * Computes {@link #computedLayout} and calls {@link #layoutChildren}.
     */
    public void layout(LayoutPassInfo pass) {
        internalLayout(pass, null, null);
    }

    /**
     * Called by FlexContainer to override position (the flex algorithm already
     * computed exactly where this widget goes).
     */
    public void layoutAbsolute(LayoutPassInfo pass,
                               float assignedW, float assignedH,
                               float localX,   float localY) {
        internalLayout(pass, new float[]{assignedW, assignedH}, new float[]{localX, localY});
    }

    private void internalLayout(LayoutPassInfo pass,
                                float @Nullable [] assignedSize,
                                float @Nullable [] forcedLocal) {
        if (!visible) { computedLayout = null; return; }

        // ── Measure ───────────────────────────────────────────────────────────
        MeasureResult natural = measure(pass.availableWidth, pass.availableHeight);

        // ── Resolve border-box size ───────────────────────────────────────────
        float borderBoxW, borderBoxH;

        if (assignedSize != null) {
            // Size was decided by the flex algorithm
            borderBoxW = assignedSize[0];
            borderBoxH = assignedSize[1];
        } else {
            borderBoxW = constraints.resolveWidth(pass.availableWidth, natural.width());
            borderBoxH = constraints.resolveHeight(pass.availableHeight, natural.height());
        }

        // ── Apply aspect ratio (always after width is final) ──────────────────
        // aspectRatio derives height from the resolved width, so it works correctly
        // regardless of whether the width came from flex, fixed, percentage, etc.
        borderBoxH = constraints.applyAspectRatio(borderBoxW, borderBoxH);

        // ── Update boxModel content size from final border-box ────────────────
        float contentW = Math.max(0, borderBoxW
                - boxModel.getPadding().horizontal()
                - boxModel.getBorder().horizontal());
        float contentH = Math.max(0, borderBoxH
                - boxModel.getPadding().vertical()
                - boxModel.getBorder().vertical());
        boxModel = boxModel.withContentWidth(contentW).withContentHeight(contentH);

        // ── Resolve local position ────────────────────────────────────────────
        float localX, localY;
        if (forcedLocal != null) {
            // Flex: position already calculated (border-box includes margin offset)
            // Account for own margin inside the forced position
            localX = forcedLocal[0] + boxModel.getMargin().getLeft();
            localY = forcedLocal[1] + boxModel.getMargin().getTop();
        } else {
            Alignment align = constraints.getAlignment();
            localX = align.resolveX(pass.availableWidth,  borderBoxW + boxModel.getMargin().horizontal(), constraints.getOffsetX())
                    + boxModel.getMargin().getLeft();
            localY = align.resolveY(pass.availableHeight, borderBoxH + boxModel.getMargin().vertical(),   constraints.getOffsetY())
                    + boxModel.getMargin().getTop();
        }

        // ── Screen space ──────────────────────────────────────────────────────
        float screenX = pass.contentScreenX + localX * pass.scale;
        float screenY = pass.contentScreenY + localY * pass.scale;

        computedLayout = ComputedLayout.create(
                screenX, screenY,
                borderBoxW, borderBoxH,
                boxModel,
                pass.scale, pass.opacity,
                pass.zIndex + constraints.getZIndex()
        );

        layoutDirty = false;

        // ── Layout children ───────────────────────────────────────────────────
        if (!children.isEmpty()) {
            LayoutPassInfo childPass = pass.deriveForChild(computedLayout);
            layoutChildren(childPass);
        }
    }

    /**
     * Layout direct children. Override in container classes.
     * Default: each child independently lays out inside the content box.
     */
    protected void layoutChildren(LayoutPassInfo childPass) {
        for (Widget child : children) {
            child.layout(childPass);
        }
    }

    /** Layout a newly-added child immediately (for dynamic insertion). */
    public void layoutNewChild(Widget child) {
        if (computedLayout == null) return;
        LayoutPassInfo pass = new LayoutPassInfo(
                computedLayout.getContentBounds().getWidth()  / computedLayout.getScale(),
                computedLayout.getContentBounds().getHeight() / computedLayout.getScale(),
                computedLayout.getContentBounds().getX(),
                computedLayout.getContentBounds().getY(),
                computedLayout.getScale(),
                computedLayout.getOpacity(),
                computedLayout.getComputedZIndex()
        );
        child.layout(pass);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PHASE 3 – RENDER
    // ═══════════════════════════════════════════════════════════════════════════

    public final void render(RenderPassInfo rp) {
        if (!visible || computedLayout == null) return;

        GuiGraphics gfx = rp.graphics;
        gfx.pose().pushMatrix();

        renderBackground(rp);
        renderContent(rp);
        renderChildren(rp);
        renderForeground(rp);

        gfx.pose().popMatrix();
        if (debugBounds) renderDebugBounds(rp);
    }

    protected void renderBackground(RenderPassInfo rp) {
        background.render(rp.graphics, computedLayout);
    }

    /** Override to draw custom widget content (text, images…). */
    protected void renderContent(RenderPassInfo rp) {}

    protected void renderChildren(RenderPassInfo rp) {
        // Render in z-index order
        List<Widget> sorted = new ArrayList<>(children);
        sorted.sort((a, b) -> {
            int az = a.computedLayout != null ? a.computedLayout.getComputedZIndex() : 0;
            int bz = b.computedLayout != null ? b.computedLayout.getComputedZIndex() : 0;
            return Integer.compare(az, bz);
        });
        for (Widget child : sorted) child.render(rp);
    }

    protected void renderForeground(RenderPassInfo rp) {}

    private void renderDebugBounds(RenderPassInfo rp) {
        if (computedLayout == null) return;
        GuiGraphics gfx = rp.graphics;
        drawBorderRect(gfx, computedLayout.getBorderBounds(),  0xFFFF0000); // red  = border box
        drawBorderRect(gfx, computedLayout.getContentBounds(), 0xFF0000FF); // blue = content box
    }

    private static void drawBorderRect(GuiGraphics gfx, ComputedLayout.Bounds b, int color) {
        int x  = (int) b.getX();
        int y  = (int) b.getY();
        int x2 = (int) b.getRight();
        int y2 = (int) b.getBottom();
        gfx.fill(x,      y,      x2,     y  + 1, color); // top
        gfx.fill(x,      y2 - 1, x2,     y2,     color); // bottom
        gfx.fill(x,      y,      x  + 1, y2,     color); // left
        gfx.fill(x2 - 1, y,      x2,     y2,     color); // right
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // EVENT HANDLING
    // ═══════════════════════════════════════════════════════════════════════════

    public boolean mouseClicked(double mx, double my, int button) {
        if (!visible || !enabled || computedLayout == null) return false;

        // Children first (deepest = topmost, reverse z-order)
        List<Widget> sorted = new ArrayList<>(children);
        sorted.sort((a, b) -> {
            int az = a.computedLayout != null ? a.computedLayout.getComputedZIndex() : 0;
            int bz = b.computedLayout != null ? b.computedLayout.getComputedZIndex() : 0;
            return Integer.compare(bz, az); // descending
        });
        for (Widget child : sorted) {
            if (child.mouseClicked(mx, my, button)) return true;
        }

        if (computedLayout.containsPoint((float)mx, (float)my)) {
            return events.fireClick(mx, my, button);
        }
        return false;
    }

    public boolean mouseReleased(double mx, double my, int button) {
        if (!visible || !enabled || computedLayout == null) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseReleased(mx, my, button)) return true;
        }
        if (computedLayout.containsPoint((float)mx, (float)my)) {
            return events.fireRelease(mx, my, button);
        }
        return false;
    }

    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (!visible || !enabled) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseDragged(mx, my, button, dx, dy)) return true;
        }
        return events.fireDrag(mx, my, button, dx, dy);
    }

    public boolean mouseScrolled(double mx, double my, double amount) {
        if (!visible || !enabled || computedLayout == null) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseScrolled(mx, my, amount)) return true;
        }
        if (computedLayout.containsPoint((float)mx, (float)my)) {
            return events.fireScroll(mx, my, amount);
        }
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!visible || !enabled) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return events.fireKey(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char c, int modifiers) {
        if (!visible || !enabled) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).charTyped(c, modifiers)) return true;
        }
        return events.fireChar(c, modifiers);
    }

    // ─── Hover management ─────────────────────────────────────────────────────

    public boolean isUnderMouse(float mx, float my) {
        return computedLayout != null && computedLayout.containsPoint(mx, my);
    }

    public void setHovered(boolean h) {
        if (this.hovered == h) return;
        this.hovered = h;
        events.fireHover(h);
    }

    public void setFocused(boolean f) {
        if (this.focused == f) return;
        this.focused = f;
        events.fireFocus(f);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // HIERARCHY
    // ═══════════════════════════════════════════════════════════════════════════

    public Widget addChild(Widget child) {
        child.parent = this;
        children.add(child);
        invalidateLayout();
        return this;
    }

    public Widget removeChild(Widget child) {
        if (children.remove(child)) {
            child.parent = null;
            invalidateLayout();
        }
        return this;
    }

    public void clearChildren() {
        for (Widget c : children) c.parent = null;
        children.clear();
        invalidateLayout();
    }

    public List<Widget> getChildren() { return Collections.unmodifiableList(children); }

    // ═══════════════════════════════════════════════════════════════════════════
    // DIRTY / INVALIDATION
    // ═══════════════════════════════════════════════════════════════════════════

    public void invalidateLayout() {
        layoutDirty = true;
        if (parent != null) parent.invalidateLayout();
    }

    public boolean isLayoutDirty() { return layoutDirty; }

    // ═══════════════════════════════════════════════════════════════════════════
    // FLUENT SETTERS
    // ═══════════════════════════════════════════════════════════════════════════

    public Widget setVisible(boolean v)           { this.visible = v;      invalidateLayout(); return this; }
    public Widget setEnabled(boolean e)           { this.enabled = e;      return this; }
    public Widget setBackground(Background bg)    { this.background = bg;  return this; }
    public Widget setDebugBounds(boolean d)       { this.debugBounds = d;  return this; }

    public Widget setConstraints(LayoutConstraints c) { this.constraints = c; invalidateLayout(); return this; }
    public Widget setFlex(FlexConstraints f)          { this.flex = f;        invalidateLayout(); return this; }
    public Widget setBoxModel(BoxModel bm)            { this.boxModel = bm;   invalidateLayout(); return this; }

    public Widget setPadding(EdgeInsets p)  { boxModel = boxModel.withPadding(p); invalidateLayout(); return this; }
    public Widget setMargin(EdgeInsets m)   { boxModel = boxModel.withMargin(m);  invalidateLayout(); return this; }
    public Widget setBorder(EdgeInsets b)   { boxModel = boxModel.withBorder(b);  invalidateLayout(); return this; }

    public Widget setPadding(float all)     { return setPadding(EdgeInsets.all(all)); }
    public Widget setMargin(float all)      { return setMargin(EdgeInsets.all(all)); }

    // ─── Event shorthand ──────────────────────────────────────────────────────

    public Widget onClick(Runnable action)    { events.onClick(action);    return this; }
    public Widget onHover(EventHandlers.HoverHandler h) { events.onHover(h); return this; }

    // ─── Accessors ────────────────────────────────────────────────────────────

    public String              getId()             { return id; }
    public boolean             isVisible()         { return visible; }
    public boolean             isEnabled()         { return enabled; }
    public boolean             isHovered()         { return hovered; }
    public boolean             isFocused()         { return focused; }
    public LayoutConstraints   getConstraints()    { return constraints; }
    public FlexConstraints     getFlexConstraints(){ return flex; }
    public BoxModel            getBoxModel()       { return boxModel; }
    public EventHandlers       getEvents()         { return events; }
    public @Nullable Widget    getParent()         { return parent; }
    public @Nullable ComputedLayout getComputedLayout() { return computedLayout; }
}


