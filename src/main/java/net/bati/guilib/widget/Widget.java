package net.bati.guilib.widget;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.bati.guilib.layout.*;
import net.bati.guilib.rendering.NineSlice;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public abstract class Widget {

    // === IDENTITY ===
    private final String id;

    // === HIERARCHY ===
    private Widget parent;
    private final List<Widget> children = new ArrayList<>();

    // === LAYOUT ===
    @Setter(AccessLevel.NONE)
    private BoxModel boxModel;

    private LayoutConstraints constraints;
    private FlexConstraints   flexConstraints;

    // Set exclusively by the layout engine — never call directly
    @Getter @Setter(AccessLevel.PACKAGE)
    private ComputedLayout computedLayout;

    private boolean layoutDirty = true;

    // === STATE ===
    private boolean     enabled = true;
    private boolean     visible = true;
    private WidgetState state   = WidgetState.IDLE;

    // === RENDERING ===
    private NineSlice background;
    private int       backgroundColor = 0;

    // === EVENTS ===
    private final WidgetEventHandlers eventHandlers = new WidgetEventHandlers();

    // === LIFECYCLE ===
    private boolean initialized = false;

    // ----------------------------------------------------------------

    protected Widget(String id) {
        this.id              = id;
        this.boxModel        = BoxModel.builder().build();
        this.constraints     = LayoutConstraints.builder().build();
        this.flexConstraints = FlexConstraints.builder().build();
    }

    // ================================================================
    // HIERARCHY
    // ================================================================

    public void addChild(Widget child) {
        if (children.contains(child)) return;
        if (child.parent != null) child.parent.removeChild(child);
        children.add(child);
        child.parent = this;
        if (computedLayout != null) {
            applyLayoutToNewChild(child);
        }
        markDirty();
    }

    public void removeChild(Widget child) {
        if (children.remove(child)) {
            child.parent = null;
            markDirty();
        }
    }

    public Optional<Widget> findChild(String id) {
        for (Widget child : children) {
            if (child.id.equals(id)) return Optional.of(child);
            Optional<Widget> found = child.findChild(id);
            if (found.isPresent()) return found;
        }
        return Optional.empty();
    }

    // ================================================================
    // LAYOUT API  (public setters invalidate layout)
    // ================================================================

    public void setContentSize(float width, float height) {
        boxModel = boxModel.withContentWidth(width).withContentHeight(height);
        markDirty();
    }

    public void setPadding(BoxModel.Insets padding) {
        boxModel = boxModel.withPadding(padding);
        markDirty();
    }

    public void setMargin(BoxModel.Insets margin) {
        boxModel = boxModel.withMargin(margin);
        markDirty();
    }

    public void setBorder(BoxModel.Insets border) {
        boxModel = boxModel.withBorder(border);
        markDirty();
    }

    public void setConstraints(LayoutConstraints constraints) {
        this.constraints = constraints;
        markDirty();
    }

    public void setFlexConstraints(FlexConstraints fc) {
        this.flexConstraints = fc;
        markDirty();
    }
    void applyFlexSize(float width, float height) {
        this.boxModel = boxModel.withContentWidth(width).withContentHeight(height);
        // intentionally no markDirty()
    }
    /**
     * Propagates dirty flag up to the root so ModernScreen knows
     * a layout pass is needed next frame.
     */
    private void markDirty() {
        layoutDirty = true;
        if (parent != null) parent.markDirty();
    }

    public boolean isLayoutDirty() { return layoutDirty; }

    // ================================================================
    // LAYOUT ENGINE  (called exclusively by the layout engine)
    // ================================================================

    /**
     * Phase 1 — Measure.
     * Returns the natural (content) size of this widget.
     * Subclasses override to report text width, image size, etc.
     */
    protected Size measureContent(float availableWidth, float availableHeight) {
        return new Size(boxModel.getContentWidth(), boxModel.getContentHeight());
    }

    public final Size measure(float availableWidth, float availableHeight) {

        Size content = measureContent(availableWidth, availableHeight);

        float contentW = content.width();
        float contentH = content.height();

        BoxModel measuredModel = boxModel
                .withContentWidth(contentW)
                .withContentHeight(contentH);

        return new Size(
                measuredModel.getTotalWidth(),
                measuredModel.getTotalHeight()
        );
    }

    /**
     * Phase 2 — Layout.
     *
     * Called top-down by the parent. The parent passes:
     *   parentWidth/Height  → available space inside parent's content area
     *   parentScreenX/Y     → absolute screen position of parent's content area
     *   inheritedScale      → cumulative scale from ancestor chain
     *   inheritedOpacity    → cumulative opacity from ancestor chain
     *   inheritedZ          → cumulative z-index from ancestor chain
     *
     * This method:
     *   1. Resolves final width/height (constraints + measure)
     *   2. Resolves position relative to the parent's content area
     *   3. Converts to absolute screen coordinates
     *   4. Builds an immutable ComputedLayout
     *   5. Calls layoutChildren() with the content area coords
     */

    // --- Reemplazar computeLayout existente por esto ---
    public void computeLayout(
            float assignedWidth,
            float assignedHeight,
            float parentAvailableWidth,
            float parentAvailableHeight,
            float parentScreenX,
            float parentScreenY,
            float scale,
            float opacity,
            int zIndex
    ) {
        // ------------------------------------------------------------
        // 1) Measure natural size (border-box natural)
        // ------------------------------------------------------------

        Size measured = measure(parentAvailableWidth, parentAvailableHeight);

        float borderBoxW = assignedWidth;
        float borderBoxH = assignedHeight;

        // Si el padre no asigna tamaño explícito → usar natural
        if (borderBoxW <= 0) borderBoxW = measured.width();
        if (borderBoxH <= 0) borderBoxH = measured.height();

        // ------------------------------------------------------------
        // 2) Resolve SizeConstraints (FIX CRÍTICO)
        // ------------------------------------------------------------

        if (constraints.getWidth() != null) {
            borderBoxW = constraints.getWidth().resolve(parentAvailableWidth, borderBoxW);
        }

        if (constraints.getHeight() != null) {
            borderBoxH = constraints.getHeight().resolve(parentAvailableHeight, borderBoxH);
        }

        // ------------------------------------------------------------
        // 3) Derive content size from FINAL border-box
        // ------------------------------------------------------------

        float contentW = Math.max(0,
                borderBoxW
                        - boxModel.getPadding().horizontal()
                        - boxModel.getBorder().horizontal()
        );

        float contentH = Math.max(0,
                borderBoxH
                        - boxModel.getPadding().vertical()
                        - boxModel.getBorder().vertical()
        );

        boxModel = boxModel.withContentWidth(contentW)
                .withContentHeight(contentH);

        // ------------------------------------------------------------
        // 4) Resolve LOCAL position against PARENT SPACE (correct)
        // ------------------------------------------------------------

        float localX = resolveLocalX(parentAvailableWidth, borderBoxW);
        float localY = resolveLocalY(parentAvailableHeight, borderBoxH);

        float screenX = parentScreenX + localX * scale;
        float screenY = parentScreenY + localY * scale;

        // ------------------------------------------------------------
        // 5) Build computed layout
        // ------------------------------------------------------------

        computedLayout = ComputedLayout.create(
                screenX,
                screenY,
                borderBoxW,
                borderBoxH,
                boxModel,
                scale,
                opacity,
                zIndex + constraints.getZIndex()
        );

        layoutDirty = false;

        layoutChildren(scale, opacity, zIndex + constraints.getZIndex());
    }

// --- Nuevo método: computeLayoutAbsolute (usado por contenedores que colocan hijos) ---
    /**
     * Variante que permite forzar la posición local del hijo dentro del área de contenido del padre.
     * - assignedWidth/Height: border-box para el hijo (unscaled)
     * - parentAvailableWidth/Height: espacio disponible del padre (unscaled)
     * - parentScreenX/Y: coordenada absoluta del origen del content area del padre (screen coords)
     * - forcedLocalX/Y: posición local (unscaled) dentro del parent content area
     */
    public void computeLayoutAbsolute(
            float assignedWidth,
            float assignedHeight,
            float parentAvailableWidth,
            float parentAvailableHeight,
            float parentScreenX,
            float parentScreenY,
            float forcedLocalX,
            float forcedLocalY,
            float scale,
            float opacity,
            int zIndex
    ) {
        // Measure natural
        Size measured = measure(parentAvailableWidth, parentAvailableHeight);

        float borderBoxW = assignedWidth > 0 ? assignedWidth : measured.width();
        float borderBoxH = assignedHeight > 0 ? assignedHeight : measured.height();

        // ✅ Aplicar constraints sobre el assignedWidth (para fillParent, etc.)
        if (constraints.getWidth() != null) {
            borderBoxW = constraints.getWidth().resolve(parentAvailableWidth, borderBoxW);
        }
        if (constraints.getHeight() != null) {
            borderBoxH = constraints.getHeight().resolve(parentAvailableHeight, borderBoxH);
        }

        // Derive content
        float contentW = Math.max(0, borderBoxW
                - boxModel.getPadding().horizontal()
                - boxModel.getBorder().horizontal());
        float contentH = Math.max(0, borderBoxH
                - boxModel.getPadding().vertical()
                - boxModel.getBorder().vertical());

        boxModel = boxModel.withContentWidth(contentW).withContentHeight(contentH);

        // ✅ CRÍTICO: usar forcedLocalX/Y sin modificar por alignment
        // Flex ya calculó la posición correcta teniendo en cuenta justifyContent/alignItems
        float screenX = parentScreenX + forcedLocalX * scale;
        float screenY = parentScreenY + forcedLocalY * scale;

        computedLayout = ComputedLayout.create(
                screenX, screenY,
                borderBoxW, borderBoxH,
                boxModel, scale, opacity,
                zIndex + constraints.getZIndex()
        );

        layoutDirty = false;
        layoutChildren(scale, opacity, zIndex + constraints.getZIndex());
    }

    protected void layoutChildren(float scale, float opacity, int zIndex) {
        if (computedLayout == null || children.isEmpty()) return;

        ComputedLayout.Bounds content = computedLayout.getContentBounds();

        // parentAvailableWidth/Height deben ser en unidades "unscaled"
        float parentAvailableW = content.getWidth() / scale;
        float parentAvailableH = content.getHeight() / scale;

        for (Widget child : children) {
            // Child decide su tamaño natural (measure), en espacio unscaled
            Widget.Size measured = child.measure(parentAvailableW, parentAvailableH);

            // Assigned width/height son los border-box que medimos (total)
            float assignedW = measured.width();
            float assignedH = measured.height();

            child.computeLayout(
                    assignedW,
                    assignedH,
                    parentAvailableW,
                    parentAvailableH,
                    content.getX(),
                    content.getY(),
                    scale,
                    opacity,
                    zIndex
            );
        }
    }
    private void applyLayoutToNewChild(Widget child) {
        ComputedLayout.Bounds content = computedLayout.getContentBounds();
        float scale = computedLayout.getScale();

        Size measured = child.measure(
                content.getWidth() / scale,
                content.getHeight() / scale
        );

        child.computeLayout(
                measured.width(),
                measured.height(),
                content.getWidth() / scale,
                content.getHeight() / scale,
                content.getX(),
                content.getY(),
                scale,
                computedLayout.getOpacity(),
                computedLayout.getComputedZIndex()
        );

        child.init();
    }
    // ----------------------------------------------------------------

    private float resolveLocalX(float parentWidth, float selfWidth) {
        float offset = constraints.getOffsetX();

        return switch (constraints.getAlignment()) {
            case TOP_CENTER, MIDDLE_CENTER, BOTTOM_CENTER, CENTER ->
                    (parentWidth - selfWidth) / 2f + offset;

            case TOP_RIGHT, MIDDLE_RIGHT, BOTTOM_RIGHT, RIGHT ->
                    parentWidth - selfWidth + offset;

            default -> offset;
        };
    }

    private float resolveLocalY(float parentHeight, float selfHeight) {
        float offset = constraints.getOffsetY();

        return switch (constraints.getAlignment()) {
            case MIDDLE_LEFT, MIDDLE_CENTER, MIDDLE_RIGHT, MIDDLE ->
                    (parentHeight - selfHeight) / 2f + offset;

            case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT, BOTTOM ->
                    parentHeight - selfHeight + offset;

            default -> offset;
        };
    }

    // ================================================================
    // STATE
    // ================================================================

    /** Called by ModernScreen — sets HOVERED or IDLE without overriding PRESSED. */
    public void setHovered(boolean hovered) {
        if (!enabled) { state = WidgetState.DISABLED; return; }
        if (state == WidgetState.PRESSED) return;
        state = hovered ? WidgetState.HOVERED : WidgetState.IDLE;
    }

    public boolean isHovered(float mouseX, float mouseY) {
        return visible && enabled && computedLayout != null
                && computedLayout.contains(mouseX, mouseY);
    }

    // ================================================================
    // RENDER
    // ================================================================

    public final void render(GuiGraphics gfx, float mouseX, float mouseY, float delta) {
        if (!visible || computedLayout == null) return;

        Matrix3x2fStack pose = gfx.pose();
        pose.pushMatrix();

        applyTransformations(pose);
        renderBackground(gfx);
        renderContent(gfx, mouseX, mouseY, delta);
        renderChildren(gfx, mouseX, mouseY, delta);
        renderForeground(gfx);

        pose.popMatrix();

        if (shouldShowDebugBounds()) renderDebugBounds(gfx);
    }

    protected void applyTransformations(Matrix3x2fStack pose) {
        if (computedLayout == null || computedLayout.getScale() == 1.0f) return;
        float cx = computedLayout.getScreenX() + computedLayout.getWidth()  / 2;
        float cy = computedLayout.getScreenY() + computedLayout.getHeight() / 2;
        pose.translate(cx, cy);
        pose.scale(computedLayout.getScale(), computedLayout.getScale());
        pose.translate(-cx, -cy);
    }

    protected void renderBackground(GuiGraphics gfx) {
        if (computedLayout == null) return;
        if (background != null) {
            background.render(gfx,
                    computedLayout.getScreenX(), computedLayout.getScreenY(),
                    computedLayout.getWidth(),   computedLayout.getHeight(),
                    computedLayout.getOpacity());
        } else if (backgroundColor != 0) {
            gfx.fill(
                    (int) computedLayout.getScreenX(),
                    (int) computedLayout.getScreenY(),
                    (int)(computedLayout.getScreenX() + computedLayout.getWidth()),
                    (int)(computedLayout.getScreenY() + computedLayout.getHeight()),
                    backgroundColor);
        }
    }

    protected void renderContent(GuiGraphics gfx, float mouseX, float mouseY, float delta) {}

    protected void renderChildren(GuiGraphics gfx, float mouseX, float mouseY, float delta) {
        for (Widget child : children) child.render(gfx, mouseX, mouseY, delta);
    }

    protected void renderForeground(GuiGraphics gfx) {}

    protected boolean shouldShowDebugBounds() { return true; }

    protected void renderDebugBounds(GuiGraphics gfx) {
        if (computedLayout == null) return;

        int x = (int) computedLayout.getScreenX(), y = (int) computedLayout.getScreenY();
        int w = (int) computedLayout.getWidth(),   h = (int) computedLayout.getHeight();

        if(w == 0 || h == 0) return;

        int c = 0xFF00FF00, t = 1;
        gfx.fill(x,         y,         x + w,     y + t,     c);
        gfx.fill(x,         y + h - t, x + w,     y + h,     c);
        gfx.fill(x,         y,         x + t,     y + h,     c);
        gfx.fill(x + w - t, y,         x + w,     y + h,     c);
    }

    // ================================================================
    // EVENTS
    // ================================================================

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!enabled || !visible || computedLayout == null) return false;

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseClicked(mouseX, mouseY, button)) return true;
        }

        if (state == WidgetState.HOVERED) {
            state = WidgetState.PRESSED;
            return eventHandlers.onClick((float) mouseX, (float) mouseY, button, this);
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!enabled || !visible) return false;

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseReleased(mouseX, mouseY, button)) return true;
        }

        if (state == WidgetState.PRESSED) {
            state = isHovered((float) mouseX, (float) mouseY) ? WidgetState.HOVERED : WidgetState.IDLE;
            return eventHandlers.onRelease((float) mouseX, (float) mouseY, button, this);
        }
        return false;
    }

    // ================================================================
    // LIFECYCLE
    // ================================================================

    public final void init() {
        if (!initialized) {
            onInit();
            children.forEach(Widget::init);
            initialized = true;
        }
    }

    protected void onInit() {}

    // ================================================================
    // HELPERS
    // ================================================================

    public record Size(float width, float height) {}

    public enum WidgetState { IDLE, HOVERED, PRESSED, FOCUSED, DISABLED }
}