package net.bati.guilib.gui.components;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.bati.guilib.utils.Callback;
import net.bati.guilib.utils.font.TextComponent;
import net.bati.guilib.utils.font.TextUtils;
import net.minecraft.client.util.math.MatrixStack;
@Getter
@Setter
@SuperBuilder
public class TextLabel extends Widget {
    private TextComponent textComponent;
    private Callback.Text callback;

    @Override
    protected void draw(MatrixStack matrices, float mouseX, float mouseY, float delta) {
        if(textComponent == null)
            return;


        TextUtils.drawTextComponent(textComponent, (callback == null) ? null : callback.get(), matrices, getOffsetX(), getOffsetY(), getRecursiveZ(), getRecursiveOpacity());
    }


}
