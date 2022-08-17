package net.bati.guilib.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import net.bati.guilib.utils.DrawHelper;
import net.bati.guilib.utils.DrawUtils;
import net.bati.guilib.utils.Orientation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

/**
 * TODO ORIENTATION!!!!
 */
@SuperBuilder
public class Slider extends Widget {
    @Builder.Default private Orientation orientation = Orientation.HORIZONTAL;
    private double value;
    private double min;
    private double max;
    private double tempValue;

    public Slider(String identifier) {
        this(identifier, 50, 10);
    }

    public Slider(String identifier, int width, int height) {
        super(identifier, width, height);
        max = 1;
        orientation = Orientation.HORIZONTAL;
    }
    @Override
    protected void draw(MatrixStack matrices, float mouseX, float mouseY, float delta) {
        matrices.push();
        var opacity = getRecursiveOpacity();
        RenderSystem.setShaderColor(1,1,1, opacity);

        DrawHelper.drawWithPivot(matrices, getOffsetX(), getOffsetY(), (float)this.getBoxWidth(), (float)this.getBoxHeight(), getSize(), delta, this.getPivot(), () -> {

            DrawUtils.drawHorizontalGradient(matrices, -2, -2, -2 + getBoxWidth(), -2 + getBoxHeight(), 0, 1, 1, opacity, opacity);

            tempValue = MathHelper.lerp(delta*0.3, tempValue, value);
            tempValue = MathHelper.clamp(tempValue, 0, 1);

            DrawUtils.drawHorizontalGradient(matrices, (float) (getBoxWidth() * tempValue - 4), -5, (float) ((getBoxWidth() * tempValue - 4) + 8), 9, 0, isEnabled() ? 16777215 : 7040105, isEnabled() ? 16777215 : 7040105, opacity, opacity);
        });

        RenderSystem.setShaderColor(1,1,1, 1);
        matrices.pop();
    }

    @Override
    public void onMouseClick(double mouseX, double mouseY, int mouseButton) {
        if(isFocused())
            calculate(mouseX);
    }

    @Override
    public void onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(isFocused())
            calculate(mouseX);
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getTempValue() {
        return tempValue;
    }

    public void setTempValue(double tempValue) {
        this.tempValue = tempValue;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    private void calculate(double mouseX) {
        value = (mouseX - getRecursiveX()) / (getBoxWidth()*getRecursiveSize());
    }

    public void setNormalizedValue(double value) {
        this.value = MathHelper.clamp(value, 0, 1);
    }

    public double getNormalizedValue() {
        return value;
    }

    public double getValueNoLerp() {
        return MathHelper.clamp(MathHelper.lerp(value, min, max), min, max);
    }

    public double getValue() {
        return MathHelper.clamp(MathHelper.lerp(tempValue, min, max), min, max);
    }
}