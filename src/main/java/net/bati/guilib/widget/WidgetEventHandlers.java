package net.bati.guilib.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Clean, type-safe event handling system for widgets.
 * Separates event logic from widget state.
 */
public class WidgetEventHandlers {

    private final List<MouseClickHandler> clickHandlers = new ArrayList<>();
    private final List<MouseReleaseHandler> releaseHandlers = new ArrayList<>();
    private final List<MouseMoveHandler> moveHandlers = new ArrayList<>();
    private final List<MouseDragHandler> dragHandlers = new ArrayList<>();
    private final List<MouseScrollHandler> scrollHandlers = new ArrayList<>();
    private final List<KeyPressHandler> keyPressHandlers = new ArrayList<>();
    private final List<KeyReleaseHandler> keyReleaseHandlers = new ArrayList<>();
    private final List<StateChangeHandler> stateChangeHandlers = new ArrayList<>();

    // === REGISTRATION ===

    public void addClickHandler(MouseClickHandler handler) {
        clickHandlers.add(handler);
    }

    public void addReleaseHandler(MouseReleaseHandler handler) {
        releaseHandlers.add(handler);
    }

    public void addMoveHandler(MouseMoveHandler handler) {
        moveHandlers.add(handler);
    }

    public void addDragHandler(MouseDragHandler handler) {
        dragHandlers.add(handler);
    }

    public void addScrollHandler(MouseScrollHandler handler) {
        scrollHandlers.add(handler);
    }

    public void addKeyPressHandler(KeyPressHandler handler) {
        keyPressHandlers.add(handler);
    }

    public void addKeyReleaseHandler(KeyReleaseHandler handler) {
        keyReleaseHandlers.add(handler);
    }

    public void addStateChangeHandler(StateChangeHandler handler) {
        stateChangeHandlers.add(handler);
    }

    // === CONVENIENCE METHODS ===

    public void onClick(Consumer<Widget> action) {
        addClickHandler((x, y, button, widget) -> {
            action.accept(widget);
            return true;
        });
    }

    public void onHover(Consumer<Widget> action) {
        addStateChangeHandler((oldState, newState, widget) -> {
            if (newState == Widget.WidgetState.HOVERED && oldState != Widget.WidgetState.HOVERED) {
                action.accept(widget);
            }
        });
    }

    public void onUnhover(Consumer<Widget> action) {
        addStateChangeHandler((oldState, newState, widget) -> {
            if (oldState == Widget.WidgetState.HOVERED && newState != Widget.WidgetState.HOVERED) {
                action.accept(widget);
            }
        });
    }

    // === EVENT FIRING ===

    public boolean onClick(float mouseX, float mouseY, int button, Widget widget) {
        boolean handled = false;
        for (MouseClickHandler handler : clickHandlers) {
            if (handler.handle(mouseX, mouseY, button, widget)) {
                handled = true;
            }
        }
        return handled || !clickHandlers.isEmpty();
    }

    public boolean onRelease(float mouseX, float mouseY, int button, Widget widget) {
        boolean handled = false;
        for (MouseReleaseHandler handler : releaseHandlers) {
            if (handler.handle(mouseX, mouseY, button, widget)) {
                handled = true;
            }
        }
        return handled || !releaseHandlers.isEmpty();
    }

    public boolean onMove(float mouseX, float mouseY, Widget widget) {
        boolean handled = false;
        for (MouseMoveHandler handler : moveHandlers) {
            if (handler.handle(mouseX, mouseY, widget)) {
                handled = true;
            }
        }
        return handled;
    }

    public boolean onDrag(float mouseX, float mouseY, int button, float deltaX, float deltaY, Widget widget) {
        boolean handled = false;
        for (MouseDragHandler handler : dragHandlers) {
            if (handler.handle(mouseX, mouseY, button, deltaX, deltaY, widget)) {
                handled = true;
            }
        }
        return handled;
    }

    public boolean onScroll(float mouseX, float mouseY, float amount, Widget widget) {
        boolean handled = false;
        for (MouseScrollHandler handler : scrollHandlers) {
            if (handler.handle(mouseX, mouseY, amount, widget)) {
                handled = true;
            }
        }
        return handled;
    }

    public boolean onKeyPress(int keyCode, int scanCode, int modifiers, Widget widget) {
        boolean handled = false;
        for (KeyPressHandler handler : keyPressHandlers) {
            if (handler.handle(keyCode, scanCode, modifiers, widget)) {
                handled = true;
            }
        }
        return handled;
    }

    public boolean onKeyRelease(int keyCode, int scanCode, int modifiers, Widget widget) {
        boolean handled = false;
        for (KeyReleaseHandler handler : keyReleaseHandlers) {
            if (handler.handle(keyCode, scanCode, modifiers, widget)) {
                handled = true;
            }
        }
        return handled;
    }

    public void onStateChange(Widget.WidgetState oldState, Widget.WidgetState newState, Widget widget) {
        for (StateChangeHandler handler : stateChangeHandlers) {
            handler.handle(oldState, newState, widget);
        }
    }

    // === HANDLER INTERFACES ===

    @FunctionalInterface
    public interface MouseClickHandler {
        boolean handle(float mouseX, float mouseY, int button, Widget widget);
    }

    @FunctionalInterface
    public interface MouseReleaseHandler {
        boolean handle(float mouseX, float mouseY, int button, Widget widget);
    }

    @FunctionalInterface
    public interface MouseMoveHandler {
        boolean handle(float mouseX, float mouseY, Widget widget);
    }

    @FunctionalInterface
    public interface MouseDragHandler {
        boolean handle(float mouseX, float mouseY, int button, float deltaX, float deltaY, Widget widget);
    }

    @FunctionalInterface
    public interface MouseScrollHandler {
        boolean handle(float mouseX, float mouseY, float amount, Widget widget);
    }

    @FunctionalInterface
    public interface KeyPressHandler {
        boolean handle(int keyCode, int scanCode, int modifiers, Widget widget);
    }

    @FunctionalInterface
    public interface KeyReleaseHandler {
        boolean handle(int keyCode, int scanCode, int modifiers, Widget widget);
    }

    @FunctionalInterface
    public interface StateChangeHandler {
        void handle(Widget.WidgetState oldState, Widget.WidgetState newState, Widget widget);
    }
}