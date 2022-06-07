package net.bati.guilib.gui.components;

import net.bati.guilib.gui.interfaces.TextCallback;
import net.bati.guilib.utils.font.TextComponent;
import net.bati.guilib.utils.font.TextUtils;
import net.minecraft.client.util.math.MatrixStack;

public class TextLabel extends Widget{
    private TextComponent textComponent;
    private TextCallback callback;


    @Override
    protected void draw(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(textComponent == null)
            return;

        TextUtils.drawTextComponent(textComponent, (callback == null) ? null : callback.get(), matrices, getX(), getY(), getZOffset(), getOpacity());
    }

    @Override
    public void mouseClick(double mouseX, double mouseY, int mouseButton) {

    }

    @Override
    public void mouseRelease(double mouseX, double mouseY, int state) {

    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        return false;
    }
}
