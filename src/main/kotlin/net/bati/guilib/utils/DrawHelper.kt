package net.bati.guilib.utils

import com.mojang.blaze3d.systems.RenderSystem
import net.bati.guilib.gui.components.Widget
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec3d
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

object DrawHelper {
    @JvmOverloads
    @JvmStatic
    fun drawRectangle(
        texture: Identifier?,
        x: Double,
        y: Double,
        u: Int,
        v: Int,
        width: Double,
        height: Double,
        scale: Double = 1.0,
        imageWidth: Int = 256,
        imageHeight: Int = 256,
        matrix: Matrix4f? = null,
        z: Float = 0.0F
    ) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, texture)
        val bufferBuilder = Tessellator.getInstance().buffer
        val minU = u.toDouble() / imageWidth.toDouble()
        val maxU = (u + width) / imageWidth.toDouble()
        val minV = v.toDouble() / imageHeight.toDouble()
        val maxV = (v + height) / imageHeight.toDouble()
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
        bufferBuilder.vertex(matrix, (x + scale * width).toFloat(), (y + scale * height).toFloat(), z)
            .texture(maxU.toFloat(), maxV.toFloat()).next()
        bufferBuilder.vertex(matrix, (x + scale * width).toFloat(), y.toFloat(), z)
            .texture(maxU.toFloat(), minV.toFloat()).next()
        bufferBuilder.vertex(matrix, x.toFloat(), y.toFloat(), z).texture(minU.toFloat(), minV.toFloat()).next()
        bufferBuilder.vertex(matrix, x.toFloat(), (y + scale * height).toFloat(), z)
            .texture(minU.toFloat(), maxV.toFloat()).next()
        bufferBuilder.end()
        BufferRenderer.draw(bufferBuilder)
        RenderSystem.disableBlend()
    }

    @JvmStatic
    fun drawRectangle2(
        texture: Identifier?,
        x: Double,
        y: Double,
        u: Int,
        v: Int,
        width: Double,
        height: Double,
        scalex: Double,
        scaley: Double,
        imageWidth: Int,
        imageHeight: Int,
        matrix: Matrix4f?
    ) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, texture)
        val bufferBuilder = Tessellator.getInstance().buffer
        val minU = u.toDouble() / imageWidth.toDouble()
        val maxU = (u + width) / imageWidth.toDouble()
        val minV = v.toDouble() / imageHeight.toDouble()
        val maxV = (v + height) / imageHeight.toDouble()
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
        bufferBuilder.vertex(matrix, (x + scalex * width).toFloat(), (y + scaley * height).toFloat(), 0f)
            .texture(maxU.toFloat(), maxV.toFloat()).next()
        bufferBuilder.vertex(matrix, (x + scalex * width).toFloat(), y.toFloat(), 0f)
            .texture(maxU.toFloat(), minV.toFloat()).next()
        bufferBuilder.vertex(matrix, x.toFloat(), y.toFloat(), 0f).texture(minU.toFloat(), minV.toFloat()).next()
        bufferBuilder.vertex(matrix, x.toFloat(), (y + scaley * height).toFloat(), 0f)
            .texture(minU.toFloat(), maxV.toFloat()).next()
        bufferBuilder.end()
        BufferRenderer.draw(bufferBuilder)
        RenderSystem.disableBlend()
    }

    @JvmStatic
    fun fillGradient(
        matrices: MatrixStack,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        colorStart: Int,
        a: Float,
        z: Int
    ) {
        RenderSystem.disableTexture()
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        val tessellator = Tessellator.getInstance()
        val bufferBuilder = tessellator.buffer
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        fillGradient(matrices.peek().positionMatrix, bufferBuilder, startX, startY, endX, endY, z, colorStart, a)
        tessellator.draw()
        RenderSystem.disableBlend()
        RenderSystem.enableTexture()
    }

    @JvmStatic
    fun fillGradient(
        matrix: Matrix4f?,
        builder: BufferBuilder,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        z: Int,
        colorStart: Int,
        a: Float
    ) {
        val color = Vec3d.unpackRgb(colorStart)
        builder.vertex(matrix, endX, startY, z.toFloat())
            .color(color.x.toFloat(), color.y.toFloat(), color.z.toFloat(), a).next()
        builder.vertex(matrix, startX, startY, z.toFloat())
            .color(color.x.toFloat(), color.y.toFloat(), color.z.toFloat(), a).next()
        builder.vertex(matrix, startX, endY, z.toFloat())
            .color(color.x.toFloat(), color.y.toFloat(), color.z.toFloat(), a).next()
        builder.vertex(matrix, endX, endY, z.toFloat())
            .color(color.x.toFloat(), color.y.toFloat(), color.z.toFloat(), a).next()
    }
    @JvmStatic
    fun drawWithPivot(@NotNull matrices : MatrixStack, x : Int, y : Int, width : Float, height : Float, size : Float, delta : Float, @NotNull pivot: Pivot, @NotNull drawCallback : Callback.DrawableBasic) {
        drawWithPivot(matrices, x, y, width, height, size, delta, pivot, drawCallback, null)
    }
    @JvmStatic
    fun drawWithPivot(@NotNull matrices : MatrixStack, x : Int, y : Int, width : Float, height : Float, size : Float, delta : Float, @NotNull pivot: Pivot, @NotNull drawCallback : Callback.DrawableBasic, @Nullable transformationCallback : Callback.DrawableBasic?) {
        val offsetX: Float = pivot.getX(width)
        val offsetY: Float = pivot.getY(height)

        matrices.push()

        matrices.translate(-offsetX.toDouble(), -offsetY.toDouble(), 0.0)
        matrices.push()
        matrices.translate((x + offsetX).toDouble(), (y + offsetY).toDouble(), 0.0)
        matrices.scale(size, size, size)
        transformationCallback?.draw()
        matrices.translate(-offsetX.toDouble(), -offsetY.toDouble(), 0.0)

        drawCallback.draw()

        matrices.pop()
        matrices.pop()

    }
}