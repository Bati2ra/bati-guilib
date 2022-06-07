package net.bati.guilib.utils;

import com.mojang.blaze3d.systems.RenderSystem;

public class ColorUtils {

    public static int toHex(int color) {
        return (int) Long.parseLong(String.format("%02x%02x%02x%02x", (color >> 24) & 0xFF, (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF), 16);
    }

    public static int toHex(int color, float alpha) {
        return (int) Long.parseLong(String.format("%02x%02x%02x%02x", (int)(alpha*255), (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF), 16);
    }

    public static void hexColor(int pColor, float alpha) {
        float h2 = (pColor >> 16 & 0xFF) / 255.0F;
        float h3 = (pColor >> 8 & 0xFF) / 255.0F;
        float h4 = (pColor & 0xFF) / 255.0F;
        float h1 = 1.0F;
        RenderSystem.setShaderColor(h1 * h2, h1 * h3, h1 * h4, alpha);
    }


}
