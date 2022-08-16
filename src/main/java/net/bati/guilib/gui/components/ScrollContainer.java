package net.bati.guilib.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bati.guilib.utils.DrawHelper;
import net.bati.guilib.utils.DrawUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Range;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    private BAR barPosition = BAR.RIGHT;

    private boolean hideScrollAnimation = false;
    private long scrollTimer = 0L;
    private long scrollFadeTimer = 0L;

    private int barHoverColor = 16771400;
    private int barColor = 0x595A61;
    public ScrollContainer(String identifier) {
        super(identifier);
        contentHeight = 1;
    }

    public float getScrollDistance() {
        return scrollDistance;
    }

    public void calculate() {
        this.top = getRecursiveY();
        this.left = getRecursiveX();
        this.bottom = getBoxHeight() + this.top;
        this.right = getBoxWidth() + this.left;

        this.barLeft = (barPosition.equals(BAR.RIGHT)) ? this.left + this.getBoxWidth() - barWidth : left;
    }

    public int getContentHeight() {
        return contentHeight;
    }

    public float getHideScrollProgress(float speed, long t) {
        return MathHelper.clamp((float) ((Util.getMeasuringTimeMs() - t) / 1000.0F * speed), 0, 1);
    }

    public void setHideScrollAnimation(boolean t) {
        hideScrollAnimation = t;
    }

    public void setBarWidth(@Range(from = 1, to = 100) int width) {
        barWidth = MathHelper.clamp(width, 1, 100);
    }
    private void onScroll() {
        if(hideScrollAnimation) {
            scrollTimer = Util.getMeasuringTimeMs();
            scrollFadeTimer = 0L;
        }
    }

    public void setContentHeight(int contentHeight) {
        this.contentHeight = contentHeight;
        calculate();
    }

    public void setScrollDistance(float s) {
        this.scrollDistance = s;
    }
    private int getMaxScroll() {
        return this.getContentHeight() - (getBoxHeight() - this.border);
    }

    public void setBarPosition(BAR position) {
        barPosition = position;
    }
    private void limitScroll() {
        int max = getMaxScroll();

        if (max < 0) {
            max /= 2;
        }
        scrollDistance = MathHelper.clamp(scrollDistance, 0, max);

    }


    protected int getScrollAmount() {
        return 20;
    }

    public void fit() {
        getWidgets().forEach((key, value) -> {
            if((value.getY() - scrollDistance) + value.getBoxHeight()*value.getSize() > contentHeight) {
                contentHeight = (int) ((value.getY() - scrollDistance) + value.getBoxHeight()*value.getSize());
            }

        });
    }

    public void disableObjectCulling() {
        objectCulling = false;
    }

    public void enableObjectCulling() {
        objectCulling = true;
    }

    public void setBarHoverColor(int color) {
        barHoverColor = color;
    }

    public void setBarColor(int color) {
        barColor = color;
    }
    private int getBarHeight() {
        int barHeight = (getBoxHeight() * getBoxHeight()) / this.getContentHeight();

        if (barHeight < 32) barHeight = 32;

        if (barHeight > getBoxHeight() - border * 2)
            barHeight = getBoxHeight() - border * 2;

        return barHeight;
    }

    public void lastRender(MatrixStack matrices, float mouseX, float mouseY, float delta) {
        getWidgets().forEach((key, value) -> {
            matrices.translate(0, -smoothScrollDistance, 0);
            value.lastRender(matrices, mouseX, mouseY, delta);
            matrices.translate(0, smoothScrollDistance, 0);
        });
    }
    @Override
    public void onMouseScroll(double mouseX, double mouseY, double amount) {
        if (amount == 0 || !isHovered()) return;

        this.scrollDistance += -amount * getScrollAmount();
        onScroll();
        limitScroll();


    }

    @Override
    public void onMouseRelease(double mouseX, double mouseY, int state) {
        this.scrolling = false;
        if (this.isEnabled() && this.isHovered()) {
            getWidgets().forEach((key, value) -> {
                if (value.isVisible()) {
                    value.mouseReleased(mouseX, mouseY, state);
                }

            });
        }
    }

    @Override
    public void onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isEnabled() && scrolling) {
            int maxScroll = getBoxHeight() - getBarHeight();
            double moved = deltaY / maxScroll;
            this.scrollDistance += getMaxScroll() * moved;
            onScroll();
            limitScroll();
        }
        if (this.isEnabled() && this.isHovered()) {
            getWidgets().forEach((key, value) -> {
                if (value.isVisible()) {
                    value.onMouseDrag(mouseX, mouseY , button, deltaX, deltaY);
                }
            });
        }
    }

    @Override
    public void onMouseClick(double mouseX, double mouseY, int mouseButton) {
        if (isEnabled() && isHovered()) {
            double ps = getRecursiveX()+getBoxWidth()* getRecursiveSize();
            this.scrolling = mouseButton == 0 &&  (barPosition.equals(BAR.RIGHT) ? mouseX >= ps-4 && mouseX < ps : mouseX >= getRecursiveX() && mouseX <= getRecursiveX() + barWidth);
            if (this.scrolling) {
                this.scrollDistance = getMaxScroll() * (float) ((mouseY - getRecursiveY()) / (getBoxHeight()* getRecursiveSize()));
                onScroll();
                return;
            }

            getWidgets().forEach((key, value) -> {
                if (value.isVisible()) {
                    value.mouseClicked(mouseX, mouseY, mouseButton);
                }

            });
        }
    }

    @Override
    protected void draw(MatrixStack matrices, float mouseX, float mouseY, float delta) {
        boolean canScroll = ((getContentHeight() + border) - getBoxHeight()) > 0;

        matrices.push();
        matrices.translate(0,0, getZ());
        smoothScrollDistance = canScroll ? MathHelper.clamp((float) MathHelper.lerp(delta*0.5, smoothScrollDistance, scrollDistance), 0, getMaxScroll()) : 0;

        float recursiveOpacity = getRecursiveOpacity();
        RenderSystem.setShaderColor(1,1,1, recursiveOpacity);
        DrawHelper.drawWithPivot(
                matrices,
                getOffsetX(),
                getOffsetY(),
                getBoxWidth(),
                getBoxHeight(),
                getSize(),
                delta,
                getPivot(),
                () -> {

                    int color = 1;

                    // DpzDrawHelper.drawVerticalGradient(matrices, 0, 0, getBoxWidth(), getBoxHeight(), 0, color, color, 0.4f, 0.4f);

                    matrices.translate(0, -smoothScrollDistance, 0);
                    double scaleFactor = MinecraftClient.getInstance().getWindow().getScaleFactor();

                    RenderSystem.enableScissor((int) (getRecursiveX()*scaleFactor), (int) (MinecraftClient.getInstance().getWindow().getFramebufferHeight() - (getRecursiveY()+getBoxHeight()* getRecursiveSize())*scaleFactor), (int)((getBoxWidth()* getRecursiveSize())*scaleFactor), (int)((getBoxHeight()* getRecursiveSize())*scaleFactor));

                    renderWidgets(matrices, mouseX, (mouseY + smoothScrollDistance* getRecursiveSize()), delta);
                    RenderSystem.disableScissor();

                    matrices.translate(0, smoothScrollDistance, 0);

                    int barHeight = getBarHeight();
                    int extraHeight = (this.getContentHeight() + border) - getBoxHeight();

                    float barTop =  this.smoothScrollDistance * (getBoxHeight() - barHeight) / extraHeight;

                    if(canScroll) {
                        float barPosX = barPosition.equals(BAR.RIGHT) ? getRecursiveX() + getBoxWidth() * getRecursiveSize() - barWidth : getRecursiveX();
                        boolean isHover = (mouseX >= barPosX && mouseX <= barPosX + barWidth && mouseY >= getRecursiveY() + barTop * getRecursiveSize() && mouseY <= getRecursiveY() + barTop * getRecursiveSize() + barHeight * getRecursiveSize()) || this.scrolling;
                        if (isHover) {
                            onScroll();
                            color = barHoverColor;
                        } else {
                            color = barColor;
                        }

                        float alphaProgress = 1;
                        if (hideScrollAnimation && getHideScrollProgress(0.3f, scrollTimer) >= 1) {
                            if (scrollFadeTimer == 0L) {
                                scrollFadeTimer = Util.getMeasuringTimeMs();
                            }
                            alphaProgress = 1 - (getHideScrollProgress(1, scrollFadeTimer));
                        }

                        DrawUtils.drawVerticalGradient(matrices, barPosition.equals(BAR.RIGHT) ? getBoxWidth() - barWidth : 0, 0, barPosition.equals(BAR.RIGHT) ? getBoxWidth() : barWidth, getBoxHeight(), 0, 1, 1, 0.4f * alphaProgress, 0.4f * alphaProgress);

                        DrawUtils.drawVerticalGradient(matrices, barPosition.equals(BAR.RIGHT) ? getBoxWidth() - barWidth : 0, barTop, barPosition.equals(BAR.RIGHT) ? getBoxWidth() : barWidth, barTop + barHeight, 0, color, color, alphaProgress, alphaProgress);
                    }
                }
        );
        matrices.translate(0,0, -getZ());
        RenderSystem.setShaderColor(1,1,1, 1);
        matrices.pop();

    }

    /**
     * Variante de {@link net.bati.guilib.gui.screen.ScreenUtils#renderWidgets(HashMap, MatrixStack, float, float, float)}, determina que Widgets se encuentran en el
     * campo de "visión" basándonos en el tamaño y escala y deja de dibujarlos.
     */
    public void renderWidgets(MatrixStack matrices, float x, float y, float delta) {

        getWidgets().forEach((key, value) -> value.preRender(matrices, x, y, delta));

        Optional<Map.Entry<String, Widget>> widgetEntry = getWidgets().entrySet().stream().filter((entry) -> entry.getValue().isVisible() && !entry.getValue().isIgnoreBox()).max(Comparator.comparingInt(current -> current.getValue().getRecursiveZ()));

        getWidgets().forEach((key, value) -> {
            if(value.isIgnoreBox()) {
                value.setFocused(true);
            } else {

                if (value.hasParent())
                    value.setFocused(widgetEntry.isPresent() && key.contentEquals(widgetEntry.get().getKey()) && widgetEntry.get().getValue().getParent().isFocused());
                else
                    value.setFocused(widgetEntry.isPresent() && key.contentEquals(widgetEntry.get().getKey()));

            }
            if(objectCulling) {
                var v = value.getRecursiveY() - smoothScrollDistance * getRecursiveSize();
                if (v <= getRecursiveY() + ((getBoxHeight() / 2F) * getRecursiveSize())) {
                    v += value.getBoxHeight() * value.getRecursiveSize();
                }

                value.setVisible(calculateHovered(value.getRecursiveX(), v));
            }

            value.render(matrices, x, y, delta);

        });
    }

    public enum BAR {
        RIGHT, LEFT
    }
}
