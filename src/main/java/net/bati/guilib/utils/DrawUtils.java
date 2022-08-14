package net.bati.guilib.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;


public class DrawUtils {
    public static void drawHorizontalGradient(MatrixStack matrices, float startX, float startY, float endX, float endY, float z, int color1, int color2, float a1, float a2) {
        drawGradient(matrices, startX, startY, endX, endY, z, color1, color2, color1, color2, a1, a2, a1, a2);
    }
    public static void drawVerticalGradient(MatrixStack matrices, float startX, float startY, float endX, float endY, float z, int color1, int color2, float a1, float a2) {
        drawGradient(matrices, startX, startY, endX, endY, z, color1, color1, color2, color2, a1, a1, a2, a2);
    }
    public static void drawGradient(MatrixStack matrices, float startX, float startY, float endX, float endY, float z, int color1, int color2, int color3, int color4, float a1, float a2, float a3, float a4) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        var tessellator = Tessellator.getInstance();
        var bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        drawGradientWithColors(matrices.peek().getPositionMatrix(), bufferBuilder, startX, startY, endX, endY, z, color1, color2, color3, color4, a1, a2, a3, a4);
        tessellator.draw();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    private static void drawGradientWithColors(Matrix4f matrix, BufferBuilder builder, float startX, float startY, float endX, float endY, float z, int color1, int color2, int color3, int color4, float a1, float a2, float a3, float a4) {
        vertex(matrix, builder, endX, startY, z, color2, a2);
        vertex(matrix, builder, startX, startY, z, color1, a1);
        vertex(matrix, builder, startX, endY, z, color3, a3);
        vertex(matrix, builder, endX, endY, z, color4, a4);


    }
    private static void vertex(Matrix4f matrix, BufferBuilder builder, float x, float y, float z, int color, float a) {
        float[] rgb = ColorUtils.convertToRGB(color);
        builder.vertex(matrix, x, y, z).color(rgb[0], rgb[1], rgb[2], a).next();

    }
}
