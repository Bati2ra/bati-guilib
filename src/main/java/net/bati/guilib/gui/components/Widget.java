package net.bati.guilib.gui.components;

import net.bati.guilib.gui.interfaces.*;
import net.bati.guilib.utils.DrawHelper;
import net.bati.guilib.utils.PIVOT;
import net.bati.guilib.utils.Vec2;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;


public abstract class Widget extends DrawableHelper implements Drawable, Element {
    // An identifier, must be unique, if it's repeated with other widget, it will be skipped/ignored.
    private String identifier;


    // I should move this to other element :/
    protected final TextRenderer font;

    // Both variables determine the size of the "box", can be used to check if mouse is over the widget or to draw the element itself
    protected int boxWidth;
    protected int boxHeight;

    // The raw position of the widget, it's not the final position, it's more like an indicator to specify the "offset"
    protected int x;
    protected int y;

    // Alpha channels for the widget
    protected float opacity = 1F;

    // Size transformation, for increasing or decreasing the widget and their children if they have.
    protected float size = 1F;


    // Just handle the visual part, if you want to hide and disable the widget, you must combine this one with "enable"
    protected boolean visible = true;

    // This one is used to disable or enable the widget, if disabled then all the events (click, drag, key, etc) will be canceled.
    protected boolean enabled = true;


    protected boolean isFocused = false;

    // Just for development proposes
    protected boolean showArea = false;
    protected int randomColor = -1;

    protected PIVOT pivot = PIVOT.LEFT_TOP;
    protected PIVOT attach = PIVOT.LEFT_TOP;

    protected long animationProgress;
    protected boolean playAnimation;


    protected Widget parent;

    protected int zOffset;

    //Events
    private DrawableCallback preDrawCallback, postDrawCallback, drawCallback;
    private boolean shouldOverrideDraw;
    private MouseCallback clickCallback, releaseClickCallback;

    private PressableCallback keyPressedCallback, keyReleasedCallback;
    private HoverCallback hoverCallback;
    private ScreenPositionCallback positionCallback;

    private float pivotX, pivotY;
    public Widget() {
        this.font = MinecraftClient.getInstance().textRenderer;
    }

    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String name) {
        this.identifier = name;
    }

    // <-- X axis getter/setter/builder section
    public int getX() {
        return  x + (hasParent() ? 0 : (int)attach.getX(MinecraftClient.getInstance().getWindow().getScaledWidth()));
    }

    public float getPivotX() {
        return pivotX;
    }
    
    private void calculatePivots() {
        pivotX = hasParent() ? parent.getPivotX() + getRelativeX() * parent.getRelativeSize() : getRelativeX();
    }

    public float getRelativeX() {
        return getX() - pivot.getX(getBoxWidth() * getSize());
    }

    public void setX(int x) {
        this.x = x;
    }

    // end section -->

    // <-- Y axis getter/setter/builder section
    public int getY() {
        return y + (hasParent() ? 0: (int)attach.getY(MinecraftClient.getInstance().getWindow().getScaledHeight()));
    }

    public float getPivotY() {
        return hasParent() ? parent.getPivotY() + getRelativeY() * parent.getRelativeSize() : getRelativeY();
    }

    public float getRelativeY() {
        return getY() - pivot.getY(getBoxHeight() * getSize());
    }

    public void setY(int y) {
        this.y = y;
    }

    // end section -->

    public void setPositionCallback(ScreenPositionCallback callback) {
        this.positionCallback = callback;
    }

    public int getBoxWidth() {
        return boxWidth;
    }
    public void setBoxWidth(int s) {
        this.boxWidth = s;
    }

    public int getBoxHeight() {
        return boxHeight;
    }

    public void setBoxHeight(int s) {
        this.boxHeight = s;
    }

    public float getRelativeBoxWidth() {
        return getBoxWidth() * getSize();
    }

    public float getRelativeBoxHeight() {
        return getBoxHeight() * getSize();
    }

    public float getSize() {
        return size;
    }

    public float getRelativeSize() {
        return (hasParent() ? parent.getRelativeSize() : 1) * getSize();
    }

    public void setSize(float s) {
        this.size = s;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public Widget getParent() {
        return parent;
    }

    public void setParent(Widget parent) {
        this.parent = parent;
    }

    public float getOpacity() {
        return (hasParent() ? parent.getOpacity() : 1) * opacity;
    }

    public void setOpacity(float s) {
        this.opacity = MathHelper.clamp(s, 0, 1);
    }

    @Override
    public void setZOffset(int zOffset) {
        this.zOffset = zOffset;
    }

    @Override
    public int getZOffset() {
        return (hasParent()) ? ((zOffset < 0) ? parent.getZOffset() + 1 : zOffset + parent.getZOffset()) : zOffset;
    }

    public void toggleVisible(boolean v) {
        this.visible = v;
    }

    public boolean isVisible() {
        return visible;
    }

    public void toggleEnabled(boolean v) {
        this.enabled = v;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggleWidget(boolean s) {
        toggleVisible(s);
        toggleEnabled(s);
    }

    public PIVOT getAttached() {
        return attach;
    }

    public void setAttachedTo(PIVOT attach) {
        this.attach = attach;
    }

    public void setPivot(PIVOT pivot) {
        this.pivot = pivot;
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return (hoverCallback == null) ? mouseX >= (getPivotX()) && mouseY >= (getPivotY()) && mouseX <= ((getPivotX() + getBoxWidth()* getRelativeSize())) && mouseY <= ((getPivotY() + getBoxHeight()*getRelativeSize())) : hoverCallback.isHover(mouseX, mouseY);
    }

    public void setHoverCallback(HoverCallback callback) {
        this.hoverCallback = callback;
    }

    public boolean isFocused(int mouseX, int mouseY) {
        return isFocused && isHovered(mouseX, mouseY);
    }

    public void setFocused(boolean focused) {
        this.isFocused = focused;
    }

    public void setDrawCallback(DrawableCallback drawable) {
        this.drawCallback = drawable;
        this.shouldOverrideDraw = true;
    }

    public void setPreDrawCallback(DrawableCallback drawable) {
        this.preDrawCallback = drawable;
    }

    public void setPostDrawCallback(DrawableCallback drawable) {
        this.postDrawCallback = drawable;
    }

    public void setClickCallback(MouseCallback callback) {
        this.clickCallback = callback;
    }

    public void setReleaseClickCallback(MouseCallback callback) {
        this.releaseClickCallback = callback;
    }

    public void setKeyPressedCallback(PressableCallback callback) {
        this.keyPressedCallback = callback;
    }

    public void setKeyReleasedCallback(PressableCallback callback) {
        this.keyReleasedCallback = callback;
    }


    // Used to render visual content for the widget, use this instead of render (which is used to handle all draw events)
    protected void draw(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderArea(matrices);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(!visible) return;

        if(positionCallback != null) {
            Vec2 positionVector = positionCallback.get(MinecraftClient.getInstance().getWindow());
            setX(positionVector.getX());
            setY(positionVector.getY());
        }


        if(preDrawCallback != null)
            preDrawCallback.draw(matrices, mouseX, mouseY, delta);

        if(shouldOverrideDraw)
            drawCallback.draw(matrices, mouseX, mouseY, delta);
        else
            draw(matrices, mouseX, mouseY, delta);

        if(postDrawCallback != null)
            postDrawCallback.draw(matrices, mouseX, mouseY, delta);



    }

    // <-- This is used only for development proposes

    public Widget showArea() {
        this.showArea = true;
        this.randomColor = (int) (Math.random()*16777215);
        return this;
    }
    public void renderArea(MatrixStack matrices) {
        if(!showArea) return;
        DrawHelper.fillGradient(matrices, getRelativeX(), getRelativeY(), getRelativeX() + getRelativeBoxWidth(),getRelativeY() + getRelativeBoxHeight(), randomColor, 0.5f, getZOffset());
    }

    // end section -->



    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        mouseClick(mouseX, mouseY, mouseButton);
        return true;
    }

    public void mouseClick(double mouseX, double mouseY, int mouseButton) {
        if(clickCallback == null)
            return;

        clickCallback.call(mouseX, mouseY, mouseButton);
    };

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        this.mouseRelease(mouseX, mouseY, state);
        return true;
    }

    public void mouseRelease(double mouseX, double mouseY, int state) {
        if(releaseClickCallback == null)
            return;

        releaseClickCallback.call(mouseX, mouseY, state);
    };

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.keyPress(keyCode, scanCode, modifiers);
        return true;
    }


    public void keyPress(int keyCode, int scanCode, int modifiers) {
        if(keyPressedCallback == null)
            return;

        keyPressedCallback.call(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.keyRelease(keyCode, scanCode, modifiers);
        return true;
    }

    public void keyRelease(int keyCode, int scanCode, int modifiers) {
        if(keyReleasedCallback == null)
            return;

        keyReleasedCallback.call(keyCode, scanCode, modifiers);
    }



}
