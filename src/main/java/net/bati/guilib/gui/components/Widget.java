package net.bati.guilib.gui.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

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

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(float s) {
        this.size = s;
    }

    public float getSize() {
        return size;
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

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + boxWidth * size && mouseY <= y + boxHeight * size;
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

        renderArea(matrices);
    }

    public Widget showArea() {
        this.showArea = true;
        return this;
    }

    public Widget hiddeArea() {
        this.showArea = false;
        return this;
    }

    public void renderArea(MatrixStack matrices) {
        if(!showArea) return;

        fill(matrices, x,y, x + (int)(boxWidth * size),y + (int)(boxHeight*size), (int)(Math.random() * 0x1000000));
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
}
