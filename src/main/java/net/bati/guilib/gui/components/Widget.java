package net.bati.guilib.gui.components;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.bati.guilib.utils.Callback;
import net.bati.guilib.utils.DrawHelper;
import net.bati.guilib.utils.Pivot;
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

    @Builder.Default private Vec2 offsetPosition = new Vec2(0,0);
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
    @Builder.Default private boolean enabled = true;

    @Builder.Default private Pivot pivot = Pivot.LEFT_TOP;
    @Builder.Default private Pivot attach = Pivot.LEFT_TOP;


    @Builder.Default private float size = 1F;
    private float expandHitbox;
    @Builder.Default private float opacity = 1F;
    @Builder.Default private boolean visible = true;
    /**
     * Determines if the item is focused or not ( depends on 'z' offset)
     */
    private boolean focused;

    /**
     * Just for development proposes, shows the area based on 'boxWidth' and 'boxHeight'.
     */
    private boolean showArea;
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

    private double mouseX, mouseY;

    public Widget() {
        randomColor = (int) (Math.random()*16777215);
    }

    public boolean hasParent() {
        return getParent() != null;
    }

    public int getOffsetX() {
        return  offsetPosition.getX() + (int)calculateXAttachedValue();
    }

    private float calculateXAttachedValue() {
        return hasParent() ? 0 : attach.getX(MinecraftClient.getInstance().getWindow().getScaledWidth());
    }

    public float getRecursiveX() {
        return hasParent() ? parent.getRecursiveX() + getX() * parent.getRecursiveSize() : getX();
    }

    public float getX() {
        return getOffsetX() - pivot.getX(getBoxWidth() * getSize());
    }

    public int getOffsetY() {
        return offsetPosition.getY() + (int)calculateYAttachedValue();
    }

    private float calculateYAttachedValue() {
        return hasParent() ? 0: attach.getY(MinecraftClient.getInstance().getWindow().getScaledHeight());
    }

    public float getRecursiveY() {
        return hasParent() ? parent.getRecursiveY() + getY() * parent.getRecursiveSize() : getY();
    }

    public float getY() {
        return getOffsetY() - pivot.getY(getBoxHeight() * getSize());
    }

    public float getRecursiveSize() {
        return (hasParent() ? parent.getRecursiveSize() : 1) * getSize();
    }

    public float getSize() {
        return size;
    }

    public float getRecursiveOpacity() {
        return (hasParent() ? parent.getRecursiveOpacity() : 1) * getOpacity();
    }

    public float getOpacity() {
        return opacity;
    }
    public void setOpacity(float s) {
        this.opacity = MathHelper.clamp(s, 0, 1);
    }

    public int getRecursiveZ() {
        return (hasParent()) ? ((getZ() < 0) ? parent.getRecursiveZ() + 1 : getZ() + parent.getRecursiveZ()) : getZ();
    }
    public int getZ() {
        return z;
    }


    public void init() {

    }

    /**
     * Use this method to hide visual and functional sides of the widget.
     * @param s
     */
    public void setHide(boolean s) {
        setVisible(!s);
        setEnabled(!s);
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return (hoverCallback == null) ?
                mouseX >= (getRecursiveX() - expandHitbox) && mouseY >= (getRecursiveY() - expandHitbox) && mouseX <= ((getRecursiveX() + expandHitbox + getBoxWidth()* getRecursiveSize())) && mouseY <= ((getRecursiveY() + expandHitbox + getBoxHeight()* getRecursiveSize()))
                : hoverCallback.isHovering(mouseX, mouseY);
    }

    public boolean isFocused(double mouseX, double mouseY) {
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

        setOffsetPosition(positionCallback.get(MinecraftClient.getInstance().getWindow()));
    }

    protected void drawBoxArea(MatrixStack matrices) {
        if(!showArea) return;
        DrawHelper.fillGradient(
                matrices,
                getX(),
                getY(),
                getX() + getBoxWidth() * getSize(),
                getY() + getBoxHeight() * getSize(),
                randomColor,
                0.5f,
                getZ()
        );
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(!visible) return;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
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

    public void onMouseClick(double mouseX, double mouseY, int mouseButton) {
        if(!isFocused(mouseX, mouseY))
            return;

        if(clickCallback != null)
            clickCallback.call(mouseX, mouseY, mouseButton);


    }
    public void onMouseRelease(double mouseX, double mouseY, int state) {
        if(!isFocused(mouseX, mouseY))
            return;
        if(releaseClickCallback != null)
            releaseClickCallback.call(mouseX, mouseY, state);


    }

    public void onKeyPress(int keyCode, int scanCode, int modifiers) {
        if(!isFocused(mouseX, mouseY))
            return;
        if(keyPressedCallback != null)
            keyPressedCallback.call(keyCode, scanCode, modifiers);

    }

    public void onKeyRelease(int keyCode, int scanCode, int modifiers) {
        if(!isFocused(mouseX, mouseY))
            return;
        if(keyReleasedCallback != null)
            keyReleasedCallback.call(keyCode, scanCode, modifiers);
    }



    public void onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(!isFocused(mouseX, mouseY))
            return;
    }
    public void onCharType(char chr, int modifiers) {
        if(!isFocused(mouseX, mouseY))
            return;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        onMouseClick(mouseX, mouseY, mouseButton);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        this.onMouseRelease(mouseX, mouseY, state);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.onKeyPress(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.onKeyRelease(keyCode, scanCode, modifiers);
        return true;
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        onMouseDrag(mouseX, mouseY, button, deltaX, deltaY);
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        onCharType(chr, modifiers);
        return true;
    }
}
