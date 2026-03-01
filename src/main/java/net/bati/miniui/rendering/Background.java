package net.bati.miniui.rendering;

import net.bati.miniui.layout.ComputedLayout;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Describes and renders a widget's background.
 */
public final class Background {

    public enum Type { NONE, COLOR, TEXTURE, NINE_SLICE }

    private final Type type;
    private final int color;
    private final @Nullable Identifier texture;
    private final int texW, texH;
    private final int sliceTop, sliceRight, sliceBottom, sliceLeft;

    private Background(Type type, int color,
                       @Nullable Identifier texture, int texW, int texH,
                       int sliceTop, int sliceRight, int sliceBottom, int sliceLeft) {
        this.type       = type;
        this.color      = color;
        this.texture    = texture;
        this.texW       = texW;
        this.texH       = texH;
        this.sliceTop   = sliceTop;
        this.sliceRight = sliceRight;
        this.sliceBottom= sliceBottom;
        this.sliceLeft  = sliceLeft;
    }

    public static Background none()               { return new Background(Type.NONE, 0, null, 0, 0, 0, 0, 0, 0); }
    public static Background color(int argb)      { return new Background(Type.COLOR, argb, null, 0, 0, 0, 0, 0, 0); }
    public static Background texture(Identifier id, int w, int h) {
        return new Background(Type.TEXTURE, -1, id, w, h, 0, 0, 0, 0);
    }
    public static Background nineSlice(Identifier id, int texW, int texH,
                                       int sliceTop, int sliceRight, int sliceBottom, int sliceLeft) {
        return new Background(Type.NINE_SLICE, -1, id, texW, texH, sliceTop, sliceRight, sliceBottom, sliceLeft);
    }
    public static Background nineSlice(Identifier id, int texW, int texH, int slice) {
        return nineSlice(id, texW, texH, slice, slice, slice, slice);
    }

    public void render(GuiGraphics gfx, ComputedLayout layout) {
        ComputedLayout.Bounds b = layout.getBorderBounds();
        float x = b.getX(), y = b.getY(), w = b.getWidth(), h = b.getHeight();
        float opacity = layout.getOpacity();

        switch (type) {
            case NONE -> {}
            case COLOR -> {
                int a = (int)((color >> 24 & 0xFF) * opacity);
                int col = (a << 24) | (color & 0x00FFFFFF);
                gfx.fill((int)x, (int)y, (int)(x+w), (int)(y+h), col);
            }
            case TEXTURE -> {
                if (texture == null) return;
                gfx.blit(RenderPipelines.GUI_TEXTURED,
                        texture, (int)x, (int)y, 0f, 0f, (int)w, (int)h,
                        texW, texH, texW, texH, -1);
            }
            case NINE_SLICE -> {
                if (texture == null) return;
                new NineSliceRenderer(texture, texW, texH, sliceTop, sliceRight, sliceBottom, sliceLeft)
                        .render(gfx, x, y, w, h);
            }
        }
    }
}