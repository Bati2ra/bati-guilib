package net.bati.guilib.layout.flex;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(toBuilder = true)
public class FlexLayout {

    @Builder.Default private final FlexDirection   direction      = FlexDirection.ROW;
    @Builder.Default private final JustifyContent  justifyContent = JustifyContent.FLEX_START;
    @Builder.Default private final AlignItems      alignItems     = AlignItems.FLEX_START;
    @Builder.Default private final FlexWrap        wrap           = FlexWrap.NO_WRAP;
    @Builder.Default private final float           gap            = 0;
    @Builder.Default private final float           rowGap         = 0;
    @Builder.Default private final float           columnGap      = 0;

    public enum FlexDirection  { ROW, ROW_REVERSE, COLUMN, COLUMN_REVERSE }
    public enum JustifyContent { FLEX_START, FLEX_END, CENTER, SPACE_BETWEEN, SPACE_AROUND, SPACE_EVENLY }
    public enum AlignItems     { FLEX_START, FLEX_END, CENTER, STRETCH, BASELINE }
    public enum FlexWrap       { NO_WRAP, WRAP, WRAP_REVERSE }

    public List<ItemLayout> computeLayout(List<FlexItem> items, float containerWidth, float containerHeight) {
        if (items.isEmpty()) return List.of();

        boolean isRow = direction == FlexDirection.ROW || direction == FlexDirection.ROW_REVERSE;
        float mainSize  = isRow ? containerWidth  : containerHeight;
        float crossSize = isRow ? containerHeight : containerWidth;
        float mainGap   = (isRow ? (columnGap > 0 ? columnGap : gap) : (rowGap > 0 ? rowGap : gap));

        List<ItemLayout> result = new ArrayList<>();

        // 1. Collect base sizes
        float totalBase = 0;
        int   totalGrow = 0, totalShrink = 0;
        for (FlexItem item : items) {
            totalBase   += isRow ? item.getMainSize()  : item.getMainSize();
            totalGrow   += item.getFlexGrow();
            totalShrink += item.getFlexShrink();
        }
        float totalGapSize  = mainGap * (items.size() - 1);
        float availableMain = mainSize - totalBase - totalGapSize;

        // 2. Resolve flex grow/shrink
        List<Float> finalSizes = new ArrayList<>();
        for (FlexItem item : items) {
            float base = item.getMainSize();
            if (availableMain > 0 && totalGrow > 0) {
                base += availableMain * ((float) item.getFlexGrow() / totalGrow);
            } else if (availableMain < 0 && totalShrink > 0) {
                base = Math.max(0, base + availableMain * ((float) item.getFlexShrink() / totalShrink));
            }
            finalSizes.add(base);
        }

        // 3. Justify content offset
        float totalFinalMain = finalSizes.stream().reduce(0f, Float::sum) + totalGapSize;
        float justifyOffset  = resolveJustifyOffset(justifyContent, items.size(), totalFinalMain, mainSize);
        float extraGap       = resolveExtraGap(justifyContent, items.size(), totalFinalMain, mainSize);

        // 4. Place items
        float cursor = justifyOffset;
        for (int i = 0; i < items.size(); i++) {
            FlexItem item     = items.get(i);
            float    mSize    = finalSizes.get(i);
            float    cSize    = resolveCrossSize(item, crossSize, alignItems);

            float crossOffset = resolveCrossOffset(item.getCrossSize(), cSize, crossSize,
                    item.getAlignSelf() != null ? item.getAlignSelf() : alignItems);

            float x = isRow ? cursor : crossOffset;
            float y = isRow ? crossOffset : cursor;
            float w = isRow ? mSize  : cSize;
            float h = isRow ? cSize  : mSize;

            result.add(new ItemLayout(item.getId(), x, y, w, h));
            cursor += mSize + mainGap + (i < items.size() - 1 ? extraGap : 0);
        }

        return result;
    }

    private float resolveJustifyOffset(JustifyContent jc, int count, float total, float container) {
        float free = container - total;
        return switch (jc) {
            case FLEX_END      -> free;
            case CENTER        -> free / 2;
            case SPACE_EVENLY  -> free / (count + 1);
            case SPACE_AROUND  -> count > 0 ? free / count / 2 : 0;
            default            -> 0;
        };
    }

    private float resolveExtraGap(JustifyContent jc, int count, float total, float container) {
        float free = container - total;
        return switch (jc) {
            case SPACE_BETWEEN -> count > 1 ? free / (count - 1) : 0;
            case SPACE_AROUND  -> count > 0 ? free / count : 0;
            case SPACE_EVENLY  -> count > 0 ? free / (count + 1) : 0;
            default            -> 0;
        };
    }

    private float resolveCrossSize(FlexItem item, float crossSize, AlignItems align) {
        AlignItems effective = item.getAlignSelf() != null ? item.getAlignSelf() : align;
        return effective == AlignItems.STRETCH ? crossSize : item.getCrossSize();
    }

    private float resolveCrossOffset(float itemCross, float resolvedCross, float containerCross, AlignItems align) {
        return switch (align) {
            case FLEX_END -> containerCross - resolvedCross;
            case CENTER   -> (containerCross - resolvedCross) / 2;
            default       -> 0;
        };
    }

    // ----------------------------------------------------------------

    @Getter
    public static final class FlexItem {
        private final String     id;
        private final float      mainSize, crossSize;
        private final int        flexGrow, flexShrink;
        private final AlignItems alignSelf; // null = inherit parent

        public FlexItem(String id, float mainSize, float crossSize,
                        int flexGrow, int flexShrink, AlignItems alignSelf) {
            this.id = id; this.mainSize = mainSize; this.crossSize = crossSize;
            this.flexGrow = flexGrow; this.flexShrink = flexShrink;
            this.alignSelf = alignSelf;
        }
    }

    @Getter
    public static final class ItemLayout {
        private final String id;
        private final float  x, y, width, height;

        public ItemLayout(String id, float x, float y, float width, float height) {
            this.id = id; this.x = x; this.y = y;
            this.width = width; this.height = height;
        }
    }
}