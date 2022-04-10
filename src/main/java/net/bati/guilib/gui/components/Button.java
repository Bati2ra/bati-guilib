package net.bati.guilib.gui.components;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.bati.guilib.utils.*;
import net.bati.guilib.utils.font.TextComponent;
import net.bati.guilib.utils.font.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class Button extends Widget {
    protected static final Identifier WIDGETS_LOCATION = new Identifier("textures/gui/widgets.png");
    protected TextureComponent textureComponent = new TextureComponent(0, 46, 256, 256, WIDGETS_LOCATION);
    protected Runnable leftClickCallback;
    protected Runnable rightClickCallback;

    protected SoundEvent sound = SoundEvents.UI_BUTTON_CLICK;
    protected SoundEvent error = SoundEvents.UI_BUTTON_CLICK;
    private boolean shouldPlaySound = false;

    protected TextComponent textComponent = new TextComponent();
    private boolean isPressed;

    public Button(int x, int y) {
        boxWidth = 200;
        boxHeight = 20;
        this.x = x;
        this.y = y;
    }

    public Button(int x, int y, int width, int height, TextComponent textComponent, TextureComponent textureComponent, PIVOT pivot) {
        this.x = x;
        this.y = y;
        boxWidth = width;
        boxHeight = height;
        this.textComponent = textComponent;
        this.textureComponent = textureComponent;
        this.pivot = pivot;
    }
    @Override
    public void mouseClick(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 1) {
            onRightClick();
        } else {
            onLeftClick();
        }
    }

    @Override
    public void mouseRelease(double mouseX, double mouseY, int state) {

    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        return false;
    }

    private void shouldPlaySound(int mouseX, int mouseY) {
        if(!isFocused(mouseX, mouseY)) {
            shouldPlaySound = true;
            return;
        }

        if(shouldPlaySound) {
            if(enabled && isFocused(mouseX, mouseY))
                playSound(sound);

            shouldPlaySound = !shouldPlaySound;
        }
    }

    private void playSound(SoundEvent sound) {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(sound, 1, 0.5f));
    }

    protected int getYImage(boolean isHovered) {
        return (!isEnabled()) ? 0 : (isHovered) ? 2 : 1;
    }

    private void drawText(MatrixStack matrices) {
        if(textComponent == null) return;

        int moveX = textComponent.isCentered() ? Math.round(getBoxWidth() / 2) :  Math.round(textComponent.getOffsetPosition().getX());
        int moveY = textComponent.isCentered() ? Math.round((getBoxHeight() / 2 - ((textComponent.hasStyle() ? textComponent.getStyle().getHeight() : font.fontHeight)* textComponent.getSize())/2)) : Math.round(textComponent.getOffsetPosition().getY());

        String buttonText = textComponent.getText();
        int strWidth = (int) (MinecraftClient.getInstance().textRenderer.getWidth(buttonText)*textComponent.getSize());
        int ellipsisWidth = (int) (MinecraftClient.getInstance().textRenderer.getWidth("...") * textComponent.getSize());

        if (strWidth > boxWidth*size - 20 && strWidth > ellipsisWidth)
            buttonText = MinecraftClient.getInstance().textRenderer.trimToWidth(buttonText, (int) (strWidth + boxWidth*size - 20 - ellipsisWidth)).trim() + "...";

        if(textComponent.isOutlined()) {
            matrices.push();
            matrices.translate(0,0,1);
            if(textComponent.getStyle() == null)
                TextUtils.drawTextOutline(new LiteralText(buttonText), moveX, moveY, textComponent.getSize(),textComponent.getColor(),textComponent.getLineColor(),textComponent.isCentered(), matrices);
            else
                TextUtils.drawTextOutline(textComponent.getStyle().getId(), new LiteralText(buttonText), moveX, moveY, textComponent.getSize(),textComponent.getColor(),textComponent.getLineColor(),textComponent.isCentered(), matrices);
            matrices.translate(0,0,-1);
            matrices.pop();
        } else {
            if(textComponent.getStyle() == null)
                TextUtils.drawText(new LiteralText(buttonText), moveX, moveY, textComponent.getSize(), textComponent.getColor(), textComponent.hasShadow(), textComponent.isCentered(), matrices);
            else
                TextUtils.drawText(textComponent.getStyle().getId(), new LiteralText(buttonText), moveX, moveY, textComponent.getSize(), textComponent.getColor(), textComponent.hasShadow(), textComponent.isCentered(), matrices);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        shouldPlaySound(mouseX, mouseY);

        if(textComponent == null) return;

        int i = getYImage(isFocused(mouseX, mouseY));
        matrices.translate(0,0,getZOffset());
        RenderSystem.setShaderColor(1,1,1,opacity);


        float offsetX = pivot.getX(getBoxWidth());
        float offsetY = pivot.getY(getBoxHeight());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        float expand = size /**  g*/;


        matrices.push();
        matrices.translate(-offsetX, -offsetY, 0);
        matrices.push();
        matrices.translate(getX() + offsetX, getY() + offsetY, 0);
        matrices.translate(0,0,50);
        DrawHelper.fillGradient(matrices, -4, -4, 4, 4, 50000, 1, 0);
        matrices.translate(0,0,-50);
        //if(isHovered(mouseX, mouseY) && isEnabled())
        //    GL11.glRotated(25* FunctionHelper.colorLerpInteger(mc.player, -0.1f, 0.1f, 20), 0, 0, 1);

        matrices.scale(expand, expand, expand);
        matrices.translate(-offsetX, -offsetY, 0);

        RenderSystem.setShaderColor(1,1,1,0.5f);
        DrawHelper.drawRectangle(textureComponent.getResource(), 0, 0, textureComponent.getU(), textureComponent.getV() + ((isPressed()) ? 2 : i) * getBoxHeight(), getBoxWidth()/2, getBoxHeight(), 1, textureComponent.getTextureWidth(), textureComponent.getTextureHeight(), matrices.peek().getPositionMatrix());
        DrawHelper.drawRectangle(textureComponent.getResource(), getBoxWidth()/2, 0, Math.round((textureComponent.getU()+this.getBoxWidth()/2)), textureComponent.getV() + ((isPressed()) ? 2 : i) * getBoxHeight(), getBoxWidth()/2, this.getBoxHeight(), 1, textureComponent.getTextureWidth(), textureComponent.getTextureHeight(), matrices.peek().getPositionMatrix());

        drawText(matrices);
        matrices.pop();
        matrices.pop();

        //if(pivot.equals(PIVOT.LEFT)) {
         /*   matrices.push();
            matrices.scale(size, size, size);
            float originalSize = (float)Math.pow(size, -1);
            DrawHelper.drawRectangle(textureComponent.getResource(), (getX()/size), Math.round(getY()/size), textureComponent.getU(), textureComponent.getV() + ((isPressed()) ? 2 : i) * getBoxHeight(), this.boxWidth/2, this.boxHeight, 1, textureComponent.getTextureWidth(), textureComponent.getTextureHeight(), matrices.peek().getPositionMatrix());
            DrawHelper.drawRectangle(textureComponent.getResource(), (((getX()+this.boxWidth/(2)*size))/size), Math.round(getY()/size), Math.round((textureComponent.getU()+this.getBoxWidth()/2)), textureComponent.getV() + ((isPressed()) ? 2 : i) * getBoxHeight(), this.boxWidth/2, this.getBoxHeight(), 1, textureComponent.getTextureWidth(), textureComponent.getTextureHeight(), matrices.peek().getPositionMatrix());
         //   matrices.translate(getX(), getY(), 0);
            drawText(matrices);

            matrices.scale(originalSize, originalSize, originalSize);
            matrices.pop();*/
      //  } else {
            /*
            middle top = width/2, 0
            middle = width/2 height/2
            middle bot = width/2, height

            left top = 0 , 0
            left middle = 0, height/2
            left bot = 0 , height

            right top = width , 0
            right middle = width , height/2
            right bot = width, height



             */


       // }
        matrices.translate(0,0, -getZOffset());


    }

    public void setPressed(boolean s) {
        this.isPressed = true;
    }

    public boolean isPressed() {
        return isPressed;
    }
    public void setSuccessSound(SoundEvent sound) {
        this.sound = sound;
    }

    public void setErrorSound(SoundEvent sound) {
        this.error = sound;
    }

    private void onLeftClick() {
        if(leftClickCallback == null) return;
        leftClickCallback.run();
        playSound(sound);
    }

    private void onRightClick() {
        if(rightClickCallback == null) return;
        rightClickCallback.run();
        playSound(sound);
    }

    public void registerRightClickListener(Runnable runnable) {
        this.rightClickCallback = runnable;
    }

    public void registerLeftClickListener(Runnable runnable) {
        this.leftClickCallback = runnable;
    }

    public Button textComponent(TextComponent textComponent) {
        this.textComponent = textComponent;
        return this;
    }

    
}
