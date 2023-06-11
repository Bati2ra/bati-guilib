package net.bati.guilib.gui.components;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.bati.guilib.gui.screen.AdvancedScreen;
import net.bati.guilib.utils.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.function.BiFunction;
import java.util.function.Consumer;

@Getter
@Setter
@SuperBuilder
public abstract class Widget implements Element {
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
    private AdvancedScreen screen;

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

    @Builder.Default private RenderType renderType = RenderType.PLACEHOLDER;
    private int placeHolderColor;

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
    private Callback.Drawable onPreDraw, onPostDraw, onDraw, drawInside;

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
    private BiFunction<Widget, Boolean, Boolean> onChangeState;

    private double mouseX, mouseY;


    // Estas variables son para evitar llamar repetidas veces a métodos que usan recursividad
    private float lastTickX;
    private float lastTickY;
    private float lastTickSize;

    private float recursiveOpacityLastTick;

    private boolean lastTickHovered;

    private long animationProgress = 0L;
    private Animation animationType;
    private long animationSpeed = 1;
    private Consumer<Widget> overrideAnimationUpdate;
    private Consumer<Widget> onAnimationOutEnd, onAnimationInStart;

    /**
     * Indica cuando puede comenzar a usarse el Widget, ya que debido a que algunos fields se calculan en un momento específico del método render,
     * pueden provocarse "errores" al cambiar la visibilidad del Widget y que interprete como si debería hacer clic. Es un caso MUY concreto.
     */
    @Builder.Default protected boolean canBeUsed = true;
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
        setRenderType(RenderType.PLACEHOLDER);
        canBeUsed = true;
    }
    public Widget() {
        randomColor = (int) (Math.random()*16777215);
    }

    public boolean hasParent() {
        return getParent() != null;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        canBeUsed = false;
    }

    public int getOffsetX() {
        return  offsetPosition.getX() + (int)calculateXAttachedValue();
    }

    private float calculateXAttachedValue() {
        return hasParent() ? 0 : attach.getX(MinecraftClient.getInstance().getWindow().getScaledWidth());
    }

    public float getRecursiveX() {
        return lastTickX;
    }

    public float calculateRecursiveX() {
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
        return lastTickY;
    }

    public float calculateRecursiveY() {
        return hasParent() ? parent.getRecursiveY() + getY() * parent.getRecursiveSize() : getY();
    }

    public float getY() {
        return getOffsetY() - pivot.getY(getBoxHeight() * getSize());
    }

    public float getRecursiveSize() {
        return lastTickSize;
    }

    protected float calculateRecursiveSize() {
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

        if(onChangeState != null && s != isVisible()) {
            var a = onChangeState.apply(this, s);
            if(!a) {
                updateState(s);
            }
            System.out.println("Cambiando estado");
        } else {
            updateState(s);
        }
    }
    private void updateState(boolean s) {
        setVisible(!s);
        setEnabled(!s);
    }


    public boolean isHovered() {
        return lastTickHovered;
    }

    /**
     * Solo se utiliza una vez por iteración de {@link Widget#render(MatrixStack, float, float, float)}, para ahorrar llamados a métodos que
     * utilizan recursividad.
     *
     * Un Widget se considera en estado "hovered" siempre y cuando el mouse se encuentre dentro de la hitbox del mismo y en caso de tener un
     * objeto padre asignado, si el mismo se encuentra "focused"
     */
    protected boolean calculateHovered(double mouseX, double mouseY) {
        boolean parent = !hasParent() || getParent().isHovered();
        return parent && ((hoveringListener == null) ?
                (mouseX >= (getRecursiveX() - expandHitbox) && mouseY >= (getRecursiveY() - expandHitbox) && mouseX <= ((getRecursiveX() + expandHitbox + getBoxWidth()* getRecursiveSize())) && mouseY <= ((getRecursiveY() + expandHitbox + getBoxHeight()* getRecursiveSize())))
                : hoveringListener.isHovering(mouseX, mouseY));
    }

    public boolean isFocused() {
        return focused && isHovered();
    }

    /**
     * If you want to draw something for the widget, use this method instead of render (which is used to handle other listeners)
     */
    protected  void draw(MatrixStack matrices, float mouseX, float mouseY, float delta){};

    protected  void postDraw(MatrixStack matrices, float mouseX, float mouseY, float delta){};

    protected  void preDraw(MatrixStack matrices, float mouseX, float mouseY, float delta){};

    private void calculatePositionCallback() {
        if(positionListener == null)
            return;

        setOffsetPosition(positionListener.get(this, MinecraftClient.getInstance().getWindow()));
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
                0
        );
    }

    public void updateLastTick(float mouseX, float mouseY) {
        lastTickX = calculateRecursiveX();
        lastTickY = calculateRecursiveY();
        lastTickSize = calculateRecursiveSize();
        recursiveOpacityLastTick = getRecursiveOpacity();

        lastTickHovered = calculateHovered(mouseX, mouseY);
    }

    public void preRender(MatrixStack matrices, float mouseX, float mouseY, float delta) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        updateLastTick(mouseX, mouseY);
    }
    public void updateMouseAppearance() {
        if(getScreen() == null) return;
        if(isFocused() && !(this instanceof Container)) {
            if(isEnabled()) {
                getScreen().setMouseState("hover");
            } else {
                getScreen().setMouseState("disabled");
            }
        }
    }
    public void render(MatrixStack matrices, float mouseX, float mouseY, float delta) {
        if(onUpdate != null)
            onUpdate.accept(this);

        tickAnimation();

        if(!visible) return;

        canBeUsed = true;

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


    public void fireAnimation(Animation animation) {
        animationType = animation;
        animationProgress = Util.getMeasuringTimeMs();
    }


    public void stopAnimation() {
        animationType = null;
        animationProgress = 0L;
    }

    public void tickAnimation() {
        if(overrideAnimationUpdate != null) {
            overrideAnimationUpdate.accept(this);
            return;
        }
        if(getAnimationType() == null) return;

        if(getAnimationType().equals(Animation.IN)) {
            setHide(false);
            if(onAnimationInStart != null) {
                onAnimationInStart.accept(this);
            }
        } else {
            if(getAnimationProgress(1) >= 1) {
                setHide(true);
                if(onAnimationOutEnd != null) {
                    onAnimationOutEnd.accept(this);
                }
            }
        }

    }


    public float getAnimationProgress(float speed) {
        return MathHelper.clamp((float) ((Util.getMeasuringTimeMs() - animationProgress) / 1000.0F * speed), 0, 1);
    }

    /** This method will be executed after all the widgets are rendered, (can be useful to draw tooltips)
     */
    public void lastRender(MatrixStack matrices, float mouseX, float mouseY, float delta) {
        updateMouseAppearance();
    }

    public void renderFirst(MatrixStack matrices, float mouseX, float mouseY, float delta) {

    }
    public void onMouseClick(double mouseX, double mouseY, int mouseButton){}
    public void onMouseRelease(double mouseX, double mouseY, int state){}
    public void onKeyPress(int keyCode, int scanCode, int modifiers){}
    public void onKeyRelease(int keyCode, int scanCode, int modifiers){}
    public void onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY){}
    public void onCharType(char chr, int modifiers){}

    public void onMouseScroll(double mouseX, double mouseY, double amount){}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(!isEnabled()) return false;
        if(!canBeUsed) return false;

        if(isFocused() && onClick != null) {
            onClick.call(this,mouseX, mouseY, mouseButton);
            return true;
        }
        onMouseClick(mouseX, mouseY, mouseButton);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        if(!isEnabled()) return false;
        if(!canBeUsed) return false;

        if(isFocused() && onReleaseClick != null) {
            onReleaseClick.call(this, mouseX, mouseY, state);
            return true;
        }
        this.onMouseRelease(mouseX, mouseY, state);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(!isEnabled()) return false;

        if(isFocused() && onPressKey != null) {
            onPressKey.call(this, keyCode, scanCode, modifiers);
            return true;
        }
        this.onKeyPress(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if(!isEnabled()) return false;

        if(isFocused() && onReleaseKey != null) {
            onReleaseKey.call(this, keyCode, scanCode, modifiers);
            return true;
        }
        this.onKeyRelease(keyCode, scanCode, modifiers);
        return true;
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(!isEnabled()) return false;

        onMouseDrag(mouseX, mouseY, button, deltaX, deltaY);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(!isEnabled()) return false;

        onMouseScroll(mouseX, mouseY, amount);
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if(!isEnabled()) return false;

        onCharType(chr, modifiers);
        return true;
    }
}
