package net.bati.guilib.utils.font;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bati.guilib.utils.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class TextUtils {

    public static final TextRenderer font = MinecraftClient.getInstance().textRenderer;

    public static void drawTextOutline(String text, float x, float y, float size, int baseColor, int lineColor, boolean centered) {
        drawTextOutline(text, x, y, size, baseColor, lineColor, centered, null);
    }

    public static void drawTextOutline(Identifier font, Text text, float x, float y, float size, int baseColor, int lineColor, boolean centered) {
        drawTextOutline(((MutableText)text).setStyle(text.getStyle().withFont(font)), x, y, size, baseColor, lineColor, centered, null);
    }
    public static void drawTextOutline(Identifier font, Text text, float x, float y, float size, int baseColor, int lineColor, boolean centered, MatrixStack matrix) {
        drawTextOutline(((MutableText)text).setStyle(text.getStyle().withFont(font)), x, y, size, baseColor, lineColor, centered, matrix);

    }
    public static void drawTextOutline(String text, float x, float y, float size, int baseColor, int lineColor, boolean centered, MatrixStack matrix) {
        drawTextOutline(new LiteralText(text), x, y, size, baseColor, lineColor, centered, matrix);
    }

    public static void drawTextOutline(Text text, float x, float y, float size, int baseColor, int lineColor, boolean centered, MatrixStack matrix) {
        matrix.push();
        matrix.translate(0, 0, -0.01f);
        drawText(text, x-1f*size, y, size, lineColor, false, centered, matrix);
        drawText(text, x+1f*size, y, size, lineColor, false, centered, matrix);
        drawText(text, x, y+1f*size, size, lineColor, false, centered, matrix);
        drawText(text, x, y-1f*size, size, lineColor, false, centered, matrix);
        matrix.pop();
        drawText(text, x, y, size, baseColor, false, centered, matrix);
    }

    public static void drawText(String text, float x, float y, float size, int color, boolean shadow, boolean centered) {
        drawText(text,  x,  y, size,color, shadow, centered, null);
    }

    public static void drawText(Identifier font, Text text, float x, float y, float size, int color, boolean shadow, boolean centered) {
        drawText(((MutableText)text).setStyle(text.getStyle().withFont(font)),  x,  y, size,color, shadow, centered, null);
    }

    public static void drawText(Text text, float x, float y, float size, int color, boolean shadow, boolean centered) {
        drawText(text,  x,  y, size,color, shadow, centered, null);
    }

    public static void drawText(Identifier font, Text text, float x, float y, float size, int color, boolean shadow, boolean centered, MatrixStack matrix) {
        drawText(((MutableText)text).setStyle(text.getStyle().withFont(font)), x, y, size, color, shadow, centered, matrix);
    }
    public static void drawText(String text, float x, float y, float size, int color, boolean shadow, boolean centered, MatrixStack matrix) {
        drawText(new LiteralText(text), x, y, size, color, shadow, centered, matrix);
    }

    public static void drawText(Text text, float x, float y, float size, int color, boolean shadow, boolean centered, MatrixStack matrix) {
        matrix.scale(size, size, size);
        float mSize = (float)Math.pow(size,-1);
        float newX = Math.round(x/size);
        float newY = Math.round(y/size);
        draw(text, centered ? newX - MinecraftClient.getInstance().textRenderer.getWidth(text)/2 : newX, newY, color, matrix, shadow);
        matrix.scale(mSize, mSize, mSize);
    }

    public static int draw(Text text, float x, float y, int color, MatrixStack matrix, boolean shadow) {
        if (text == null) {
            return 0;
        } else {
            RenderSystem.enableBlend();
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            int i = (MinecraftClient.getInstance()).textRenderer.draw(text, x, y, color, shadow, matrix.peek().getPositionMatrix(), immediate, false, 0, 15728880);

            immediate.draw();
            RenderSystem.disableBlend();
            return i;
        }
    }

    public static void drawTextComponent(TextComponent textComponent, @Nullable String text, MatrixStack matrices, float x, float y, float z, float alpha) {
        if(textComponent == null)
            return;

        String content = (text == null) ? textComponent.getText() : text;
        int color = ColorUtils.toHex(textComponent.getColor(), alpha);

        if(textComponent.isOutlined()) {
            int lineColor = ColorUtils.toHex(textComponent.getLineColor(), alpha);

            matrices.push();
            matrices.translate(0,0,z);
            if(textComponent.getStyle() == null)
                TextUtils.drawTextOutline(new LiteralText(content), x, y, textComponent.getSize(),color,lineColor, textComponent.isCentered(), matrices);
            else
                TextUtils.drawTextOutline(textComponent.getStyle().id(), new LiteralText(content), x, y, textComponent.getSize(),color,lineColor, textComponent.isCentered(), matrices);
            matrices.translate(0,0,-z);
            matrices.pop();
        } else {
            if(textComponent.getStyle() == null)
                TextUtils.drawText(new LiteralText(content), x, y, textComponent.getSize(), color, textComponent.hasShadow(), textComponent.isCentered(), matrices);
            else
                TextUtils.drawText(textComponent.getStyle().id(), new LiteralText(content), x, y, textComponent.getSize(), color, textComponent.hasShadow(), textComponent.isCentered(), matrices);
        }
    }

}
