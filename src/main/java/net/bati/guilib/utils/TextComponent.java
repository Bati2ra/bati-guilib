package net.bati.guilib.utils;

import net.minecraft.util.Identifier;

public class TextComponent {
    private String text = "";
    private int color = 16777215;
    private float size = 1.0F;
    private boolean outlined;
    private int lineColor;
    private boolean shadow;
    private boolean centered = true;
    private Vec2 offsetPosition;
    private Identifier style;

    public TextComponent() {
    }

    public TextComponent setText(String text) {
        this.text = text;
        return this;
    }

    public String getText() {
        return text;
    }

    public TextComponent setColor(int color) {
        this.color = color;
        return this;
    }
    public int getColor() {
        return color;
    }

    public TextComponent setLineColor(int color) {
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

    public TextComponent setShadow(boolean s) {
        this.shadow = s;
        return this;
    }

    public boolean hasShadow() {
        return shadow;
    }

    public TextComponent setSize(float s) {
        this.size = s;
        return this;
    }

    public float getSize() {
        return size;
    }

    public TextComponent setOffsetPosition(Vec2 vec) {
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

    public class Vec2 {
        private int x;
        private int y;

        public Vec2(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Vec2 getVec(int x, int y) {
            return new Vec2(x,y);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public void setFont(Identifier style) {
        this.style = style;
    }

    public Identifier getStyle() {
        return style;
    }
}
