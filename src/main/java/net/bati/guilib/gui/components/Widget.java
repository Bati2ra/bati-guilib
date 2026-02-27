package net.bati.guilib.gui.components;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.bati.guilib.gui.screen.AdvancedScreen;
import net.bati.guilib.utils.*;

import java.util.function.BiFunction;
import java.util.function.Consumer;

@Getter
@Setter
@SuperBuilder
public abstract class Widget {
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


}
