package net.bati.miniui.layout;

/**
 * Describes how a widget resolves its size given the parent's available space
 * and its own natural (content) size.
 */
public abstract class SizeConstraint {

    private SizeConstraint() {}

    /** Resolve this constraint to an actual pixel value.
     *  @param parentSize   available space from the parent (border-box axis)
     *  @param contentSize  natural content size of the widget (border-box)
     */
    public abstract float resolve(float parentSize, float contentSize);

    // ─── Factory methods ──────────────────────────────────────────────────────

    /** Always this many pixels. */
    public static SizeConstraint fixed(float px) {
        return new Fixed(px);
    }

    /** Fraction of the parent's available space. */
    public static SizeConstraint percentage(float fraction) {
        return new Percentage(fraction);
    }

    /** Use the widget's natural content size. */
    public static SizeConstraint auto() {
        return Auto.INSTANCE;
    }

    /** Fill all available parent space. */
    public static SizeConstraint fillParent() {
        return FillParent.INSTANCE;
    }

    /** At least {@code min} pixels, but resolve {@code inner} otherwise. */
    public static SizeConstraint min(float min, SizeConstraint inner) {
        return new Min(min, inner);
    }

    /** At most {@code max} pixels, but resolve {@code inner} otherwise. */
    public static SizeConstraint max(float max, SizeConstraint inner) {
        return new Max(max, inner);
    }

    /** Clamp resolved {@code inner} to [{@code min}, {@code max}]. */
    public static SizeConstraint clamp(float min, float max, SizeConstraint inner) {
        return new Clamp(min, max, inner);
    }

    // ─── Implementations ──────────────────────────────────────────────────────

    private static final class Fixed extends SizeConstraint {
        private final float px;
        Fixed(float px) { this.px = px; }

        @Override public float resolve(float parentSize, float contentSize) { return px; }
        @Override public String toString() { return "fixed(" + px + ")"; }
    }

    private static final class Percentage extends SizeConstraint {
        private final float fraction;
        Percentage(float fraction) { this.fraction = fraction; }

        @Override public float resolve(float parentSize, float contentSize) { return parentSize * fraction; }
        @Override public String toString() { return "percentage(" + (fraction * 100) + "%)"; }
    }

    private static final class Auto extends SizeConstraint {
        static final Auto INSTANCE = new Auto();

        @Override public float resolve(float parentSize, float contentSize) { return contentSize; }
        @Override public String toString() { return "auto"; }
    }

    private static final class FillParent extends SizeConstraint {
        static final FillParent INSTANCE = new FillParent();

        @Override public float resolve(float parentSize, float contentSize) { return parentSize; }
        @Override public String toString() { return "fillParent"; }
    }

    private static final class Min extends SizeConstraint {
        private final float min;
        private final SizeConstraint inner;
        Min(float min, SizeConstraint inner) { this.min = min; this.inner = inner; }

        @Override public float resolve(float parentSize, float contentSize) {
            return Math.max(min, inner.resolve(parentSize, contentSize));
        }
        @Override public String toString() { return "min(" + min + ", " + inner + ")"; }
    }

    private static final class Max extends SizeConstraint {
        private final float max;
        private final SizeConstraint inner;
        Max(float max, SizeConstraint inner) { this.max = max; this.inner = inner; }

        @Override public float resolve(float parentSize, float contentSize) {
            return Math.min(max, inner.resolve(parentSize, contentSize));
        }
        @Override public String toString() { return "max(" + max + ", " + inner + ")"; }
    }

    private static final class Clamp extends SizeConstraint {
        private final float min, max;
        private final SizeConstraint inner;
        Clamp(float min, float max, SizeConstraint inner) {
            this.min = min; this.max = max; this.inner = inner;
        }

        @Override public float resolve(float parentSize, float contentSize) {
            return Math.max(min, Math.min(max, inner.resolve(parentSize, contentSize)));
        }
        @Override public String toString() { return "clamp(" + min + ", " + max + ", " + inner + ")"; }
    }
}