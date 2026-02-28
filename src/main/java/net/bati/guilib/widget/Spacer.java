package net.bati.guilib.widget;


import net.bati.guilib.layout.MeasureResult;

/**
 * An invisible spacer widget.
 * Set flexGrow=1 to fill remaining space in a flex container.
 */
public class Spacer extends Widget {

    private final float fixedWidth;
    private final float fixedHeight;

    public Spacer(String id, float w, float h) {
        super(id);
        this.fixedWidth  = w;
        this.fixedHeight = h;
    }

    /** A spacer that grows to fill available flex space. */
    public static Spacer grow(String id) {
        Spacer s = new Spacer(id, 0, 0);
        s.setFlex(s.getFlexConstraints().withFlexGrow(1f));
        return s;
    }

    /** Fixed-size spacer. */
    public static Spacer fixed(String id, float size) {
        return new Spacer(id, size, size);
    }

    @Override
    protected MeasureResult measureContent(float aw, float ah) {
        return new MeasureResult(fixedWidth, fixedHeight);
    }
}