package net.bati.guilib.utils.font;

import net.bati.guilib.utils.Vec2;

public class TextComponent {
    private String text = "";
    private int color = 16777215;
    private float size = 1.0F;

    private boolean outlined;
    private int lineColor = 16777215;

    private boolean shadow;
    private boolean centered = true;
    private Vec2 offsetPosition;
    private Font style;

    public TextComponent() {
    }

    public TextComponent text(String text) {
        this.text = text;
        return this;
    }

    public String getText() {
        return text;
    }

    public TextComponent color(int color) {
        this.color = color;
        return this;
    }
    public int getColor() {
        return color;
    }

    public TextComponent outlineColor(int color) {
        this.outlined = true;
        this.lineColor = color;
        return this;
    }
    public int getLineColor() {
        return lineColor;
    }

    public boolean isOutlined() {
        return outlined;
    }

    public TextComponent shadow() {
        this.shadow = true;
        return this;
    }

    public boolean hasShadow() {
        return shadow;
    }

    public TextComponent size(float s) {
        this.size = s;
        return this;
    }

    public float getSize() {
        return size;
    }

    public TextComponent offset(Vec2 vec) {
        this.centered = false;
        this.offsetPosition = vec;
        return this;
    }
    public Vec2 getOffsetPosition() {
        return offsetPosition;
    }

    public boolean isCentered() {
        return centered;
    }


    public TextComponent font(Font style) {
        this.style = style;
        return this;
    }

    public boolean hasStyle() {
        return getStyle() != null;
    }
    public Font getStyle() {
        return style;
    }



}
