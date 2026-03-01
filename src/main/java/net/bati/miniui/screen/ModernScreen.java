package net.bati.miniui.screen;

import net.bati.miniui.layout.LayoutPassInfo;
import net.bati.miniui.rendering.RenderPassInfo;
import net.bati.miniui.widget.Widget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * A Minecraft {@link Screen} that manages a {@link Widget} tree.
 *
 * <p>Usage:
 * <pre>{@code
 * ModernScreen screen = new ModernScreen(Text.literal("My Screen"));
 * screen.setRoot(myFlexContainer);
 * client.setScreen(screen);
 * }</pre>
 */
public class ModernScreen extends Screen {

    private @Nullable Widget root;
    private @Nullable Widget hoveredWidget;
    private @Nullable Widget focusedWidget;

    // Track screen dimensions for dirty detection
    private int lastWidth  = -1;
    private int lastHeight = -1;

    public ModernScreen(Component title) {
        super(title);
    }

    // ─── Root management ──────────────────────────────────────────────────────

    public ModernScreen setRoot(Widget widget) {
        this.root = widget;
        return this;
    }

    public @Nullable Widget getRoot() { return root; }

    // ─── Screen lifecycle ─────────────────────────────────────────────────────

    @Override
    protected void init() {
        lastWidth  = -1; // force re-layout
        lastHeight = -1;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (root == null) return;

        // Re-layout when dirty or screen resized
        if (root.isLayoutDirty() || width != lastWidth || height != lastHeight) {
            doLayout();
            lastWidth  = width;
            lastHeight = height;
        }

        // Update hover state
        updateHoveredWidget((float)mouseX, (float)mouseY);

        // Render
        RenderPassInfo rp = new RenderPassInfo(context, mouseX, mouseY, delta);
        root.render(rp);
    }

    private void doLayout() {
        if (root == null) return;
        LayoutPassInfo pass = LayoutPassInfo.root(width, height);
        root.layout(pass);
    }

    // ─── Hover tracking ───────────────────────────────────────────────────────

    private void updateHoveredWidget(float mx, float my) {
        if (root == null) return;

        Widget newHovered = findDeepestHovered(root, mx, my);

        if (newHovered != hoveredWidget) {
            if (hoveredWidget != null) hoveredWidget.setHovered(false);
            hoveredWidget = newHovered;
            if (hoveredWidget != null) hoveredWidget.setHovered(true);
        }
    }

    private @Nullable Widget findDeepestHovered(Widget node, float mx, float my) {
        if (!node.isVisible() || !node.isEnabled()) return null;
        if (!node.isUnderMouse(mx, my)) return null;

        Widget best  = null;
        int    bestZ = Integer.MIN_VALUE;

        for (Widget child : node.getChildren()) {
            Widget candidate = findDeepestHovered(child, mx, my);
            if (candidate != null) {
                var cl = candidate.getComputedLayout();
                int z  = cl != null ? cl.getComputedZIndex() : 0;
                if (z >= bestZ) {
                    bestZ = z;
                    best  = candidate;
                }
            }
        }

        return best != null ? best : node;
    }

    // ─── Input forwarding ─────────────────────────────────────────────────────


    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (root == null) return false;

        // Clear old focus
        if (focusedWidget != null) {
            focusedWidget.setFocused(false);
            focusedWidget = null;
        }

        boolean handled = root.mouseClicked(mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.button());

        // Newly focused widget (whichever claimed the click)
        if (hoveredWidget != null && handled) {
            focusedWidget = hoveredWidget;
            focusedWidget.setFocused(true);
        }

        return handled ||super.mouseClicked(mouseButtonEvent, bl);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        if (root == null) return false;
        return root.mouseReleased(mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.button()) || super.mouseReleased(mouseButtonEvent);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double d, double e) {
        if (root == null) return false;
        return root.mouseDragged(mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.button(), d, e) || super.mouseDragged(mouseButtonEvent, d, e);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hAmount, double vAmount) {
        if (root == null) return false;
        return root.mouseScrolled(mx, my, vAmount) || super.mouseScrolled(mx, my, hAmount, vAmount);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (root != null && root.keyPressed(keyEvent.key(), keyEvent.scancode(), keyEvent.modifiers())) return true;
        return super.keyPressed(keyEvent);
    }
/*
    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        if (root != null && root.charTyped(characterEvent.codepoint(), characterEvent.modifiers())) return true;
        return super.charTyped(characterEvent);
    }*/

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
