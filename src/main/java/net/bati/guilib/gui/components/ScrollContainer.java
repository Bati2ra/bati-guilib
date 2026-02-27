package net.bati.guilib.gui.components;


public class ScrollContainer extends Container {
    protected final int border = 4;

    private int barWidth = 6;
    protected float top, bottom, right, left;
    protected float scrollDistance;
    protected float smoothScrollDistance;

    private float barLeft;
    private boolean scrolling;
    private int contentHeight;

    private boolean objectCulling = true;

    private boolean hideScrollAnimation = false;
    private long scrollTimer = 0L;
    private long scrollFadeTimer = 0L;

    private int barHoverColor = 16771400;
    private int barColor = 0x595A61;
    public ScrollContainer(String identifier) {
        super(identifier);
        contentHeight = 1;
    }
}
