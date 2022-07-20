package net.bati.guilib.gui.components;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.bati.guilib.utils.*;
import net.bati.guilib.utils.font.TextComponent;
import net.bati.guilib.utils.font.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.NotNull;
@Getter
@Setter
@SuperBuilder
public class Button extends Widget {
    @Builder.Default private TextureComponent textureComponent = new TextureComponent(0, 46, 256, 256, Constants.getWIDGETS_LOCATION());
    private boolean hoverSound;
    private SoundEvent hoverSoundEvent;
    @Builder.Default private TextComponent textComponent = new TextComponent().text("Empty");
    private boolean pressed;

    public Button(String identifier) {
        this.setIdentifier(identifier);
    }
    /**
     * @param identifier Must be an unique name, if it's repeated, it'll be ignored
     */
    public static ButtonBuilder<?, ?> builder(@NotNull String identifier) {
        return new ButtonBuilderImpl().identifier(identifier);
    }
    @Override
    protected void draw(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        shouldPlaySound(mouseX, mouseY);

        if(textComponent == null) return;

        int i = getYImage(isFocused(mouseX, mouseY));
        matrices.translate(0,0, getZ());
        RenderSystem.setShaderColor(1,1,1, getRecursiveOpacity());

        DrawHelper.drawWithPivot(
                matrices,
                getOffsetX(),
                getOffsetY(),
                getBoxWidth(),
                getBoxHeight(),
                getSize(),
                delta,
                getPivot(),
                (matrixStack, x, y, deltaTime) -> {
                    matrixStack.push();
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);

                    DrawHelper.drawRectangle(textureComponent.getResource(), 0, 0, textureComponent.getU(), textureComponent.getV() + ((isPressed()) ? 2 : i) * getBoxHeight(), getBoxWidth() * 0.5, getBoxHeight(), 1, textureComponent.getTextureWidth(), textureComponent.getTextureHeight(), matrices.peek().getPositionMatrix());
                    DrawHelper.drawRectangle(textureComponent.getResource(), getBoxWidth() / 2, 0, Math.round((textureComponent.getU()+this.getBoxWidth() / 2)), textureComponent.getV() + ((isPressed()) ? 2 : i) * getBoxHeight(), getBoxWidth()/2, this.getBoxHeight(), 1, textureComponent.getTextureWidth(), textureComponent.getTextureHeight(), matrices.peek().getPositionMatrix());

                    drawText(matrices);
                    matrixStack.pop();
                }
        );

        matrices.translate(0,0, -getZ());

    }

    private void shouldPlaySound(int mouseX, int mouseY) {
        if(!isFocused(mouseX, mouseY)) {
            hoverSound = true;
            return;
        }
        if(!hoverSound || hoverSoundEvent == null) return;

        if(isEnabled() && isFocused(mouseX, mouseY))
            playSound(hoverSoundEvent);

        hoverSound = !hoverSound;

    }

    public void playSound(SoundEvent sound) {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(sound, 1, 0.5f));
    }

    protected int getYImage(boolean isHovered) {
        return (!isEnabled()) ? 0 : (isHovered) ? 2 : 1;
    }

    private void drawText(MatrixStack matrices) {
        if(textComponent == null) return;

        int moveX = textComponent.isCentered() ? Math.round(getBoxWidth() / 2) :  Math.round(textComponent.getOffsetPosition().getX());
        int moveY = textComponent.isCentered() ? Math.round((getBoxHeight() / 2 - ((textComponent.hasStyle() ? textComponent.getStyle().getHeight() : TextUtils.font.fontHeight)* textComponent.getSize())/2)) : Math.round(textComponent.getOffsetPosition().getY());

        String buttonText = textComponent.getText();
        int strWidth = (int) (MinecraftClient.getInstance().textRenderer.getWidth(buttonText)*textComponent.getSize());
        int ellipsisWidth = (int) (MinecraftClient.getInstance().textRenderer.getWidth("...") * textComponent.getSize());

        if (strWidth > getBoxWidth()* getSize() - 20 && strWidth > ellipsisWidth)
            buttonText = MinecraftClient.getInstance().textRenderer.trimToWidth(buttonText, (int) (strWidth + getBoxWidth()* getSize() - 20 - ellipsisWidth)).trim() + "...";

        int color = ColorUtils.toHex(textComponent.getColor(), getRecursiveOpacity());

        if(textComponent.isOutlined()) {
            int lineColor = ColorUtils.toHex(textComponent.getLineColor(), getRecursiveOpacity());

            matrices.push();
            matrices.translate(0,0,1);
            if(textComponent.getStyle() == null)
                TextUtils.drawTextOutline(new LiteralText(buttonText), moveX, moveY, textComponent.getSize(),color,lineColor,textComponent.isCentered(), matrices);
            else
                TextUtils.drawTextOutline(textComponent.getStyle().getIdentifier(), new LiteralText(buttonText), moveX, moveY, textComponent.getSize(),color,lineColor,textComponent.isCentered(), matrices);
            matrices.translate(0,0,-1);
            matrices.pop();
        } else {
            if(textComponent.getStyle() == null)
                TextUtils.drawText(new LiteralText(buttonText), moveX, moveY, textComponent.getSize(), color, textComponent.hasShadow(), textComponent.isCentered(), matrices);
            else
                TextUtils.drawText(textComponent.getStyle().getIdentifier(), new LiteralText(buttonText), moveX, moveY, textComponent.getSize(), color, textComponent.hasShadow(), textComponent.isCentered(), matrices);
        }
    }
    
}
