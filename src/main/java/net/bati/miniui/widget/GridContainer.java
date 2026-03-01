package net.bati.miniui.widget;

import net.bati.miniui.layout.LayoutPassInfo;
import net.bati.miniui.layout.MeasureResult;
import net.bati.miniui.rendering.RenderPassInfo;

/**
 * A container that arranges children in a fixed-column grid.
 *
 * <p>Unlike FlexContainer with wrap, GridContainer guarantees exactly
 * {@code columns} items per row, distributing available width evenly
 * and deducting gaps automatically — no hardcoded sizes needed.
 *
 * <p>Optionally, each cell can enforce an aspect ratio so rows of square
 * (or any proportional) items work without specifying height at all.
 *
 * <pre>
 * GridContainer grid = new GridContainer("shop", 3)
 *     .gap(8)
 *     .cellAspectRatio(1f);   // square cells
 *
 * for (Item item : items) {
 *     grid.add(new Button(item.id(), item.name()));
 * }
 * </pre>
 */
public class GridContainer extends Widget {

    private int   columns;
    private float gap          = 0f;
    private float rowGap       = -1f; // -1 = use gap
    private float columnGap    = -1f; // -1 = use gap
    private float cellAspectRatio = -1f; // -1 = not set (height comes from child)

    // ─── Constructor ──────────────────────────────────────────────────────────

    public GridContainer(String id, int columns) {
        super(id);
        this.columns = Math.max(1, columns);
    }

    // ─── Fluent configuration ─────────────────────────────────────────────────

    public GridContainer columns(int n)            { this.columns = Math.max(1, n); invalidateLayout(); return this; }
    public GridContainer gap(float g)              { this.gap = g;                  invalidateLayout(); return this; }
    public GridContainer rowGap(float g)           { this.rowGap = g;               invalidateLayout(); return this; }
    public GridContainer columnGap(float g)        { this.columnGap = g;            invalidateLayout(); return this; }
    /** Enforce a fixed aspect ratio (width/height) for every cell. 1f = square. */
    public GridContainer cellAspectRatio(float r)  { this.cellAspectRatio = r;      invalidateLayout(); return this; }

    public GridContainer add(Widget child) { addChild(child); return this; }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private float effectiveColumnGap() { return columnGap >= 0 ? columnGap : gap; }
    private float effectiveRowGap()    { return rowGap    >= 0 ? rowGap    : gap; }

    /**
     * Compute the cell width given the container's available content width.
     * cellW = (containerW - columnGap * (columns - 1)) / columns
     */
    private float cellWidth(float containerW) {
        float totalGaps = effectiveColumnGap() * (columns - 1);
        return Math.max(0, (containerW - totalGaps) / columns);
    }

    private float cellHeight(float cellW, Widget child) {
        if (cellAspectRatio > 0) return cellW / cellAspectRatio;
        // No grid-level ratio — measure the child at cellW to get its natural height
        MeasureResult nat = child.measure(cellW, Float.MAX_VALUE);
        float resolved = child.getConstraints().resolveHeight(Float.MAX_VALUE, nat.height());
        // Apply the child's own aspectRatio if it has one
        return child.getConstraints().applyAspectRatio(cellW, resolved);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PHASE 1 – MEASURE
    // ═══════════════════════════════════════════════════════════════════════════

    @Override
    protected MeasureResult measureContent(float availableWidth, float availableHeight) {
        if (children.isEmpty()) return MeasureResult.ZERO;

        float cw = cellWidth(availableWidth);
        int   visibleCount = (int) children.stream().filter(Widget::isVisible).count();
        int   rows = (int) Math.ceil((double) visibleCount / columns);

        // Row height = max cell height in the row (or fixed aspect ratio)
        float totalH = 0;
        int   idx    = 0;
        for (int row = 0; row < rows; row++) {
            float rowH = 0;
            for (int col = 0; col < columns && idx < children.size(); col++, idx++) {
                Widget child = children.get(idx);
                if (!child.isVisible()) { col--; continue; }
                rowH = Math.max(rowH, cellHeight(cw, child));
            }
            totalH += rowH;
            if (row < rows - 1) totalH += effectiveRowGap();
        }

        return new MeasureResult(availableWidth, totalH);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PHASE 2 – LAYOUT CHILDREN
    // ═══════════════════════════════════════════════════════════════════════════

    @Override
    protected void layoutChildren(LayoutPassInfo childPass) {
        if (children.isEmpty()) return;

        float containerW = childPass.availableWidth;
        float cw         = cellWidth(containerW);
        float colGap     = effectiveColumnGap();
        float rowGapVal  = effectiveRowGap();

        float cursorY = 0f;
        int   col     = 0;
        float rowH    = 0f;

        // Two-pass per row: first collect row height, then layout all cells in the row
        java.util.List<Widget> visible = new java.util.ArrayList<>();
        for (Widget w : children) if (w.isVisible()) visible.add(w);

        int total = visible.size();
        int i     = 0;

        while (i < total) {
            // ── Collect this row ──────────────────────────────────────────────
            int rowStart = i;
            int rowEnd   = Math.min(i + columns, total);
            rowH = 0f;

            for (int j = rowStart; j < rowEnd; j++) {
                rowH = Math.max(rowH, cellHeight(cw, visible.get(j)));
            }

            // ── Layout each cell in the row ───────────────────────────────────
            float cursorX = 0f;
            for (int j = rowStart; j < rowEnd; j++) {
                Widget child = visible.get(j);

                float ch = cellAspectRatio > 0
                        ? cw / cellAspectRatio
                        : child.getConstraints().applyAspectRatio(cw, rowH);

                child.layoutAbsolute(childPass, cw, ch, cursorX, cursorY);

                cursorX += cw + colGap;
            }

            cursorY += rowH + rowGapVal;
            i = rowEnd;
        }
    }

    @Override
    protected void renderContent(RenderPassInfo rp) {}
}