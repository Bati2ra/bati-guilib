package net.bati.guilib.widget;

import lombok.Getter;
import lombok.Setter;
import net.bati.guilib.layout.ComputedLayout;
import net.bati.guilib.layout.FlexConstraints;
import net.bati.guilib.layout.flex.FlexLayout;
import net.bati.guilib.layout.LayoutConstraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container that uses Flexbox layout for its children.
 * Provides modern, web-like layout capabilities.
 */
@Getter
public class FlexContainer extends Widget {

    private FlexLayout flexLayout;

    public FlexContainer(String id) {
        super(id);
        flexLayout = FlexLayout.builder().build();
    }

    // ----------------------------------------------------------------
    //  Fluent configuration
    // ----------------------------------------------------------------

    public FlexContainer direction(FlexLayout.FlexDirection d) {
        flexLayout = flexLayout.toBuilder().direction(d).build();
        invalidateFlexLayout();
        return this;
    }

    public FlexContainer justifyContent(FlexLayout.JustifyContent j) {
        flexLayout = flexLayout.toBuilder().justifyContent(j).build();
        invalidateFlexLayout();
        return this;
    }

    public FlexContainer alignItems(FlexLayout.AlignItems a) {
        flexLayout = flexLayout.toBuilder().alignItems(a).build();
        invalidateFlexLayout();
        return this;
    }

    public FlexContainer gap(float gap) {
        flexLayout = flexLayout.toBuilder().gap(gap).build();
        invalidateFlexLayout();
        return this;
    }

    public FlexContainer wrap(FlexLayout.FlexWrap w) {
        flexLayout = flexLayout.toBuilder().wrap(w).build();
        invalidateFlexLayout();
        return this;
    }

    private void invalidateFlexLayout() {
        // triggers markDirty() via parent chain
        if (getComputedLayout() != null) getChildren().forEach(c -> {});
    }

    // ----------------------------------------------------------------
    //  Measure — natural size is the sum of children in flex direction
    // ----------------------------------------------------------------

    @Override
    protected Size measureContent(float availableWidth, float availableHeight) {

        if (getChildren().isEmpty()) return new Size(0, 0);

        boolean isRow = flexLayout.getDirection() == FlexLayout.FlexDirection.ROW;

        float main = 0;
        float cross = 0;

        for (Widget child : getChildren()) {
            Size s = child.measure(availableWidth, availableHeight);

            if (isRow) {
                main += s.width();
                cross = Math.max(cross, s.height());
            } else {
                main += s.height();
                cross = Math.max(cross, s.width());
            }
        }

        main += flexLayout.getGap() * Math.max(0, getChildren().size() - 1);

        return isRow ? new Size(main, cross) : new Size(cross, main);
    }

    @Override
    protected void layoutChildren(float scale, float opacity, int zIndex) {

        if (getComputedLayout() == null) return;

        ComputedLayout.Bounds content = getComputedLayout().getContentBounds();
        float availableW = content.getWidth() / scale;
        float availableH = content.getHeight() / scale;

        boolean isRow = flexLayout.getDirection() == FlexLayout.FlexDirection.ROW;

        // ✅ Paso 1: Resolver el tamaño FINAL de cada hijo (con constraints)
        List<FlexLayout.FlexItem> items = new ArrayList<>();
        for (Widget child : getChildren()) {
            // Medir tamaño natural
            Size natural = child.measure(availableW, availableH);

            // ✅ Aplicar constraints del hijo para obtener tamaño final
            float finalW = natural.width();
            float finalH = natural.height();

            if (child.getConstraints().getWidth() != null) {
                finalW = child.getConstraints().getWidth().resolve(availableW, finalW);
            }
            if (child.getConstraints().getHeight() != null) {
                finalH = child.getConstraints().getHeight().resolve(availableH, finalH);
            }

            // Flex usa el tamaño FINAL (post-constraints)
            items.add(new FlexLayout.FlexItem(
                    child.getId(),
                    isRow ? finalW : finalH,
                    isRow ? finalH : finalW,
                    child.getFlexConstraints().getFlexGrow(),
                    child.getFlexConstraints().getFlexShrink(),
                    child.getFlexConstraints().getAlignSelf()
            ));
        }

        // Paso 2: Flex calcula posiciones basándose en tamaños finales
        List<FlexLayout.ItemLayout> layouts = flexLayout.computeLayout(items, availableW, availableH);

        Map<String, FlexLayout.ItemLayout> map = new HashMap<>();
        layouts.forEach(l -> map.put(l.getId(), l));

        // Paso 3: Aplicar layout a cada hijo
        for (Widget child : getChildren()) {
            FlexLayout.ItemLayout il = map.get(child.getId());
            if (il == null) continue;

            child.computeLayoutAbsolute(
                    il.getWidth(),
                    il.getHeight(),
                    availableW,
                    availableH,
                    content.getX(),
                    content.getY(),
                    il.getX(),
                    il.getY(),
                    scale, opacity, zIndex
            );
        }
    }

    // ----------------------------------------------------------------
    //  Static factories
    // ----------------------------------------------------------------

    public static FlexContainer row(String id) {
        FlexContainer c = new FlexContainer(id);
        c.flexLayout = c.flexLayout.toBuilder()
                .direction(FlexLayout.FlexDirection.ROW)
                .build();
        return c;
    }

    public static FlexContainer column(String id) {
        return new FlexContainer(id).direction(FlexLayout.FlexDirection.COLUMN);
    }
}