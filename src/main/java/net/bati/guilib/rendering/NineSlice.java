package net.bati.guilib.rendering;

import lombok.Builder;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/**
 * Nine-slice (9-patch) texture rendering for scalable UI components.
 * Divides a texture into 9 regions to allow scaling without distorting corners.
 *
 * Layout:
 * +-------+-------+-------+
 * | TL    | Top   | TR    |
 * +-------+-------+-------+
 * | Left  | Center| Right |
 * +-------+-------+-------+
 * | BL    | Bottom| BR    |
 * +-------+-------+-------+
 */
@Getter
@Builder
public class NineSlice {

    private final Identifier texture;

    // Source texture dimensions
    @Builder.Default
    private final int textureWidth = 256;

    @Builder.Default
    private final int textureHeight = 256;

    // UV coordinates for the 9-slice regions (in pixels)
    private final int u; // Left edge of the texture region
    private final int v; // Top edge of the texture region
    private final int regionWidth; // Total width of the 9-slice region
    private final int regionHeight; // Total height of the 9-slice region

    // Slice dimensions (in pixels from edges)
    private final int sliceLeft;
    private final int sliceRight;
    private final int sliceTop;
    private final int sliceBottom;

    public static NineSlice uniform(Identifier texture, int u, int v, int width, int height, int slice) {
        return uniform(texture, u, v, width, height, slice, 0,0);
    }
        /**
         * Create a simple nine-slice with equal slices on all sides
         */
    public static NineSlice uniform(Identifier texture, int u, int v, int width, int height, int slice, int textureWidth, int textureHeight) {
        return NineSlice.builder()
                .texture(texture)
                .u(u)
                .v(v)
                .regionWidth(width)
                .regionHeight(height)
                .sliceLeft(slice)
                .sliceRight(slice)
                .sliceTop(slice)
                .sliceBottom(slice)
                .textureWidth(textureWidth)
                .textureHeight(textureHeight)
                .build();
    }

    /**
     * Render the nine-slice texture at the specified position and size
     */
    public void render(GuiGraphics context, float x, float y, float width, float height) {
        render(context, x, y, width, height, 1.0f);
    }

    /**
     * Render with opacity
     */
    public void render(GuiGraphics context, float x, float y, float width, float height, float opacity) {
        // Calculate target dimensions for each region
        float centerWidth = width - sliceLeft - sliceRight;
        float centerHeight = height - sliceTop - sliceBottom;

        // Ensure we don't have negative dimensions
        if (centerWidth < 0 || centerHeight < 0) {
            // Fall back to simple scaled rendering if too small
            renderSimple(context, x, y, width, height, opacity);
            return;
        }

        // Apply opacity
        float prevAlpha = 1.0f; // Store previous alpha if needed
        // context.setShaderColor(1.0f, 1.0f, 1.0f, opacity); // Commented - implementation specific

        // Top-left corner
        drawTextureRegion(context,
                x, y,
                sliceLeft, sliceTop,
                u, v,
                sliceLeft, sliceTop
        );

        // Top edge
        drawTextureRegion(context,
                x + sliceLeft, y,
                centerWidth, sliceTop,
                u + sliceLeft, v,
                regionWidth - sliceLeft - sliceRight, sliceTop
        );

        // Top-right corner
        drawTextureRegion(context,
                x + width - sliceRight, y,
                sliceRight, sliceTop,
                u + regionWidth - sliceRight, v,
                sliceRight, sliceTop
        );

        // Left edge
        drawTextureRegion(context,
                x, y + sliceTop,
                sliceLeft, centerHeight,
                u, v + sliceTop,
                sliceLeft, regionHeight - sliceTop - sliceBottom
        );

        // Center
        drawTextureRegion(context,
                x + sliceLeft, y + sliceTop,
                centerWidth, centerHeight,
                u + sliceLeft, v + sliceTop,
                regionWidth - sliceLeft - sliceRight, regionHeight - sliceTop - sliceBottom
        );

        // Right edge
        drawTextureRegion(context,
                x + width - sliceRight, y + sliceTop,
                sliceRight, centerHeight,
                u + regionWidth - sliceRight, v + sliceTop,
                sliceRight, regionHeight - sliceTop - sliceBottom
        );

        // Bottom-left corner
        drawTextureRegion(context,
                x, y + height - sliceBottom,
                sliceLeft, sliceBottom,
                u, v + regionHeight - sliceBottom,
                sliceLeft, sliceBottom
        );

        // Bottom edge
        drawTextureRegion(context,
                x + sliceLeft, y + height - sliceBottom,
                centerWidth, sliceBottom,
                u + sliceLeft, v + regionHeight - sliceBottom,
                regionWidth - sliceLeft - sliceRight, sliceBottom
        );

        // Bottom-right corner
        drawTextureRegion(context,
                x + width - sliceRight, y + height - sliceBottom,
                sliceRight, sliceBottom,
                u + regionWidth - sliceRight, v + regionHeight - sliceBottom,
                sliceRight, sliceBottom
        );

        // Reset opacity
        // context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Draw a single texture region
     */
    private void drawTextureRegion(
            GuiGraphics context,
            float x, float y,
            float targetWidth, float targetHeight,
            int u, int v,
            int sourceWidth, int sourceHeight
    ) {
        context.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                (int)x, (int)y,              // screen position
                (float)u, (float)v,          // texture UV offset
                (int)targetWidth,             // screen width (target)
                (int)targetHeight,            // screen height (target)
                sourceWidth,                  // source region width
                sourceHeight,                 // source region height
                textureWidth,                 // total texture width
                textureHeight,                // total texture height
                -1                            // color tint (-1 = no tint)
        );



    }

    /**
     * Fallback simple rendering for very small sizes
     */
    private void renderSimple(GuiGraphics context, float x, float y, float width, float height, float opacity) {
        context.blit(
                texture,
                (int)x, (int)y,
                u, v,
                (int)width, (int)height,
                textureWidth, textureHeight
        );
    }

    /**
     * Builder helper for common nine-slice patterns
     */
    public static class Presets {

        /**
         * Standard button with 4px slices
         */
        public static NineSlice button(Identifier texture) {
            return uniform(texture, 0, 0, 200, 20, 4);
        }

        /**
         * Panel with 8px slices
         */
        public static NineSlice panel(Identifier texture) {
            return uniform(texture, 0, 0, 256, 256, 8);
        }

        /**
         * Tooltip with 2px slices
         */
        public static NineSlice tooltip(Identifier texture) {
            return uniform(texture, 0, 0, 100, 50, 2);
        }
    }
}