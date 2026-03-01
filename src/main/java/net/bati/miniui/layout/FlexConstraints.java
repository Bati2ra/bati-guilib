package net.bati.miniui.layout;

import net.bati.miniui.layout.flex.FlexLayout;

/**
 * Flex child properties: grow, shrink, basis, alignSelf.
 */
public final class FlexConstraints {

    public static final FlexConstraints DEFAULT = new FlexConstraints(0f, 1f, null, null);

    private final float flexGrow;
    private final float flexShrink;
    /** Explicit basis in pixels. {@code null} means "use natural size". */
    private final Float flexBasis;
    /** Override parent alignItems. {@code null} means use parent's alignItems. */
    private final FlexLayout.AlignItems alignSelf;

    private FlexConstraints(float flexGrow, float flexShrink, Float flexBasis,
                            FlexLayout.AlignItems alignSelf) {
        this.flexGrow   = flexGrow;
        this.flexShrink = flexShrink;
        this.flexBasis  = flexBasis;
        this.alignSelf  = alignSelf;
    }

    public static Builder builder() { return new Builder(); }

    public FlexConstraints withFlexGrow(float grow)           { return new FlexConstraints(grow,    flexShrink, flexBasis, alignSelf); }
    public FlexConstraints withFlexShrink(float shrink)       { return new FlexConstraints(flexGrow, shrink,    flexBasis, alignSelf); }
    public FlexConstraints withFlexBasis(float basis)         { return new FlexConstraints(flexGrow, flexShrink, basis,    alignSelf); }
    public FlexConstraints withAlignSelf(FlexLayout.AlignItems a) { return new FlexConstraints(flexGrow, flexShrink, flexBasis, a); }

    public float               getFlexGrow()   { return flexGrow; }
    public float               getFlexShrink() { return flexShrink; }
    public Float               getFlexBasis()  { return flexBasis; }
    public FlexLayout.AlignItems getAlignSelf() { return alignSelf; }

    public static class Builder {
        private float flexGrow = 0f;
        private float flexShrink = 1f;
        private Float flexBasis = null;
        private FlexLayout.AlignItems alignSelf = null;

        public Builder flexGrow(float v)   { flexGrow   = v; return this; }
        public Builder flexShrink(float v) { flexShrink = v; return this; }
        public Builder flexBasis(float v)  { flexBasis  = v; return this; }
        public Builder alignSelf(FlexLayout.AlignItems a) { alignSelf = a; return this; }

        public FlexConstraints build() {
            return new FlexConstraints(flexGrow, flexShrink, flexBasis, alignSelf);
        }
    }
}