package net.bati.guilib.gui.components;

import lombok.Getter;
import lombok.Setter;
import net.bati.guilib.gui.interfaces.*;
import net.bati.guilib.utils.DrawHelper;
import net.bati.guilib.utils.PIVOT;
import net.bati.guilib.utils.Vec2;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
@Getter
@Setter
public abstract class Widget extends DrawableHelper implements Drawable, Element {
    /**
     * A unique identifier, repeated identifiers will be ignored.
     * @param identifier Unique identifier to register the widget.
     * @return The Identifier.
     */
    private String identifier;
    /**
     * Raw X axis position, not final, used to 'offset' the component
     * @param x New value for X 'offset'.
     * @return Current value of 'x'.
     */
    private int x;
    /**
     * Raw Y axis position, not final, used to 'offset' the component
     * @param y New value for Y 'offset'.
     * @return Current value of 'y'.
     */
    private int y;
    /**
     * Raw Z axis offset used to prioritize the top element (In case one or more element overlaps each other)
     * @param z New value for Z 'offset'.
     * @return Current value of 'z'.
     */
    private int z;

    // Both variables determine the size of the "box", can be used to check if mouse is over the widget or to draw the element itself
    /**
     * Determines the box's width size, used to check if mouse is over the widget.
     * @param boxWidth New value for width.
     * @return Width integer.
     */
    private int boxWidth;
    /**
     * Determines the box's height size, used to check if mouse is over the widget.
     * @param boxHeight New value for height.
     * @return Height integer.
     */
    private int boxHeight;
    /**
     * If true, isHovered method will return true (Width & Height will be ignored)
     */
    private boolean ignoreBox;

    private Widget parent;

    // This one is used to disable or enable the widget, if disabled then all the events (click, drag, key, etc) will be canceled.
    /**
     * If false, all the events (Click, drag, key, etc) will be canceled.
     */
    private boolean enabled = true;

    private PIVOT pivot = PIVOT.LEFT_TOP;
    private PIVOT attach = PIVOT.LEFT_TOP;

    /**
     * Size transformation, for increasing or decreasing the widget and their children if they have.
     */
    private float transformSize = 1F;
    // Alpha channels for the widget
    private float opacity = 1F;
    /**
     * Just handles the visual part, if you want to hide and disable the widget, you must combine this one with 'enabled'
     */
    private boolean visible = true;
    /**
     * Determines if the item is focused or not ( depends on 'z' offset)
     */
    private boolean focused;

    /**
     * Just for development proposes, shows the area based on 'boxWidth' and 'boxHeight'.
     */
    private boolean showArea = false;
    /**
     * Random color to paint the area.
     */
    private final int randomColor;

    /**
     * These interfaces are used to draw something on screen using lambda expressions, in case you want to render something without having to
     * create a new class for every little variation.
     * - 'preDrawCallback' is called before drawCallback.
     * - 'postDrawCallback' is called after drawCallback.
     * - 'drawCallback' if used, it'll override the widget draw(m, x, y, d) method
     * @param callback Lambda expression.
     * @return callback.
     */
    private DrawableCallback preDrawCallback, postDrawCallback, drawCallback;


    private MouseCallback clickCallback, releaseClickCallback;

    private PressableCallback keyPressedCallback, keyReleasedCallback;

    /**
     * [Listener] Overrides isHover method with your custom lambda expression.
     */
    private HoverCallback hoverCallback;

    /**
     * [Listener] If is used, it'll override 'x' and 'y' fields, used in case you need a dynamic position.
     */
    private ScreenPositionCallback positionCallback;


    public Widget() {
        randomColor = (int) (Math.random()*16777215);
    }

    public boolean hasParent() {
        return getParent() != null;
    }

    public int getX() {
        return  x + (int)calculateXAttachedValue();
    }

    private float calculateXAttachedValue() {
        return hasParent() ? 0 : attach.getX(MinecraftClient.getInstance().getWindow().getScaledWidth());
    }

    public float calculateRelativeX() {
        return hasParent() ? parent.calculateRelativeX() + calculateXWithPivot() * parent.getRelativeTransformSize() : calculateXWithPivot();
    }

    public float calculateXWithPivot() {
        return getX() - pivot.getX(getBoxWidth() * getTransformSize());
    }

    public int getY() {
        return y + (int)calculateYAttachedValue();
    }

    private float calculateYAttachedValue() {
        return hasParent() ? 0: attach.getY(MinecraftClient.getInstance().getWindow().getScaledHeight());
    }

    public float calculateRelativeY() {
        return hasParent() ? parent.calculateRelativeY() + calculateYWithPivot() * parent.getRelativeTransformSize() : calculateYWithPivot();
    }

    public float calculateYWithPivot() {
        return getY() - pivot.getY(getBoxHeight() * getTransformSize());
    }

    public float getRelativeTransformSize() {
        return (hasParent() ? parent.getRelativeTransformSize() : 1) * getTransformSize();
    }

    public float getOpacity() {
        return (hasParent() ? parent.getOpacity() : 1) * opacity;
    }

    public void setOpacity(float s) {
        this.opacity = MathHelper.clamp(s, 0, 1);
    }

    public int getZ() {
        return (hasParent()) ? ((z < 0) ? parent.getZ() + 1 : z + parent.getZ()) : z;
    }

    /**
     * Use this method to hide visual and functional sides of the widget.
     * @param s
     */
    public void setHide(boolean s) {
        setVisible(!s);
        setEnabled(!s);
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return (hoverCallback == null) ? mouseX >= (calculateRelativeX()) && mouseY >= (calculateRelativeY()) && mouseX <= ((calculateRelativeX() + getBoxWidth()* getRelativeTransformSize())) && mouseY <= ((calculateRelativeY() + getBoxHeight()*getRelativeTransformSize())) : hoverCallback.isHover(mouseX, mouseY);
    }

    public boolean isFocused(int mouseX, int mouseY) {
        return focused && isHovered(mouseX, mouseY);
    }

    /**
     * If you want to draw something for the widget, use this method instead of render (which is used to handle other listeners)
     * @param matrices
     * @param mouseX
     * @param mouseY
     * @param delta
     */
    protected abstract void draw(MatrixStack matrices, int mouseX, int mouseY, float delta);

    private void calculatePositionCallback() {
        if(positionCallback == null)
            return;

        Vec2 positionVector = positionCallback.get(MinecraftClient.getInstance().getWindow());
        setX(positionVector.getX());
        setY(positionVector.getY());
    }

    protected void drawBoxArea(MatrixStack matrices) {
        if(!showArea) return;
        DrawHelper.fillGradient(
                matrices,
                calculateXWithPivot(),
                calculateYWithPivot(),
                calculateXWithPivot() + getBoxWidth() * getTransformSize(),
                calculateYWithPivot() + getBoxHeight() * getTransformSize(),
                randomColor,
                0.5f,
                getZ()
        );
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(!visible) return;

        calculatePositionCallback();
        drawBoxArea(matrices);

        if(preDrawCallback != null)
            preDrawCallback.draw(matrices, mouseX, mouseY, delta);

        if(drawCallback != null)
            drawCallback.draw(matrices, mouseX, mouseY, delta);
        else
            draw(matrices, mouseX, mouseY, delta);

        if(postDrawCallback != null)
            postDrawCallback.draw(matrices, mouseX, mouseY, delta);
    }

    protected void mouseClickCallback(double mouseX, double mouseY, int mouseButton) {
        if(clickCallback == null)
            return;

        clickCallback.call(mouseX, mouseY, mouseButton);
    }
    protected void mouseReleaseCallback(double mouseX, double mouseY, int state) {
        if(releaseClickCallback == null)
            return;

        releaseClickCallback.call(mouseX, mouseY, state);
    }

    protected void keyPressCallback(int keyCode, int scanCode, int modifiers) {
        if(keyPressedCallback == null)
            return;

        keyPressedCallback.call(keyCode, scanCode, modifiers);
    }

    protected void keyReleaseCallback(int keyCode, int scanCode, int modifiers) {
        if(keyReleasedCallback == null)
            return;

        keyReleasedCallback.call(keyCode, scanCode, modifiers);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        mouseClickCallback(mouseX, mouseY, mouseButton);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        this.mouseReleaseCallback(mouseX, mouseY, state);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.keyPressCallback(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.keyReleaseCallback(keyCode, scanCode, modifiers);
        return true;
    }

}
