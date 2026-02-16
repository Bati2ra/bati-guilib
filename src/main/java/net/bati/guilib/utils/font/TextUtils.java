package net.bati.guilib.utils.font;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bati.guilib.utils.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class TextUtils {

    public static final TextRenderer font = MinecraftClient.getInstance().textRenderer;

    public static void drawTextOutline(Identifier font, Text text, float x, float y, float size, int baseColor, int lineColor, boolean centered, DrawContext context) {
        drawTextOutline(((MutableText)text).setStyle(text.getStyle().withFont(font)), x, y, size, baseColor, lineColor, centered, context);

    }
    public static void drawTextOutline(String text, float x, float y, float size, int baseColor, int lineColor, boolean centered, DrawContext context) {
        drawTextOutline(Text.literal(text), x, y, size, baseColor, lineColor, centered, context);
    }

    public static void drawTextOutline(Text text, float x, float y, float size, int baseColor, int lineColor, boolean centered, DrawContext context) {
        context.getMatrices().push();
        drawText(text, x- size, y, size, lineColor, false, centered, context);
        drawText(text, x+ size, y, size, lineColor, false, centered, context);
        drawText(text, x, y+ size, size, lineColor, false, centered, context);
        drawText(text, x, y- size, size, lineColor, false, centered, context);
        context.getMatrices().pop();
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 0.01f);
        drawText(text, x, y, size, baseColor, false, centered, context);
        context.getMatrices().pop();
    }

    public static void drawText(Identifier font, Text text, float x, float y, float size, int color, boolean shadow, boolean centered, DrawContext context) {
        drawText(((MutableText)text).setStyle(text.getStyle().withFont(font)), x, y, size, color, shadow, centered, context);
    }
    public static void drawText(String text, float x, float y, float size, int color, boolean shadow, boolean centered, DrawContext context) {
        drawText(Text.literal(text), x, y, size, color, shadow, centered, context);
    }

    public static void drawText(Text text, float x, float y, float size, int color, boolean shadow, boolean centered, DrawContext context) {
        context.getMatrices().scale(size, size, 1);
        float mSize = (float)Math.pow(size,-1);
        float newX = Math.round(x/size);
        float newY = Math.round(y/size);
        draw(text, centered ? newX - MinecraftClient.getInstance().textRenderer.getWidth(text)/2f : newX, newY, color, context, shadow);
        context.getMatrices().scale(mSize, mSize, 1);
    }

    public static int draw(Text text, float x, float y, int color, DrawContext context, boolean shadow) {
        if (text == null) {
            return 0;
        } else {
            RenderSystem.disableDepthTest();
            context.draw((vertexConsumerProvider -> (MinecraftClient.getInstance()).textRenderer.draw(text, (float)x, (float)y, color, shadow, context.getMatrices().peek().getPositionMatrix(), vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, 15728880)));
            RenderSystem.enableDepthTest();
            return 0;
        }
    }

    public static void drawTextComponent(TextComponent textComponent, @Nullable String text, DrawContext context, float x, float y, float z, float alpha) {
        if(textComponent == null)
            return;

        String content = (text == null) ? textComponent.getText() : text;
        int color = ColorUtils.convertToHex(textComponent.getColor(), alpha);

        if(textComponent.isOutlined()) {
            int lineColor = ColorUtils.convertToHex(textComponent.getLineColor(), alpha);

            context.getMatrices().push();
            context.getMatrices().translate(0,0,z);
            if(textComponent.getStyle() == null)
                TextUtils.drawTextOutline(Text.literal(content), x, y, textComponent.getSize(),color,lineColor, textComponent.isCentered(), context);
            else
                TextUtils.drawTextOutline(textComponent.getStyle().getIdentifier(), Text.literal(content), x, y, textComponent.getSize(),color,lineColor, textComponent.isCentered(), context);
            context.getMatrices().translate(0,0,-z);
            context.getMatrices().pop();
        } else {
            if(textComponent.getStyle() == null)
                TextUtils.drawText(Text.literal(content), x, y, textComponent.getSize(), color, textComponent.hasShadow(), textComponent.isCentered(), context);
            else
                TextUtils.drawText(textComponent.getStyle().getIdentifier(), Text.literal(content), x, y, textComponent.getSize(), color, textComponent.hasShadow(), textComponent.isCentered(), context);
        }
    }

}
