package net.bati.miniui.widget;

import net.bati.miniui.layout.LayoutPassInfo;
import net.bati.miniui.layout.MeasureResult;
import net.bati.miniui.rendering.RenderPassInfo;
import net.minecraft.client.gui.GuiGraphics;

import java.util.*;
import java.util.function.Supplier;

/**
 * A scrollable virtual grid. Combines viewport/scrollbar management with a
 * fixed-size widget pool — only the rows visible in the viewport are
 * materialized as real widgets.
 *
 * <p>This is the correct unit of abstraction: virtualization only makes sense
 * inside a scrollable viewport, so both concerns live in a single class.
 *
 * <h3>Usage</h3>
 * <pre>
 * VirtualScrollGrid&lt;Item&gt; grid = new VirtualScrollGrid&lt;&gt;(
 *     "cosmetic-grid", 4,
 *     () -> new Button("_", ""),
 *     (btn, item, index) -> {
 *         btn.clearClickHandlers();
 *         btn.setLabel(item.name());
 *         btn.setColors(item.color(), item.hoverColor());
 *         btn.onClick(() -> open(item));
 *     }
 * );
 * grid.gap(8).cellAspectRatio(1f).setItems(items);
 * </pre>
 *
 * @param <T> the data type for each grid cell
 */
public class VirtualScrollGrid<K extends Widget, T> extends Widget {

    // ─── Public interface ─────────────────────────────────────────────────────

    @FunctionalInterface
    public interface ItemBinder<K, T> {
        void bind(K widget, T item, int index);
    }

    // ─── Grid config ──────────────────────────────────────────────────────────

    private int   columns;
    private float gap             = 0f;
    private float rowGap          = -1f;  // -1 = use gap
    private float columnGap       = -1f;  // -1 = use gap
    private float cellAspectRatio = -1f;  // -1 = not set
    private int   bufferRows      = 1;

    // ─── Scroll config ────────────────────────────────────────────────────────

    private float   scrollSpeed       = 20f;
    private boolean showScrollbar     = true;
    private int     scrollbarColor    = 0xFF555555;
    private int     scrollbarBgColor  = 0xFF333333;
    private int     scrollbarWidth    = 6;

    // ─── Data ─────────────────────────────────────────────────────────────────

    private List<T>              items   = Collections.emptyList();
    private final Supplier<K> factory;
    private final ItemBinder<K, T>    binder;

    // ─── Pool ─────────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private K[] pool = (K[]) new Widget[0];
    private int[]    slotToIndex = new int[0];  // -1 = free

    // ─── Scroll state ─────────────────────────────────────────────────────────

    private float scrollOffset = 0f;
    private float maxScroll    = 0f;

    // ─── Cached geometry (set during layoutChildren) ──────────────────────────

    private float          viewportW      = 0f;
    private float          viewportH      = 0f;
    private float          cachedCellW    = 0f;
    private float          cachedRowH     = 0f;
    private float          cachedRowStride = 0f;  // rowH + rowGap
    private LayoutPassInfo cachedChildPass = null;  // pass for pool widgets

    // ─── Constructor ──────────────────────────────────────────────────────────

    public VirtualScrollGrid(String id, int columns,
                             Supplier<K> factory,
                             ItemBinder<K, T> binder) {
        super(id);
        this.columns = Math.max(1, columns);
        this.factory = factory;
        this.binder  = binder;
    }

    // ─── Fluent grid config ───────────────────────────────────────────────────

    public VirtualScrollGrid<K, T> columns(int n)           { this.columns = Math.max(1, n); invalidateLayout(); return this; }
    public VirtualScrollGrid<K, T> gap(float g)             { this.gap = g;                  invalidateLayout(); return this; }
    public VirtualScrollGrid<K, T> rowGap(float g)          { this.rowGap = g;               invalidateLayout(); return this; }
    public VirtualScrollGrid<K, T> columnGap(float g)       { this.columnGap = g;            invalidateLayout(); return this; }
    public VirtualScrollGrid<K, T> cellAspectRatio(float r) { this.cellAspectRatio = r;      invalidateLayout(); return this; }
    public VirtualScrollGrid<K, T> bufferRows(int r)        { this.bufferRows = Math.max(0, r); return this; }

    // ─── Fluent scroll config ─────────────────────────────────────────────────

    public VirtualScrollGrid<K, T> scrollSpeed(float s)       { this.scrollSpeed = s;       return this; }
    public VirtualScrollGrid<K, T> showScrollbar(boolean show){ this.showScrollbar = show;   return this; }
    public VirtualScrollGrid<K, T> scrollbarWidth(int w)      { this.scrollbarWidth = w;     invalidateLayout(); return this; }
    public VirtualScrollGrid<K, T> scrollbarColors(int bg, int thumb) {
        this.scrollbarBgColor = bg;
        this.scrollbarColor   = thumb;
        return this;
    }

    // ─── Data API ─────────────────────────────────────────────────────────────

    /**
     * Replace the item list. Resets scroll to top and re-layouts the pool.
     * Does NOT trigger a full tree layout — only re-layouts this widget's subtree,
     * which is correct because the VirtualScrollGrid occupies a fixed size in the
     * parent layout (defined by its constraints/flexGrow).
     */
    public VirtualScrollGrid<K, T> setItems(List<T> items) {
        this.items = items == null ? Collections.emptyList() : items;
        Arrays.fill(slotToIndex, -1);  // force full rebind
        scrollOffset = 0f;
        relayoutChildren();
        return this;
    }

    public List<T> getItems() { return Collections.unmodifiableList(items); }

    public void resetScroll() {
        if (scrollOffset == 0f) return;
        scrollOffset = 0f;
        relayoutChildren();
    }

    /**
     * Re-run the binder on all currently materialized pool slots without
     * recreating widgets or changing scroll position. Use this when external
     * state that affects widget appearance changes — e.g. the equipped item
     * changed and buttons need to reflect their new selected/idle state.
     *
     * <p>Complexity: O(pool size), independent of total item count.
     */
    public void rebindAll() {
        for (int s = 0; s < pool.length; s++) {
            int di = slotToIndex[s];
            if (di >= 0 && di < items.size()) {
                binder.bind(pool[s], items.get(di), di);
            }
        }
    }

    // ─── Geometry helpers ─────────────────────────────────────────────────────

    private float effectiveColumnGap() { return columnGap >= 0 ? columnGap : gap; }
    private float effectiveRowGap()    { return rowGap    >= 0 ? rowGap    : gap; }

    private float gridWidth() {
        return showScrollbar ? viewportW - scrollbarWidth : viewportW;
    }

    private float cellWidth(float containerW) {
        return Math.max(1, (containerW - effectiveColumnGap() * (columns - 1)) / columns);
    }

    private float uniformRowH(float cw) {
        return cellAspectRatio > 0 ? cw / cellAspectRatio : cw;
    }

    private int totalRows() {
        return items.isEmpty() ? 0 : (int) Math.ceil((double) items.size() / columns);
    }

    private float totalHeight(float cw) {
        int rows = totalRows();
        if (rows == 0) return 0;
        return rows * uniformRowH(cw) + (rows - 1) * effectiveRowGap();
    }

    private int yToRow(float y) {
        if (cachedRowStride <= 0) return 0;
        return Math.max(0, Math.min(totalRows() - 1, (int) (y / cachedRowStride)));
    }

    // ─── Pool management ──────────────────────────────────────────────────────

    private int requiredPoolSize() {
        if (cachedRowStride <= 0 || viewportH <= 0) return columns * (bufferRows * 2 + 2);
        int visibleRows = (int) Math.ceil(viewportH / cachedRowStride) + bufferRows * 2 + 1;
        return visibleRows * columns;
    }

    @SuppressWarnings("unchecked")
    private void resizePool(int size) {
        if (pool.length == size) return;

        K[] newPool = (K[]) new Widget[size];
        int[]    newSlotIdx = new int[size];
        Arrays.fill(newSlotIdx, -1);

        int keep = Math.min(pool.length, size);
        System.arraycopy(pool,        0, newPool,    0, keep);
        System.arraycopy(slotToIndex, 0, newSlotIdx, 0, keep);

        // Detach widgets outside the new pool size
        for (int i = size; i < pool.length; i++) {
            if (pool[i] != null) {
                children.remove(pool[i]);
                pool[i].parent = null;
            }
        }

        // Create widgets for new slots — bypass addChild to avoid invalidateLayout
        for (int i = keep; i < size; i++) {
            K w = factory.get();
            w.parent = this;
            newPool[i]    = w;
            newSlotIdx[i] = -1;
            children.add(w);
        }

        pool        = newPool;
        slotToIndex = newSlotIdx;
    }

    // ─── Materialization ──────────────────────────────────────────────────────

    /**
     * Bind pool slots to the currently visible data window.
     * Slots already showing the correct item are untouched (no binder call).
     * Complexity: O(pool size), independent of total item count.
     */
    private void materializeWindow(LayoutPassInfo childPass) {
        if (childPass == null || items.isEmpty() || cachedRowStride <= 0) return;

        int firstRow = Math.max(0, yToRow(scrollOffset) - bufferRows);
        int lastRow  = Math.min(totalRows() - 1, yToRow(scrollOffset + viewportH) + bufferRows);
        int firstIdx = firstRow * columns;
        int lastIdx  = Math.min(items.size() - 1, (lastRow + 1) * columns - 1);

        // Reverse map: dataIndex → slot currently showing it
        int[] indexToSlot = new int[items.size()];
        Arrays.fill(indexToSlot, -1);
        for (int s = 0; s < pool.length; s++) {
            int di = slotToIndex[s];
            if (di >= 0 && di < items.size()) indexToSlot[di] = s;
        }

        // Slots outside the window are free for reuse
        Queue<Integer> freeSlots = new ArrayDeque<>();
        for (int s = 0; s < pool.length; s++) {
            int di = slotToIndex[s];
            if (di < firstIdx || di > lastIdx) {
                freeSlots.add(s);
                slotToIndex[s] = -1;
            }
        }

        float cw      = cachedCellW;
        float ch      = cachedRowH;
        float colGap  = effectiveColumnGap();
        float rowGapV = effectiveRowGap();

        for (int di = firstIdx; di <= lastIdx; di++) {
            int slot = indexToSlot[di];

            if (slot == -1) {
                Integer fs = freeSlots.poll();
                if (fs == null) break;
                slot = fs;
                slotToIndex[slot] = di;
                binder.bind(pool[slot], items.get(di), di);
            }

            int   row = di / columns;
            int   col = di % columns;
            float lx  = col * (cw + colGap);
            // ly is relative to the virtual grid top, scroll offset applied via childPass
            float ly  = row * (ch + rowGapV);

            pool[slot].layoutAbsolute(childPass, cw, ch, lx, ly);
        }

        // Update visibility without triggering invalidateLayout
        for (int fs : freeSlots)              pool[fs].visible = false;
        for (int s = 0; s < pool.length; s++) if (slotToIndex[s] >= 0) pool[s].visible = true;
    }

    // ─── PHASE 1 — MEASURE ───────────────────────────────────────────────────

    @Override
    protected MeasureResult measureContent(float aw, float ah) {
        // Natural size is 0×0 — always sized by parent via constraints/flexGrow,
        // exactly like ScrollContainer.
        return MeasureResult.ZERO;
    }

    // ─── PHASE 2 — LAYOUT ────────────────────────────────────────────────────

    @Override
    protected boolean alwaysLayoutChildren() { return true; }

    @Override
    protected void layoutChildren(LayoutPassInfo pass) {
        viewportW = pass.availableWidth;
        viewportH = pass.availableHeight;

        float gw = gridWidth();  // viewport width minus scrollbar reservation

        // ── Update grid geometry ──────────────────────────────────────────────
        cachedCellW     = cellWidth(gw);
        cachedRowH      = uniformRowH(cachedCellW);
        cachedRowStride = cachedRowH + effectiveRowGap();

        // ── Recalculate maxScroll ─────────────────────────────────────────────
        float totalH = totalHeight(cachedCellW);
        maxScroll    = Math.max(0, totalH - viewportH);
        scrollOffset = Math.min(scrollOffset, maxScroll);

        // ── Build childPass with scroll offset applied ────────────────────────
        // Pool widgets are positioned relative to the grid top (ly = row * stride).
        // The scroll offset shifts the entire grid up by subtracting it from screenY.
        cachedChildPass = new LayoutPassInfo(
                gw, viewportH,
                pass.contentScreenX,
                pass.contentScreenY - scrollOffset * pass.scale,
                pass.scale, pass.opacity, pass.zIndex
        );

        // ── Resize pool and materialize visible window ────────────────────────
        resizePool(requiredPoolSize());
        materializeWindow(cachedChildPass);
    }

    // ─── PHASE 3 — RENDER ────────────────────────────────────────────────────

    @Override
    protected void renderContent(RenderPassInfo rp) {}

    @Override
    protected void renderChildren(RenderPassInfo rp) {
        if (computedLayout == null) return;

        var bounds = computedLayout.getPaddingBounds();
        GuiGraphics gfx = rp.graphics;

        float sbW = showScrollbar ? scrollbarWidth : 0f;
        gfx.enableScissor(
                (int)  bounds.getX(),
                (int)  bounds.getY(),
                (int) (bounds.getRight() - sbW),
                (int)  bounds.getBottom()
        );
        super.renderChildren(rp);
        gfx.disableScissor();

        if (showScrollbar && maxScroll > 0) renderScrollbar(rp);
    }

    private void renderScrollbar(RenderPassInfo rp) {
        if (computedLayout == null) return;
        var bounds = computedLayout.getPaddingBounds();
        GuiGraphics gfx = rp.graphics;

        int bx = (int)(bounds.getRight() - scrollbarWidth);
        int by = (int) bounds.getY();
        int bh = (int) bounds.getHeight();

        gfx.fill(bx, by, bx + scrollbarWidth, by + bh, scrollbarBgColor);

        float viewRatio   = bh / (bh + maxScroll);
        int   thumbH      = Math.max(16, (int)(bh * viewRatio));
        float scrollRatio = maxScroll > 0 ? scrollOffset / maxScroll : 0f;
        int   thumbY      = by + (int)((bh - thumbH) * scrollRatio);

        gfx.fill(bx, thumbY, bx + scrollbarWidth, thumbY + thumbH, scrollbarColor);
    }

    // ─── EVENTS ───────────────────────────────────────────────────────────────

    private boolean isInViewport(double mx, double my) {
        if (computedLayout == null) return false;
        var b = computedLayout.getPaddingBounds();
        float sbW = showScrollbar ? scrollbarWidth : 0f;
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

        float prev = scrollOffset;
        scrollOffset -= (float) amount * scrollSpeed;
        scrollOffset  = Math.max(0, Math.min(maxScroll, scrollOffset));
        if (scrollOffset == prev) return true;

        relayoutChildren();
        return true;
    }

    // ─── Guard against direct child manipulation ──────────────────────────────

    @Override
    public Widget addChild(Widget child) {
        throw new UnsupportedOperationException(
                "VirtualScrollGrid manages its children internally. Use setItems() instead.");
    }
}