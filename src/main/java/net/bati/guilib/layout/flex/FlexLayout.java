package net.bati.guilib.layout.flex;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure flexbox layout algorithm. No Minecraft dependencies – fully unit-testable.
 */
public final class FlexLayout {

    // ─── Enumerations ─────────────────────────────────────────────────────────

    public enum FlexDirection {
        ROW, COLUMN, ROW_REVERSE, COLUMN_REVERSE;

        public boolean isRow()     { return this == ROW || this == ROW_REVERSE; }
        public boolean isReverse() { return this == ROW_REVERSE || this == COLUMN_REVERSE; }
    }

    public enum JustifyContent {
        FLEX_START, FLEX_END, CENTER,
        SPACE_BETWEEN, SPACE_AROUND, SPACE_EVENLY
    }

    public enum AlignItems {
        FLEX_START, FLEX_END, CENTER, STRETCH, BASELINE
    }

    public enum FlexWrap {
        NO_WRAP, WRAP, WRAP_REVERSE
    }

    // ─── Input / Output DTOs ──────────────────────────────────────────────────

    /** Input: describes one flex child with its resolved natural size. */
    public static final class FlexItem {
        private final String id;
        private final float mainSize;   // natural border-box size along main axis
        private final float crossSize;  // natural border-box size along cross axis
        private final float flexGrow;
        private final float flexShrink;
        private final Float flexBasis;
        private final AlignItems alignSelf; // null = use container's alignItems

        public FlexItem(String id, float mainSize, float crossSize,
                        float flexGrow, float flexShrink,
                        Float flexBasis, AlignItems alignSelf) {
            this.id         = id;
            this.mainSize   = mainSize;
            this.crossSize  = crossSize;
            this.flexGrow   = flexGrow;
            this.flexShrink = flexShrink;
            this.flexBasis  = flexBasis;
            this.alignSelf  = alignSelf;
        }

        public String      getId()        { return id; }
        public float       getMainSize()  { return mainSize; }
        public float       getCrossSize() { return crossSize; }
        public float       getFlexGrow()  { return flexGrow; }
        public float       getFlexShrink(){ return flexShrink; }
        public Float       getFlexBasis() { return flexBasis; }
        public AlignItems  getAlignSelf() { return alignSelf; }
    }

    /** Output: final position and size for one flex child (in local coords, relative to container content box). */
    public static final class ItemLayout {
        private final String id;
        private final float x, y, width, height;

        public ItemLayout(String id, float x, float y, float width, float height) {
            this.id = id; this.x = x; this.y = y;
            this.width = width; this.height = height;
        }

        public String getId()     { return id; }
        public float  getX()      { return x; }
        public float  getY()      { return y; }
        public float  getWidth()  { return width; }
        public float  getHeight() { return height; }
    }

    // ─── Configuration ────────────────────────────────────────────────────────

    private FlexDirection   direction       = FlexDirection.ROW;
    private JustifyContent  justifyContent  = JustifyContent.FLEX_START;
    private AlignItems      alignItems      = AlignItems.FLEX_START;
    private FlexWrap        wrap            = FlexWrap.NO_WRAP;
    private float           gap             = 0f;
    private float           rowGap          = -1f; // -1 means "use gap"
    private float           columnGap       = -1f;

    // ─── Builders ─────────────────────────────────────────────────────────────

    public FlexLayout direction(FlexDirection d)      { this.direction      = d; return this; }
    public FlexLayout justifyContent(JustifyContent j){ this.justifyContent  = j; return this; }
    public FlexLayout alignItems(AlignItems a)         { this.alignItems     = a; return this; }
    public FlexLayout wrap(FlexWrap w)                 { this.wrap           = w; return this; }
    public FlexLayout gap(float g)                     { this.gap            = g; return this; }
    public FlexLayout rowGap(float g)                  { this.rowGap         = g; return this; }
    public FlexLayout columnGap(float g)               { this.columnGap      = g; return this; }

    public FlexDirection  getDirection()      { return direction; }
    public JustifyContent getJustifyContent() { return justifyContent; }
    public AlignItems     getAlignItems()     { return alignItems; }
    public FlexWrap       getWrap()           { return wrap; }
    public float          getGap()            { return gap; }
    public float          getRowGap()         { return rowGap >= 0 ? rowGap : gap; }
    public float          getColumnGap()      { return columnGap >= 0 ? columnGap : gap; }

    // ─── Main algorithm ───────────────────────────────────────────────────────

    /**
     * Compute layout for the provided items inside a container of
     * ({@code containerW} × {@code containerH}).
     *
     * @return ordered list of {@link ItemLayout} matching input order.
     */
    public List<ItemLayout> computeLayout(List<FlexItem> items,
                                          float containerW, float containerH) {
        if (items.isEmpty()) return List.of();

        boolean isRow     = direction.isRow();
        boolean isReverse = direction.isReverse();

        float mainContainer  = isRow ? containerW : containerH;
        float crossContainer = isRow ? containerH : containerW;
        float mainGap        = isRow ? getColumnGap() : getRowGap();
        float crossGap       = isRow ? getRowGap()    : getColumnGap();

        // ── Step 1: collect lines (wrap) ───────────────────────────────────────
        List<List<FlexItem>> lines = collectLines(items, mainContainer, mainGap);

        List<ItemLayout> result = new ArrayList<>(items.size());

        float crossOffset = 0f;

        for (List<FlexItem> line : lines) {
            List<ItemLayout> lineLayouts = computeLine(
                    line, mainContainer, crossContainer,
                    mainGap, isRow);
            // translate cross offset
            for (ItemLayout il : lineLayouts) {
                float x = isRow ? il.getX() : crossOffset + il.getX();
                float y = isRow ? crossOffset + il.getY() : il.getY();
                result.add(new ItemLayout(il.getId(), x, y, il.getWidth(), il.getHeight()));
            }
            // advance cross: max cross size in this line + crossGap
            float lineMax = (float) lineLayouts.stream()
                    .mapToDouble(il -> isRow ? il.getHeight() : il.getWidth())
                    .max().orElse(0.0);
            crossOffset += lineMax + crossGap;
        }

        // Reverse if needed
        if (isReverse) {
            result = reverseMainAxis(result, mainContainer, isRow);
        }

        return result;
    }

    // ── Line collection (wrap) ────────────────────────────────────────────────

    private List<List<FlexItem>> collectLines(List<FlexItem> items,
                                              float mainContainer, float mainGap) {
        if (wrap == FlexWrap.NO_WRAP) {
            return List.of(items);
        }

        List<List<FlexItem>> lines = new ArrayList<>();
        List<FlexItem> current = new ArrayList<>();
        float used = 0f;
        boolean first = true;

        for (FlexItem item : items) {
            float base = item.getFlexBasis() != null ? item.getFlexBasis() : item.getMainSize();
            float needed = first ? base : base + mainGap;

            if (!first && used + needed > mainContainer && !current.isEmpty()) {
                lines.add(current);
                current = new ArrayList<>();
                used = base;
                first = true;
            } else {
                used += needed;
                first = false;
            }
            current.add(item);
        }
        if (!current.isEmpty()) lines.add(current);
        return lines;
    }

    // ── Single line layout ────────────────────────────────────────────────────

    private List<ItemLayout> computeLine(List<FlexItem> line,
                                         float mainContainer, float crossContainer,
                                         float mainGap, boolean isRow) {
        int n = line.size();

        // Step 2: base sizes
        float[] base = new float[n];
        float totalBase = 0f;
        for (int i = 0; i < n; i++) {
            FlexItem item = line.get(i);
            base[i] = item.getFlexBasis() != null ? item.getFlexBasis() : item.getMainSize();
            totalBase += base[i];
        }

        float totalGap = mainGap * (n - 1);
        float available = mainContainer - totalBase - totalGap;

        // Step 3/4: grow or shrink
        float[] finalMain = new float[n];
        if (available > 0) {
            float totalGrow = 0f;
            for (FlexItem item : line) totalGrow += item.getFlexGrow();

            for (int i = 0; i < n; i++) {
                float grow = line.get(i).getFlexGrow();
                finalMain[i] = (totalGrow > 0 && grow > 0)
                        ? base[i] + available * (grow / totalGrow)
                        : base[i];
            }
        } else if (available < 0) {
            float totalShrink = 0f;
            for (FlexItem item : line) totalShrink += item.getFlexShrink();

            for (int i = 0; i < n; i++) {
                float shrink = line.get(i).getFlexShrink();
                finalMain[i] = (totalShrink > 0 && shrink > 0)
                        ? Math.max(0, base[i] + available * (shrink / totalShrink))
                        : base[i];
            }
        } else {
            System.arraycopy(base, 0, finalMain, 0, n);
        }

        // Cross sizes – if STRETCH, fill container
        float[] finalCross = new float[n];
        for (int i = 0; i < n; i++) {
            AlignItems eff = effectiveAlign(line.get(i));
            if (eff == AlignItems.STRETCH) {
                finalCross[i] = crossContainer;
            } else {
                finalCross[i] = line.get(i).getCrossSize();
            }
        }

        // Step 5: justify (main positions)
        float totalFinal = 0;
        for (float f : finalMain) totalFinal += f;
        float freeSpace = mainContainer - totalFinal - totalGap;

        float[] mainPos = new float[n];
        computeMainPositions(mainPos, finalMain, n, mainGap, freeSpace, mainContainer);

        // Step 6: align (cross positions)
        float maxCross = 0;
        for (float c : finalCross) maxCross = Math.max(maxCross, c);
        float effectiveCross = Math.max(crossContainer, maxCross);

        float[] crossPos = new float[n];
        for (int i = 0; i < n; i++) {
            crossPos[i] = computeCrossPosition(line.get(i), finalCross[i], effectiveCross);
        }

        // Assemble
        List<ItemLayout> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            float x = isRow ? mainPos[i] : crossPos[i];
            float y = isRow ? crossPos[i] : mainPos[i];
            float w = isRow ? finalMain[i] : finalCross[i];
            float h = isRow ? finalCross[i] : finalMain[i];
            result.add(new ItemLayout(line.get(i).getId(), x, y, w, h));
        }
        return result;
    }

    private void computeMainPositions(float[] pos, float[] sizes, int n,
                                      float gap, float freeSpace, float container) {
        float offset;
        float extraGap;

        switch (justifyContent) {
            case FLEX_START -> { offset = 0; extraGap = 0; }
            case FLEX_END   -> { offset = Math.max(0, freeSpace); extraGap = 0; }
            case CENTER     -> { offset = Math.max(0, freeSpace / 2f); extraGap = 0; }
            case SPACE_BETWEEN -> {
                offset = 0;
                extraGap = n > 1 ? Math.max(0, freeSpace / (n - 1)) : 0;
            }
            case SPACE_AROUND -> {
                float sp = n > 0 ? Math.max(0, freeSpace / n) : 0;
                offset = sp / 2f;
                extraGap = sp;
            }
            case SPACE_EVENLY -> {
                float sp = n > 0 ? Math.max(0, freeSpace / (n + 1)) : 0;
                offset = sp;
                extraGap = sp;
            }
            default -> { offset = 0; extraGap = 0; }
        }

        for (int i = 0; i < n; i++) {
            pos[i] = offset;
            offset += sizes[i] + gap + extraGap;
        }
    }

    private float computeCrossPosition(FlexItem item, float childCross, float containerCross) {
        return switch (effectiveAlign(item)) {
            case FLEX_START, BASELINE, STRETCH -> 0f;
            case CENTER    -> (containerCross - childCross) / 2f;
            case FLEX_END  -> containerCross - childCross;
        };
    }

    private AlignItems effectiveAlign(FlexItem item) {
        return item.getAlignSelf() != null ? item.getAlignSelf() : alignItems;
    }

    // ── Reverse helper ────────────────────────────────────────────────────────

    private List<ItemLayout> reverseMainAxis(List<ItemLayout> layouts,
                                             float mainContainer, boolean isRow) {
        List<ItemLayout> reversed = new ArrayList<>(layouts.size());
        for (ItemLayout il : layouts) {
            float newMain = mainContainer - (isRow ? il.getX() : il.getY())
                    - (isRow ? il.getWidth() : il.getHeight());
            float x = isRow ? newMain : il.getX();
            float y = isRow ? il.getY() : newMain;
            reversed.add(new ItemLayout(il.getId(), x, y, il.getWidth(), il.getHeight()));
        }
        return reversed;
    }

    // ─── Measure helpers (called from FlexContainer.measureContent) ───────────

    /**
     * Calculate natural (unwrapped) content size for a flex container given its children's sizes.
     */
    public float measureMainAxis(List<Float> childMainSizes) {
        float total = 0;
        for (float s : childMainSizes) total += s;
        if (!childMainSizes.isEmpty()) {
            total += gap * (childMainSizes.size() - 1);
        }
        return total;
    }

    public float measureCrossAxis(List<Float> childCrossSizes) {
        float max = 0;
        for (float s : childCrossSizes) max = Math.max(max, s);
        return max;
    }
}