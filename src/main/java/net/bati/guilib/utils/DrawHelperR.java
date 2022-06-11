package net.bati.guilib.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
@Deprecated
public class DrawHelperR {
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


}
