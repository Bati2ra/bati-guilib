package net.bati.guilib.layout;

import lombok.Builder;
import lombok.Getter;

/**
 * Defines how a widget positions and sizes itself within its parent.
 * Purely static layout — flex properties live in FlexConstraints.
 */
@Getter
@Builder(toBuilder = true)
public class LayoutConstraints {

    @Builder.Default private final Alignment     alignment    = Alignment.TOP_LEFT;
    @Builder.Default private final PositionType  positionType = PositionType.RELATIVE;
    @Builder.Default private final float         offsetX      = 0;
    @Builder.Default private final float         offsetY      = 0;
    @Builder.Default private final int           zIndex       = 0;

    // null = defer to measured content size
    private final SizeConstraint width;
    private final SizeConstraint height;

    public LayoutConstraints withOffset(float x, float y) {
        return toBuilder().offsetX(x).offsetY(y).build();
    }

    public enum PositionType { RELATIVE, ABSOLUTE, FIXED }

    public enum Alignment {
        TOP_LEFT,    TOP_CENTER,    TOP_RIGHT,
        MIDDLE_LEFT, MIDDLE_CENTER, MIDDLE_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT,
        // Aliases
        LEFT, CENTER, RIGHT, TOP, MIDDLE, BOTTOM,
        AUTO, STRETCH
    }

    @FunctionalInterface
    public interface SizeConstraint {
        float resolve(float parentSize, float contentSize);

        static SizeConstraint fixed(float size)              { return (p, c) -> size; }
        static SizeConstraint percentage(float pct)          { return (p, c) -> p * pct; }
        static SizeConstraint auto()                         { return (p, c) -> c; }
        static SizeConstraint fillParent()                   { return (p, c) -> p; }
        static SizeConstraint min(float min, SizeConstraint inner) {
            return (p, c) -> Math.max(min, inner.resolve(p, c));
        }
        static SizeConstraint max(float max, SizeConstraint inner) {
            return (p, c) -> Math.min(max, inner.resolve(p, c));
        }
        static SizeConstraint clamp(float min, float max, SizeConstraint inner) {
            return (p, c) -> Math.max(min, Math.min(max, inner.resolve(p, c)));
        }
    }
}