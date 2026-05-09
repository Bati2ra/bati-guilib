package net.bati.miniui.widget;

import net.bati.miniui.layout.EdgeInsets;
import net.bati.miniui.layout.FlexConstraints;
import net.bati.miniui.layout.LayoutPassInfo;
import net.bati.miniui.layout.MeasureResult;
import net.bati.miniui.layout.flex.FlexLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A container that arranges its children using CSS Flexbox semantics.
 *
 * <p>FlexContainers can be nested arbitrarily. Each is an independent flex
 * context – it measures its children, runs the flex algorithm, and assigns
 * each child an absolute position and size.
 */
public class FlexContainer extends Widget {

    private final FlexLayout flexLayout;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public FlexContainer(String id) {
        super(id);
        this.flexLayout = new FlexLayout();
    }

    public static FlexContainer row(String id) {
        return new FlexContainer(id).direction(FlexLayout.FlexDirection.ROW);
    }

    public static FlexContainer column(String id) {
        return new FlexContainer(id).direction(FlexLayout.FlexDirection.COLUMN);
    }

    // ─── Fluent configuration ─────────────────────────────────────────────────

    public FlexContainer direction(FlexLayout.FlexDirection d)       { flexLayout.direction(d);       invalidateLayout(); return this; }
    public FlexContainer justifyContent(FlexLayout.JustifyContent j) { flexLayout.justifyContent(j);  invalidateLayout(); return this; }
    public FlexContainer alignItems(FlexLayout.AlignItems a)          { flexLayout.alignItems(a);      invalidateLayout(); return this; }
    public FlexContainer wrap(FlexLayout.FlexWrap w)                  { flexLayout.wrap(w);            invalidateLayout(); return this; }
    public FlexContainer gap(float g)                                  { flexLayout.gap(g);             invalidateLayout(); return this; }
    public FlexContainer rowGap(float g)                               { flexLayout.rowGap(g);          invalidateLayout(); return this; }
    public FlexContainer columnGap(float g)                            { flexLayout.columnGap(g);       invalidateLayout(); return this; }

    public FlexLayout getFlexLayout() { return flexLayout; }

    // ─── Fluent child helper ──────────────────────────────────────────────────

    public FlexContainer add(Widget child) {
        addChild(child);
        return this;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PHASE 1 – MEASURE
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Measure total natural content size for this flex container.
     * <p>
     * We measure each child, compute their base sizes (respecting their own
     * LayoutConstraints), then apply the flex algorithm conceptually to arrive
     * at the container's natural size.
     */
    @Override
    protected MeasureResult measureContent(float availableWidth, float availableHeight) {
        if (children.isEmpty()) return MeasureResult.ZERO;

        boolean isRow = flexLayout.getDirection().isRow();

        List<Float> mainSizes  = new ArrayList<>(children.size());
        List<Float> crossSizes = new ArrayList<>(children.size());

        for (Widget child : children) {
            if (!child.isVisible()) continue;

            boolean childHasGrow = child.getFlexConstraints().getFlexGrow() > 0;

            float measureW = isRow ? (childHasGrow ? 0f : availableWidth)  : availableWidth;
            float measureH = isRow ? availableHeight : (childHasGrow ? 0f : availableHeight);
            MeasureResult natural = child.measure(measureW, measureH);

            float bw, bh;
            if (isRow) {
                bw = childHasGrow ? natural.width()  : child.getConstraints().resolveWidth(availableWidth,   natural.width());
                bh =                                   child.getConstraints().resolveHeight(availableHeight, natural.height());
            } else {
                bw =                                   child.getConstraints().resolveWidth(availableWidth,   natural.width());
                bh = childHasGrow ? natural.height() : child.getConstraints().resolveHeight(availableHeight, natural.height());
            }

            Float basis = child.getFlexConstraints().getFlexBasis();
            float mainBase  = isRow ? bw : bh;
            float crossBase = isRow ? bh : bw;
            if (basis != null) mainBase = basis;

            mainSizes.add(mainBase   + (isRow ? child.getBoxModel().getMargin().horizontal()
                    : child.getBoxModel().getMargin().vertical()));
            crossSizes.add(crossBase + (isRow ? child.getBoxModel().getMargin().vertical()
                    : child.getBoxModel().getMargin().horizontal()));
        }

        float main  = flexLayout.measureMainAxis(mainSizes);
        float cross = flexLayout.measureCrossAxis(crossSizes);

        return isRow ? new MeasureResult(main, cross) : new MeasureResult(cross, main);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PHASE 2 – LAYOUT CHILDREN
    // ═══════════════════════════════════════════════════════════════════════════

    @Override
    protected void layoutChildren(LayoutPassInfo childPass) {
        if (children.isEmpty()) return;

        boolean isRow = flexLayout.getDirection().isRow();

        float containerW = childPass.availableWidth;
        float containerH = childPass.availableHeight;

        // ── Step 1: build FlexItem list ───────────────────────────────────────
        List<FlexLayout.FlexItem> items = new ArrayList<>(children.size());

        for (Widget child : children) {
            if (!child.isVisible()) continue;

            boolean childHasGrow = child.getFlexConstraints().getFlexGrow() > 0;

            float measureW = isRow ? (childHasGrow ? 0f : containerW) : containerW;
            float measureH = isRow ? containerH : (childHasGrow ? 0f : containerH);
            MeasureResult natural = child.measure(measureW, measureH);

            float bw, bh;
            if (isRow) {
                bw = childHasGrow ? natural.width()  : child.getConstraints().resolveWidth(containerW,  natural.width());
                bh =                                   child.getConstraints().resolveHeight(containerH, natural.height());
            } else {
                bw =                                   child.getConstraints().resolveWidth(containerW,  natural.width());
                bh = childHasGrow ? natural.height() : child.getConstraints().resolveHeight(containerH, natural.height());
            }

            // aspectRatio: height always derives from width (ratio = w/h).
            // bw is already correctly resolved (fillParent, fixed, etc.) so we
            // can derive bh here for the FlexItem slot size. internalLayout will
            // re-apply applyAspectRatio with the true assigned width.
            if (child.getConstraints().hasAspectRatio()) {
                bh = bw / child.getConstraints().getAspectRatio();
            }

            EdgeInsets margin = child.getBoxModel().getMargin();
            float itemMainSize  = isRow ? (bw + margin.horizontal()) : (bh + margin.vertical());
            float itemCrossSize = isRow ? (bh + margin.vertical())   : (bw + margin.horizontal());

            FlexConstraints fc = child.getFlexConstraints();
            items.add(new FlexLayout.FlexItem(
                    child.getId(),
                    itemMainSize,
                    itemCrossSize,
                    fc.getFlexGrow(),
                    fc.getFlexShrink(),
                    fc.getFlexBasis(),
                    fc.getAlignSelf()
            ));
        }

        // ── Step 2: run flex algorithm ────────────────────────────────────────
        List<FlexLayout.ItemLayout> layouts = flexLayout.computeLayout(items, containerW, containerH);

        java.util.Map<String, FlexLayout.ItemLayout> byId = new java.util.HashMap<>();
        for (FlexLayout.ItemLayout il : layouts) byId.put(il.getId(), il);

        // ── Step 3: assign computed layout to each child ──────────────────────
        for (Widget child : children) {
            if (!child.isVisible()) continue;

            FlexLayout.ItemLayout il = byId.get(child.getId());
            if (il == null) continue;

            EdgeInsets margin = child.getBoxModel().getMargin();
            float assignedW = il.getWidth()  - margin.horizontal();
            float assignedH = il.getHeight() - margin.vertical();

            // aspectRatio is applied inside internalLayout (via layoutAbsolute) — no
            // special handling needed here; the correct height comes from measure().
            child.layoutAbsolute(childPass, assignedW, assignedH, il.getX(), il.getY());
        }
    }
}