package net.bati.guilib.layout;

import lombok.Builder;
import lombok.Getter;
import lombok.With;

@Getter
@Builder
@With
public class BoxModel {
    @Builder.Default private final float contentWidth  = 0;
    @Builder.Default private final float contentHeight = 0;

    @Builder.Default private final Insets padding = Insets.ZERO;
    @Builder.Default private final Insets border  = Insets.ZERO;
    @Builder.Default private final Insets margin  = Insets.ZERO;

    public float getBorderBoxWidth()  { return contentWidth  + padding.horizontal() + border.horizontal(); }
    public float getBorderBoxHeight() { return contentHeight + padding.vertical()   + border.vertical();   }
    public float getTotalWidth()      { return getBorderBoxWidth()  + margin.horizontal(); }
    public float getTotalHeight()     { return getBorderBoxHeight() + margin.vertical();   }

    @Getter
    @Builder
    public static final class Insets {
        public static final Insets ZERO = new Insets(0, 0, 0, 0);

        private final float top, right, bottom, left;

        public static Insets all(float v)                         { return new Insets(v, v, v, v); }
        public static Insets symmetric(float vertical, float horizontal) { return new Insets(vertical, horizontal, vertical, horizontal); }
        public static Insets horizontal(float v)                  { return new Insets(0, v, 0, v); }
        public static Insets vertical(float v)                    { return new Insets(v, 0, v, 0); }
        public static Insets of(float top, float right, float bottom, float left) { return new Insets(top, right, bottom, left); }

        public float horizontal() { return left + right; }
        public float vertical()   { return top + bottom; }
    }
}