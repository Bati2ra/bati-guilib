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

import java.util.function.Consumer;

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
    private Callback.Drawable onPreDraw, onPostDraw, onDraw;

    /**
     * Updates every tick inclusive when the widget is not visible
     */
    private Consumer<Widget> onUpdate;


    private Callback.Mouse onClick, onReleaseClick;

    private Callback.Pressable onPressKey, onReleaseKey;

    /**
     * [Listener] Overrides isHover method with your custom lambda expression.
     */
    private Callback.Hoverable hoveringListener;

    /**
     * [Listener] If is used, it'll override 'x' and 'y' fields, used in case you need a dynamic position.
     */
    private Callback.ScreenPosition positionListener;
    private Consumer<Widget> onInit;

    private double mouseX, mouseY;

    public Widget(String identifier, int boxWidth, int boxHeight) {
        this();
        setIdentifier(identifier);
        setBoxWidth(boxWidth);
        setBoxHeight(boxHeight);
        setEnabled(true);
        setVisible(true);
        setOpacity(1);
        setSize(1);
        setPivot(Pivot.LEFT_TOP);
        setAttach(Pivot.LEFT_TOP);
        setOffsetPosition(new Vec2(0,0));
    }
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
        if(onInit != null)
            onInit.accept(this);
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
        return (hoveringListener == null) ?
                mouseX >= (getRecursiveX() - expandHitbox) && mouseY >= (getRecursiveY() - expandHitbox) && mouseX <= ((getRecursiveX() + expandHitbox + getBoxWidth()* getRecursiveSize())) && mouseY <= ((getRecursiveY() + expandHitbox + getBoxHeight()* getRecursiveSize()))
                : hoveringListener.isHovering(mouseX, mouseY);
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

    protected  void postDraw(MatrixStack matrices, int mouseX, int mouseY, float delta){};

    protected  void preDraw(MatrixStack matrices, int mouseX, int mouseY, float delta){};

    private void calculatePositionCallback() {
        if(positionListener == null)
            return;

        setOffsetPosition(positionListener.get(MinecraftClient.getInstance().getWindow()));
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
        if(onUpdate != null)
            onUpdate.accept(this);

        if(!visible) return;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        calculatePositionCallback();
        drawBoxArea(matrices);

        if(onPreDraw != null)
            onPreDraw.draw(this, matrices, mouseX, mouseY, delta);

        preDraw(matrices, mouseX, mouseY, delta);

        if(onDraw != null)
            onDraw.draw(this, matrices, mouseX, mouseY, delta);
        else
            draw(matrices, mouseX, mouseY, delta);

        if(onPostDraw != null)
            onPostDraw.draw(this, matrices, mouseX, mouseY, delta);

        postDraw(matrices, mouseX, mouseY, delta);
    }

    /** This method will be executed after all the widgets are rendered, (can be useful to draw tooltips)
     */
    public void lastRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {

    }

    public void onMouseClick(double mouseX, double mouseY, int mouseButton){}
    public void onMouseRelease(double mouseX, double mouseY, int state){}
    public void onKeyPress(int keyCode, int scanCode, int modifiers){}
    public void onKeyRelease(int keyCode, int scanCode, int modifiers){}
    public void onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY){}
    public void onCharType(char chr, int modifiers){}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(isFocused(mouseX, mouseY) && onClick != null) {
            onClick.call(mouseX, mouseY, mouseButton);
            return true;
        }
        onMouseClick(mouseX, mouseY, mouseButton);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        if(isFocused(mouseX, mouseY) && onReleaseClick != null) {
            onReleaseClick.call(mouseX, mouseY, state);
            return true;
        }
        this.onMouseRelease(mouseX, mouseY, state);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(isFocused(mouseX, mouseY) && onPressKey != null) {
            onPressKey.call(keyCode, scanCode, modifiers);
            return true;
        }
        this.onKeyPress(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if(isFocused(mouseX, mouseY) && onReleaseKey != null) {
            onReleaseKey.call(keyCode, scanCode, modifiers);
            return true;
        }
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
