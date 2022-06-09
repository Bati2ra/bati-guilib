package net.bati.guilib.gui.components;

import net.bati.guilib.gui.interfaces.*;
import net.bati.guilib.utils.PIVOT;
import net.bati.guilib.utils.font.TextComponent;

@SuppressWarnings("unchecked")
public class WidgetBuilder<T extends Widget> {
    private T widget;

    public WidgetBuilder(T type) {
        widget = type;
    }

    public WidgetBuilder<T> identifier(String name) {
        widget.setIdentifier(name);
        return this;
    }

    public WidgetBuilder<T> position(int x, int y) {
        widget.setX(x);
        widget.setY(y);
        return this;
    }

    public WidgetBuilder<T> box(int width, int height) {
        widget.setBoxWidth(width);
        widget.setBoxHeight(height);
        return this;
    }

    public WidgetBuilder<T> size(float s) {
        widget.setSize(s);
        return this;
    }

    public WidgetBuilder<T> opacity(float s) {
        widget.setOpacity(s);
        return this;
    }

    public WidgetBuilder<T> attachTo(PIVOT pivot) {
        widget.setAttachedTo(pivot);
        return this;
    }

    public WidgetBuilder<T> pivot(PIVOT pivot) {
        widget.setPivot(pivot);
        return this;
    }

    public WidgetBuilder<T> hover(HoverCallback callback) {
        widget.setHoverCallback(callback);
        return this;
    }

    public WidgetBuilder<T> draw(DrawableCallback callback) {
        widget.setDrawCallback(callback);
        return this;
    }

    public WidgetBuilder<T> preDraw(DrawableCallback callback) {
        widget.setPreDrawCallback(callback);
        return this;
    }

    public WidgetBuilder<T> postDraw(DrawableCallback callback) {
        widget.setPostDrawCallback(callback);
        return this;
    }

    public WidgetBuilder<T> onClick(MouseCallback callback) {
        widget.setClickCallback(callback);
        return this;
    }

    public WidgetBuilder<T> onReleaseClick(MouseCallback callback) {
        widget.setReleaseClickCallback(callback);
        return this;
    }

    public WidgetBuilder<T> onPressKey(PressableCallback callback) {
        widget.setKeyPressedCallback(callback);
        return this;
    }

    public WidgetBuilder<T> onReleaseKey(PressableCallback callback) {
        widget.setKeyReleasedCallback(callback);
        return this;
    }

    public WidgetBuilder<T> position(ScreenPositionCallback callback) {
        widget.setPositionCallback(callback);
        return this;
    }

    // <-- TextLabel Builder methods
    public WidgetBuilder<TextLabel> text(TextCallback callback) {
        ((TextLabel)widget).setTextCallback(callback);
        return (WidgetBuilder<TextLabel>) this;
    }

    public WidgetBuilder<TextLabel> component(TextComponent component) {
        ((TextLabel)widget).setTextComponent(component);
        return (WidgetBuilder<TextLabel>) this;
    }
    // -->

    public T build() {
        return widget;
    }


}
