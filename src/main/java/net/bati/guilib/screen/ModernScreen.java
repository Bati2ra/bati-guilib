package net.bati.guilib.screen;

import net.bati.guilib.layout.ComputedLayout;
import net.bati.guilib.layout.LayoutContext;
import net.bati.guilib.widget.Widget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Modernized screen base class with:
 * - Proper widget tree management
 * - Z-index based rendering
 * - Clean event propagation
 * - Layout caching
 */
public abstract class ModernScreen extends Screen {

    // Single source of truth: the widget tree.
    // widgetRegistry exists only for O(1) lookup by ID.
    private final ScreenRootWidget          rootWidget;
    private final Map<String, Widget>       widgetRegistry = new LinkedHashMap<>();

    private int    lastWidth  = -1;
    private int    lastHeight = -1;

    private Widget hoveredWidget = null;  // topmost widget under the mouse
    private Widget focusedWidget = null;  // widget that received the last click (keyboard focus)

    protected ModernScreen(@Nullable Component title) {
        super(title != null ? title : Component.literal(""));
        rootWidget = new ScreenRootWidget("root", this);
    }

    // ================================================================
    // Public API
    // ================================================================

    protected abstract void buildUI();

    public void addWidget(Widget widget) {
        if (widgetRegistry.containsKey(widget.getId()))
            throw new IllegalArgumentException("Duplicate widget id: " + widget.getId());
        widgetRegistry.put(widget.getId(), widget);
        rootWidget.addChild(widget);
    }

    public void removeWidget(String id) {
        Widget w = widgetRegistry.remove(id);
        if (w != null) rootWidget.removeChild(w);
    }

    /** Searches the entire widget tree (not just top-level). */
    public Optional<Widget> findWidget(String id) {
        return rootWidget.findChild(id);

    }

    // ================================================================
    // Lifecycle
    // ================================================================

    @Override
    protected void init() {
        super.init();
        if (widgetRegistry.isEmpty()) buildUI();
        rootWidget.init();
        // Force layout on next frame
        lastWidth = lastHeight = -1;
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float delta) {
        // Layout pass — only when something changed
        if (rootWidget.isLayoutDirty() || width != lastWidth || height != lastHeight) {
            computeLayout();
            lastWidth  = width;
            lastHeight = height;
        }

        updateHoveredWidget(mouseX, mouseY);
        renderBackground(gfx, mouseX, mouseY, delta);

        // Render top-level children in z-index order
        rootWidget.getChildren().stream()
                .sorted(Comparator.comparingInt(w ->
                        w.getComputedLayout() != null ? w.getComputedLayout().getComputedZIndex() : 0))
                .forEach(w -> w.render(gfx, mouseX, mouseY, delta));

        renderOverlays(gfx, mouseX, mouseY, delta);
        super.render(gfx, mouseX, mouseY, delta);
    }

    // ================================================================
    // Layout
    // ================================================================

    private void computeLayout() {
        rootWidget.computeLayout(
                width,              // assignedWidth for root = screen width (border box)
                height,             // assignedHeight for root
                width,              // parentAvailableWidth (root's parent available — screen)
                height,             // parentAvailableHeight
                0f,                 // parentScreenX
                0f,                 // parentScreenY
                1.0f,               // scale
                1.0f,               // opacity
                0                   // zIndex
        );
    }

    // ================================================================
    // Hover — find the deepest (most specific) hovered widget
    // ================================================================

    private void updateHoveredWidget(float mouseX, float mouseY) {
        Widget newHovered = findDeepestHovered(rootWidget, mouseX, mouseY);

        if (newHovered != hoveredWidget) {
            if (hoveredWidget != null) hoveredWidget.setHovered(false);
            hoveredWidget = newHovered;
            if (hoveredWidget != null) hoveredWidget.setHovered(true);
        }
    }

    /**
     * Recursively finds the deepest visible+enabled widget under the mouse.
     * Children take priority over their parent (more specific hit).
     * Among siblings at the same depth, highest z-index wins.
     */
    private Widget findDeepestHovered(Widget node, float mouseX, float mouseY) {
        if (node != rootWidget && (!node.isVisible() || !node.isEnabled())) return null;
        if (node != rootWidget && !node.isHovered(mouseX, mouseY)) return null;

        // Try children first (deepest / most specific)
        Widget best  = null;
        int    bestZ = Integer.MIN_VALUE;

        for (Widget child : node.getChildren()) {
            Widget candidate = findDeepestHovered(child, mouseX, mouseY);
            if (candidate != null) {
                int z = candidate.getComputedLayout() != null
                        ? candidate.getComputedLayout().getComputedZIndex() : 0;
                if (z >= bestZ) { bestZ = z; best = candidate; }
            }
        }

        // If a child matched, return it; otherwise return this node (if it's not root)
        if (best != null)        return best;
        if (node != rootWidget)  return node;
        return null;
    }

    // ================================================================
    // Hooks
    // ================================================================

    public void renderBackground(GuiGraphics gfx, int mouseX, int mouseY, float delta) {
        if (minecraft != null && minecraft.level != null)
            gfx.fillGradient(0, 0, width, height, 0xC0101010, 0xD0101010);
    }

    protected void renderOverlays(GuiGraphics gfx, int mouseX, int mouseY, float delta) {}

    // ================================================================
    // Events
    // ================================================================

    @Override
    public boolean mouseClicked(MouseButtonEvent e, boolean bl) {
        double mx = e.x(), my = e.y();
        int    btn = e.button();

        // Reverse z-order so topmost widget gets the event first
        List<Widget> sorted = rootWidget.getChildren().stream()
                .sorted(Comparator.comparingInt((Widget w) ->
                                w.getComputedLayout() != null ? w.getComputedLayout().getComputedZIndex() : 0)
                        .reversed())
                .collect(Collectors.toList());

        for (Widget w : sorted) {
            if (w.mouseClicked(mx, my, btn)) {
                focusedWidget = w;
                return true;
            }
        }
        focusedWidget = null;
        return super.mouseClicked(e, bl);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent e) {
        double mx = e.x(), my = e.y();
        int    btn = e.button();
        boolean handled = false;
        for (Widget w : rootWidget.getChildren()) {
            if (w.mouseReleased(mx, my, btn)) handled = true;
        }
        return handled || super.mouseReleased(e);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
        return focusedWidget != null || super.mouseDragged(e, dx, dy);
    }

    @Override
    public boolean keyPressed(KeyEvent e) {
        return super.keyPressed(e);
    }

    @Override
    public void removed() {
        super.removed();
        widgetRegistry.clear();
    }

    // ================================================================
    // Root widget — represents the screen itself
    // ================================================================

    private static final class ScreenRootWidget extends Widget {
        private final ModernScreen screen;

        ScreenRootWidget(String id, ModernScreen screen) {
            super(id);
            this.screen = screen;
        }

        @Override
        protected Size measureContent(float availableWidth, float availableHeight) {
            return new Size(screen.width, screen.height);
        }

        @Override
        protected void renderContent(GuiGraphics gfx, float mouseX, float mouseY, float delta) {}
    }
}
