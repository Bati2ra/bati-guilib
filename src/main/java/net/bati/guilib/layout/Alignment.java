package net.bati.guilib.layout;

/** Positional alignment within a parent's content area. */
public enum Alignment {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    MIDDLE_LEFT,
    MIDDLE_CENTER,
    MIDDLE_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT;

    public float resolveX(float parentW, float childW, float offsetX) {
        return switch (this) {
            case TOP_LEFT,    MIDDLE_LEFT,    BOTTOM_LEFT   -> offsetX;
            case TOP_CENTER,  MIDDLE_CENTER,  BOTTOM_CENTER -> (parentW - childW) / 2f + offsetX;
            case TOP_RIGHT,   MIDDLE_RIGHT,   BOTTOM_RIGHT  -> parentW - childW + offsetX;
        };
    }

    public float resolveY(float parentH, float childH, float offsetY) {
        return switch (this) {
            case TOP_LEFT,    TOP_CENTER,    TOP_RIGHT    -> offsetY;
            case MIDDLE_LEFT, MIDDLE_CENTER, MIDDLE_RIGHT -> (parentH - childH) / 2f + offsetY;
            case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> parentH - childH + offsetY;
        };
    }
}


