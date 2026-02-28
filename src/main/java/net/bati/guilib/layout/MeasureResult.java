package net.bati.guilib.layout;

/** Immutable result of the measure phase: natural border-box size. */
public record MeasureResult(float width, float height) {
    public static final MeasureResult ZERO = new MeasureResult(0, 0);
}


