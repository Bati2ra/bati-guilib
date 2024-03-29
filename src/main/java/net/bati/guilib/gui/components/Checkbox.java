package net.bati.guilib.gui.components;

import net.bati.guilib.utils.DrawHelper;
import net.bati.guilib.utils.DrawUtils;
import net.bati.guilib.utils.Sound;
import net.bati.guilib.utils.font.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.function.BiFunction;

public class Checkbox extends Button {

    protected boolean                                 checked;
    private CHECK_TYPE                              checkType = CHECK_TYPE.CROSS;
    private BiFunction<Checkbox, Boolean, Sound>    onChange;
    private int                                     checkColor;
    private String                                  displayText;
    private float                                   textSize = 1;
    private boolean                                 ignoreTextArea;
    private boolean                                 alignRight = true;

    private Vec2f                                   displayTextOffset;

    public Checkbox(String identifier) {
        super(identifier, 1, 1);
    }

    public void setOnChange(BiFunction<Checkbox, Boolean, Sound> onChange) {
        this.onChange = onChange;
    }

    public void setCheckType(CHECK_TYPE check_type) {
        this.checkType = check_type;
    }

    public void setCheckColor(int checkColor) {
        this.checkColor = checkColor;
    }

    public void setDisplayText(String displayText, float textSize) {
        this.displayText = displayText;
        this.textSize = textSize;
    }

    public void setIgnoreTextArea(boolean ignoreTextArea) {
        this.ignoreTextArea = ignoreTextArea;
    }

    public void setAlignRight(boolean alignRight) {
        this.alignRight = alignRight;
    }

    @Override
    protected void draw(MatrixStack matrices, float mouseX, float mouseY, float delta) {
        shouldPlaySound(mouseX, mouseY);

        DrawHelper.drawWithPivot(matrices, getOffsetX(), getOffsetY(), getBoxWidth(), getBoxHeight(), getSize(), delta, getPivot(), () -> {
            DrawUtils.drawVerticalGradient(matrices, 0, 0, getBoxWidth(), getBoxHeight(), 0, getPlaceHolderColor(), getPlaceHolderColor(), 0.5F * getRecursiveOpacityLastTick(), 0.5F * getRecursiveOpacityLastTick());

            float z = MinecraftClient.getInstance().textRenderer.getWidth(displayText) * textSize;
            if(displayText != null) {
                TextUtils.drawText(new LiteralText(displayText), displayTextOffset == null ? (alignRight) ? getBoxWidth() + 2 : -z - 2 : displayTextOffset.x, displayTextOffset == null ? getBoxHeight() * 0.5F - 4F * textSize : displayTextOffset.y, textSize, checkColor, false, false, matrices);
            }
            if(checkType.equals(CHECK_TYPE.BOX)) {
                float gapX = getBoxWidth() * 0.2F;
                float gapY = getBoxHeight() * 0.2F;
                if(checked)
                    DrawUtils.drawVerticalGradient(matrices, gapX, gapY, getBoxWidth() - gapX, getBoxHeight() - gapY, 0, checkColor,  checkColor, getRecursiveOpacityLastTick(), getRecursiveOpacityLastTick());
            } else {
                if(checked) {
                    float s = getBoxHeight()/15F;
                    TextUtils.drawText(new LiteralText("x"), getBoxWidth() * 0.5F, getBoxHeight() * 0.5F - 4F * s, s, checkColor, false, true, matrices);
                }
            }
            if(getDrawInside() != null) {
                getDrawInside().draw(this, matrices, mouseX, mouseY, delta);
            }
        });
    }

    @Override
    public void onMouseClick(double mouseX, double mouseY, int mouseButton) {
        if(!isFocused() || !isEnabled() || !isVisible()) return;

        if(onChange != null) {
            checked = !checked;
            Sound sound = onChange.apply(this, checked);
            switch(sound) {
                case ENABLED -> playSound(getClickEnabledSound());
                case DISABLED -> playSound(getClickDisabledSound());
            }

            if(hasParent()) {
                if(getParent() instanceof Radio radio) {
                    radio.updateWidgets(getIdentifier());
                }
            }
        }
    }

    public void setDisplayTextOffset(Vec2f displayTextOffset) {
        this.displayTextOffset = displayTextOffset;
    }

    public Vec2f getDisplayTextOffset() {
        return displayTextOffset;
    }

    public enum CHECK_TYPE {
        CROSS, BOX
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }
}
