package net.bati.guilib.gui.components;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.bati.guilib.utils.Callback;
import net.bati.guilib.utils.DrawHelper;
import net.bati.guilib.utils.PIVOT;
import net.bati.guilib.utils.Vec2;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
@Getter
@Setter
@SuperBuilder
public abstract class Widget implements Drawable, Element {
    /**
     * A unique identifier, repeated identifiers will be ignored.
     * @param identifier Unique identifier to register the widget.
     * @return The Identifier.
     */
    private String identifier;

    /**
     * Raw X,Y axis position, not final, used to 'offset' the component
     */

    @Builder.Default private Vec2 position = new Vec2(0,0);
    /**
     * Raw Z axis offset used to prioritize the top element (In case one or more element overlaps each other)
     */
    private int z;
    /**
     * Determines the box's size, used to check if mouse is over the widget.
     */
    private int boxWidth, boxHeight;
    /**
     * If true, isHovered method will return true (Width & Height will be ignored)
     */
    private boolean ignoreBox;

    private Widget parent;

     /**
     * If false, all the events (Click, drag, key, etc) will be canceled.
     */
    private boolean enabled = true;

    private PIVOT pivot = PIVOT.LEFT_TOP;
    private PIVOT attach = PIVOT.LEFT_TOP;


    @Builder.Default private float transformSize = 1F;
    @Builder.Default private float opacity = 1F;
    @Builder.Default private boolean visible = true;
    /**
     * Determines if the item is focused or not ( depends on 'z' offset)
     */
    private boolean focused;

    /**
     * Just for development proposes, shows the area based on 'boxWidth' and 'boxHeight'.
     */
    @Builder.Default private boolean showArea = false;
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
    private Callback.Drawable preDrawCallback, postDrawCallback, drawCallback;


    private Callback.Mouse clickCallback, releaseClickCallback;

    private Callback.Pressable keyPressedCallback, keyReleasedCallback;

    /**
     * [Listener] Overrides isHover method with your custom lambda expression.
     */
    private Callback.Hoverable hoverCallback;

    /**
     * [Listener] If is used, it'll override 'x' and 'y' fields, used in case you need a dynamic position.
     */
    private Callback.ScreenPosition positionCallback;

    public Widget() {
        randomColor = (int) (Math.random()*16777215);
    }

    public boolean hasParent() {
        return getParent() != null;
    }

    public int getX() {
        return  position.getX() + (int)calculateXAttachedValue();
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
        return position.getY() + (int)calculateYAttachedValue();
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
        return (hoverCallback == null) ? mouseX >= (calculateRelativeX()) && mouseY >= (calculateRelativeY()) && mouseX <= ((calculateRelativeX() + getBoxWidth()* getRelativeTransformSize())) && mouseY <= ((calculateRelativeY() + getBoxHeight()*getRelativeTransformSize())) : hoverCallback.isHovering(mouseX, mouseY);
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
    protected  void draw(MatrixStack matrices, int mouseX, int mouseY, float delta){};

    private void calculatePositionCallback() {
        if(positionCallback == null)
            return;

        setPosition(positionCallback.get(MinecraftClient.getInstance().getWindow()));
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
