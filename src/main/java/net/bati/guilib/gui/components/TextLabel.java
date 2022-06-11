package net.bati.guilib.gui.components;

import net.bati.guilib.utils.Callback;
import net.bati.guilib.utils.font.TextComponent;
import net.bati.guilib.utils.font.TextUtils;
import net.minecraft.client.util.math.MatrixStack;

public class TextLabel extends Widget{
    private TextComponent textComponent;
    protected Callback.Text callback;


    public TextLabel() {

    }


    public TextComponent getTextComponent() {
        return textComponent;
    }
    public void setTextComponent(TextComponent component) {
        this.textComponent = component;
    }
    public void setTextCallback(Callback.Text callback) {
        this.callback = callback;
    }

    @Override
    protected void draw(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(textComponent == null)
            return;


        TextUtils.drawTextComponent(textComponent, (callback == null) ? null : callback.get(), matrices, getX(), getY(), getZ(), getOpacity());
    }


}
