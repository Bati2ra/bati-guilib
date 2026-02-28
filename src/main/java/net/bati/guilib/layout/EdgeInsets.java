package net.bati.guilib.layout;

/** Immutable four-sided insets (top, right, bottom, left). */
public final class EdgeInsets {

    public static final EdgeInsets ZERO = new EdgeInsets(0, 0, 0, 0);

    private final float top, right, bottom, left;

    private EdgeInsets(float top, float right, float bottom, float left) {
        this.top    = top;
        this.right  = right;
        this.bottom = bottom;
        this.left   = left;
    }

    public static EdgeInsets all(float value)                              { return new EdgeInsets(value, value, value, value); }
    public static EdgeInsets symmetric(float vertical, float horizontal)   { return new EdgeInsets(vertical, horizontal, vertical, horizontal); }
    public static EdgeInsets only(float top, float right, float bottom, float left) { return new EdgeInsets(top, right, bottom, left); }
    public static EdgeInsets horizontal(float h)                           { return new EdgeInsets(0, h, 0, h); }
    public static EdgeInsets vertical(float v)                             { return new EdgeInsets(v, 0, v, 0); }

    public float getTop()    { return top; }
    public float getRight()  { return right; }
    public float getBottom() { return bottom; }
    public float getLeft()   { return left; }

    public float horizontal() { return left + right; }
    public float vertical()   { return top + bottom; }
}


