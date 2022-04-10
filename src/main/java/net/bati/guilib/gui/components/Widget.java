package net.bati.guilib.gui.components;

import net.bati.guilib.utils.DrawHelper;
import net.bati.guilib.utils.PIVOT;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;


public abstract class Widget extends DrawableHelper implements Drawable, Element {
    private String identifier;
    protected final TextRenderer font;
    protected int boxWidth;
    protected int boxHeight;
    protected int x;
    protected int y;
    protected float opacity = 1F;
    protected float size = 1F;
    protected boolean visible = true;
    protected boolean enabled = true;
    protected boolean isFocused = false;
    protected boolean showArea = false;
    protected int randomColor = -1;
    protected PIVOT pivot = PIVOT.LEFT_TOP;
    protected PIVOT attach = PIVOT.LEFT_TOP;

    protected long animationProgress;
    protected boolean playAnimation;
    protected Widget parent;

    protected int zOffset;
    protected int offsetX,offsetY;
    public Widget() {
        this.font = MinecraftClient.getInstance().textRenderer;
    }

    public void  setIdentifier(String name) {
        this.identifier = name;
    }

    public String getIdentifier() {
        return identifier;
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
    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x + ((hasParent()) ? parent.getX() - parent.getOffsetX() : (int)attach.getX(MinecraftClient.getInstance().getWindow().getScaledWidth()));
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y + ((hasParent()) ? parent.getY() - parent.getOffsetY(): (int)attach.getY(MinecraftClient.getInstance().getWindow().getScaledHeight()));
    }

    public float getRelativeX() {
        return getX() - pivot.getX(getBoxWidth()*getSize());
    }

    public float getRelativeY() {
        return getY() - pivot.getY(getBoxHeight()*getSize());
    }

    public float getRelativeBoxWidth() {
        return getBoxWidth() * getSize();
    }

    public float getRelativeBoxHeight() {
        return getBoxHeight() * getSize();
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(float s) {
        this.size = s;
    }

    public float getSize() {
        return size * ((hasParent()) ? parent.getSize() : 1);
    }

    public Widget setParent(Widget parent) {
        this.parent = parent;
        return this;
    }
    public boolean hasParent() {
        return parent != null;
    }

    public Widget getParent() {
        return parent;
    }

    public void setOpacity(float s) {
        this.opacity = s;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setBoxWidth(int s) {
        this.boxWidth = s;
    }

    public int getBoxWidth() {
        return boxWidth;
    }

    public void setBoxHeight(int s) {
        this.boxHeight = s;
    }

    public int getBoxHeight() {
        return boxHeight;
    }

    public void fireAnimation(String animationName) {
        this.playAnimation = true;
        this.animationProgress = 0L;
    }

    public boolean isPlayingAnimation() {
        return this.playAnimation && this.animationProgress != 0L;
    }

    public float getAnimationProgress() {
        return MathHelper.clamp((float) ((Util.getMeasuringTimeMs() - this.animationProgress) / 1000.0F * 6), 0, 1);
    }
    public Widget attach(PIVOT pivot) {
        this.attach = pivot;
        return this;
    }

    public PIVOT getAttached() {
        return attach;
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= getRelativeX() && mouseY >= getRelativeY() && mouseX <= getRelativeX() + getRelativeBoxWidth() && mouseY <= getRelativeY() + getRelativeBoxHeight();
    }

    public void setFocused(boolean focused) {
        this.isFocused = focused;
    }

    public boolean isFocused(int mouseX, int mouseY) {
        return isFocused && isHovered(mouseX, mouseY);
    }
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(!visible) return;


        if(playAnimation) {
            if(animationProgress == 0L)
                this.animationProgress = Util.getMeasuringTimeMs();

        }

        renderArea(matrices);
    }

    public Widget showArea() {
        this.showArea = true;
        this.randomColor = (int) (Math.random()*16777215);
        return this;
    }

    public Widget hiddeArea() {
        this.showArea = false;
        return this;
    }

    public void renderArea(MatrixStack matrices) {
        if(!showArea) return;
        DrawHelper.fillGradient(matrices, getRelativeX(), getRelativeY(), getRelativeX() + getRelativeBoxWidth(),getRelativeY() + getRelativeBoxHeight(), randomColor, 0.5f, getZOffset());

    }
    public void setPivot(PIVOT pivot) {
        this.pivot = pivot;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        mouseClick(mouseX, mouseY, mouseButton);
        return true;
    }

    public abstract void mouseClick(double mouseX, double mouseY, int mouseButton);

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        this.mouseRelease(mouseX, mouseY, state);
        return true;
    }

    public abstract void mouseRelease(double mouseX, double mouseY, int state);

    public abstract boolean charTyped(char typedChar, int keyCode);

    @Override
    public void setZOffset(int zOffset) {
        this.zOffset = zOffset;
    }

    @Override
    public int getZOffset() {
        return (hasParent()) ? ((zOffset < 0) ? parent.getZOffset() + 1 : zOffset + parent.getZOffset()) : zOffset;
    }
}
