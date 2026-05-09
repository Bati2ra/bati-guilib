package net.bati.miniui.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all event handler lists for a widget. Built to be easily extended.
 */
public final class EventHandlers {

    // ─── Handler functional interfaces ────────────────────────────────────────

    @FunctionalInterface public interface MouseClickHandler  { boolean handle(double x, double y, int button); }
    @FunctionalInterface public interface MouseMoveHandler   { void    handle(double x, double y); }
    @FunctionalInterface public interface MouseScrollHandler { boolean handle(double x, double y, double amount); }
    @FunctionalInterface public interface MouseDragHandler   { boolean handle(double x, double y, int button, double dx, double dy); }
    @FunctionalInterface public interface KeyHandler         { boolean handle(int keyCode, int scanCode, int modifiers); }
    @FunctionalInterface public interface CharTypedHandler   { boolean handle(char c, int modifiers); }
    @FunctionalInterface public interface HoverHandler       { void    handle(boolean hovered); }
    @FunctionalInterface public interface FocusHandler       { void    handle(boolean focused); }

    // ─── Handler lists ────────────────────────────────────────────────────────

    private final List<MouseClickHandler> clickHandlers   = new ArrayList<>(2);
    private final List<MouseClickHandler>  releaseHandlers = new ArrayList<>(2);
    private final List<MouseMoveHandler>   moveHandlers    = new ArrayList<>(2);
    private final List<MouseScrollHandler> scrollHandlers  = new ArrayList<>(2);
    private final List<MouseDragHandler>   dragHandlers    = new ArrayList<>(2);
    private final List<KeyHandler>         keyHandlers     = new ArrayList<>(2);
    private final List<CharTypedHandler>   charHandlers    = new ArrayList<>(2);
    private final List<HoverHandler>       hoverHandlers   = new ArrayList<>(2);
    private final List<FocusHandler>       focusHandlers   = new ArrayList<>(2);

    // ─── Registration helpers ─────────────────────────────────────────────────

    public EventHandlers onClick(MouseClickHandler h)   { clickHandlers.add(h);   return this; }
    public EventHandlers onRelease(MouseClickHandler h) { releaseHandlers.add(h); return this; }
    public EventHandlers onMouseMove(MouseMoveHandler h){ moveHandlers.add(h);    return this; }
    public EventHandlers onScroll(MouseScrollHandler h) { scrollHandlers.add(h);  return this; }
    public EventHandlers onDrag(MouseDragHandler h)     { dragHandlers.add(h);    return this; }
    public EventHandlers onKey(KeyHandler h)            { keyHandlers.add(h);     return this; }
    public EventHandlers onChar(CharTypedHandler h)     { charHandlers.add(h);    return this; }
    public EventHandlers onHover(HoverHandler h)        { hoverHandlers.add(h);   return this; }
    public EventHandlers onFocus(FocusHandler h)        { focusHandlers.add(h);   return this; }

    /** Shorthand: click handler that doesn't care about button. */
    public EventHandlers onClick(Runnable action) {
        return onClick((x, y, b) -> { action.run(); return true; });
    }

    // ─── Dispatch methods ─────────────────────────────────────────────────────

    public boolean fireClick(double x, double y, int button) {
        boolean handled = false;
        for (MouseClickHandler h : clickHandlers) if (h.handle(x, y, button)) handled = true;
        return handled;
    }

    public boolean fireRelease(double x, double y, int button) {
        boolean handled = false;
        for (MouseClickHandler h : releaseHandlers) if (h.handle(x, y, button)) handled = true;
        return handled;
    }

    public void fireMouseMove(double x, double y) {
        for (MouseMoveHandler h : moveHandlers) h.handle(x, y);
    }

    public boolean fireScroll(double x, double y, double amount) {
        boolean handled = false;
        for (MouseScrollHandler h : scrollHandlers) if (h.handle(x, y, amount)) handled = true;
        return handled;
    }

    public boolean fireDrag(double x, double y, int button, double dx, double dy) {
        boolean handled = false;
        for (MouseDragHandler h : dragHandlers) if (h.handle(x, y, button, dx, dy)) handled = true;
        return handled;
    }

    public boolean fireKey(int keyCode, int scanCode, int modifiers) {
        boolean handled = false;
        for (KeyHandler h : keyHandlers) if (h.handle(keyCode, scanCode, modifiers)) handled = true;
        return handled;
    }

    public boolean fireChar(char c, int modifiers) {
        boolean handled = false;
        for (CharTypedHandler h : charHandlers) if (h.handle(c, modifiers)) handled = true;
        return handled;
    }

    public void fireHover(boolean hovered) {
        for (HoverHandler h : hoverHandlers) h.handle(hovered);
    }

    public void fireFocus(boolean focused) {
        for (FocusHandler h : focusHandlers) h.handle(focused);
    }

    public boolean hasClickHandlers() { return !clickHandlers.isEmpty(); }

    /** Remove all click handlers. Useful for pool widgets that get rebound to new items. */
    public void clearClickHandlers() { clickHandlers.clear(); }

    /** Remove all handlers of all types. */
    public void clearAllHandlers() {
        clickHandlers.clear();
        releaseHandlers.clear();
        moveHandlers.clear();
        scrollHandlers.clear();
        dragHandlers.clear();
        keyHandlers.clear();
        charHandlers.clear();
        hoverHandlers.clear();
        focusHandlers.clear();
    }
}