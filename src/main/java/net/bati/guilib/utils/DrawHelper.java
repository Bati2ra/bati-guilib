package net.bati.guilib.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;


public class DrawHelper {
    public static void drawRectangle(Identifier texture, double x, double y, int u, int v, double width, double height, double scale, int imageWidth, int imageHeight) {
        drawRectangle(texture,x, y, u, v, width, height,scale,imageWidth, imageHeight, null);
    }


    public static void drawRectangle(Identifier texture,double x, double y, int u, int v, double width, double height,double scale, int imageWidth, int imageHeight, Matrix4f matrix)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        double minU = (double)u / (double)imageWidth;
        double maxU = (double)(u + width) / (double)imageWidth;
        double minV = (double)v / (double)imageHeight;
        double maxV = (double)(v + height) / (double)imageHeight;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix,(float)(x + scale*(double)width),(float)( y + scale*(double)height), 0).texture((float)maxU,(float)maxV).next();
        bufferBuilder.vertex(matrix, (float)(x + scale*(double)width), (float)y, 0).texture((float)maxU,(float)minV).next();
        bufferBuilder.vertex(matrix, (float)x, (float)y, 0).texture((float)minU,(float)minV).next();
        bufferBuilder.vertex(matrix, (float)x, (float)(y + scale*(double)height),0).texture((float)minU,(float)maxV).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);

        RenderSystem.disableBlend();
    }
    public static void drawRectangle(Identifier texture,double x, double y, int u, int v, double width, double height,double scalex, double scaley, int imageWidth, int imageHeight, Matrix4f matrix)
    {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        double minU = (double)u / (double)imageWidth;
        double maxU = (double)(u + width) / (double)imageWidth;
        double minV = (double)v / (double)imageHeight;
        double maxV = (double)(v + height) / (double)imageHeight;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix,(float)(x + scalex*(double)width),(float)( y + scaley*(double)height), 0).texture((float)maxU,(float)maxV).next();
        bufferBuilder.vertex(matrix, (float)(x + scalex*(double)width), (float)y, 0).texture((float)maxU,(float)minV).next();
        bufferBuilder.vertex(matrix, (float)x, (float)y, 0).texture((float)minU,(float)minV).next();
        bufferBuilder.vertex(matrix, (float)x, (float)(y + scaley*(double)height),0).texture((float)minU,(float)maxV).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);

        RenderSystem.disableBlend();
    }

    public static void fillGradient(MatrixStack matrices, float startX, float startY, float endX, float endY, int colorStart, float a, int z) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        fillGradient(matrices.peek().getPositionMatrix(), bufferBuilder, startX, startY, endX, endY, z, colorStart, a);
        tessellator.draw();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void fillGradient(Matrix4f matrix, BufferBuilder builder, float startX, float startY, float endX, float endY, int z, int colorStart, float a) {
        Vec3d color = Vec3d.unpackRgb(colorStart);
        builder.vertex(matrix, (float)endX, (float)startY, (float)z).color((float)color.x, (float)color.y, (float)color.z, a).next();
        builder.vertex(matrix, (float)startX, (float)startY, (float)z).color((float)color.x, (float)color.y, (float)color.z, a).next();
        builder.vertex(matrix, (float)startX, (float)endY, (float)z).color((float)color.x, (float)color.y, (float)color.z, a).next();
        builder.vertex(matrix, (float)endX, (float)endY, (float)z).color((float)color.x, (float)color.y, (float)color.z, a).next();

    }



    public static void hexColor(int pColor, float alpha) {
        float h2 = (pColor >> 16 & 0xFF) / 255.0F;
        float h3 = (pColor >> 8 & 0xFF) / 255.0F;
        float h4 = (pColor & 0xFF) / 255.0F;
        float h1 = 1.0F;
        RenderSystem.setShaderColor(h1 * h2, h1 * h3, h1 * h4, alpha);
    }

    public static void drawFilledArc(int xCenter, int yCenter, int radius, double startDegrees, double finishDegrees, int color, float opacity, MatrixStack matrices) {
        matrices.push();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        hexColor(color, opacity);
        GL11.glBegin(6);
        GL11.glVertex2d(xCenter, yCenter);
        double i;
        for (i = startDegrees; i <= finishDegrees; i += 0.05D) {
            double theta = Math.PI*2 * i / 360.0D;
            double dotX = xCenter + Math.sin(theta) * radius;
            double dotY = yCenter + Math.cos(theta) * radius;
            GL11.glVertex2d(dotX , dotY);
        }
        GL11.glEnd();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        matrices.pop();
    }
}
