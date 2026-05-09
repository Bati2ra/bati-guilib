package net.bati.miniui.rendering;

import net.bati.miniui.layout.ComputedLayout;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

/**
 * Describes and renders a widget's background.
 */
public final class Background {

    private final BackgroundRenderer renderer;

    private Background(BackgroundRenderer renderer) {
        this.renderer = renderer;
    }

    public static Background none() {
        return new Background((gfx, layout) -> {});
    }

    public static Background color(int argb) {
        return new Background(new ColorBackground(argb));
    }

    public static Background texture(Identifier id, int w, int h) {
        return new Background(TextureBackground.of(id, w, h));
    }

    public static Background of(BackgroundRenderer textureBackground) {
        return new Background(textureBackground);
    }

    public static Background nineSlice(
            Identifier id,
            int texW,
            int texH,
            int sliceTop,
            int sliceRight,
            int sliceBottom,
            int sliceLeft
    ) {
        return new Background(
                new NineSliceBackground(
                        id,
                        texW,
                        texH,
                        sliceTop,
                        sliceRight,
                        sliceBottom,
                        sliceLeft
                )
        );
    }

    public static Background nineSlice(
            Identifier id,
            int texW,
            int texH,
            int slice
    ) {
        return nineSlice(id, texW, texH, slice, slice, slice, slice);
    }

    public static Background gradient(
            int startColor,
            int endColor,
            GradientBackground.GradientDirection direction
    ) {
        return new Background(
                new GradientBackground(startColor, endColor, direction)
        );
    }

    public void render(GuiGraphics gfx, ComputedLayout layout) {
        renderer.render(gfx, layout);
    }
}