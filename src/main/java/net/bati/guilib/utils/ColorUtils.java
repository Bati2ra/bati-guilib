package net.bati.guilib.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.math.Vec3d;

public class ColorUtils {
/*
    @JvmStatic
    fun toHex(color: Int): Int {
        return String.format("%02x%02x%02x%02x", color shr 24 and 0xFF, color shr 16 and 0xFF, color shr 8 and 0xFF, color and 0xFF).toLong(16).toInt()
    }
    @JvmStatic
    fun toHex(color: Int, alpha: Float): Int {
        return String.format("%02x%02x%02x%02x", (alpha * 255).toInt(), color shr 16 and 0xFF, color shr 8 and 0xFF, color and 0xFF).toLong(16).toInt()
    }
    }*/
    public static void setShaderColor(int color) {
        setShaderColor(color, 1);
    }
    public static void setShaderColor(int color, float alpha) {
        var h2 = (color >> 16 & 0xff) / 255.0F;
        var h3 = (color >> 8 & 0xff) / 255.0f;
        var h4 = (color & 0xff) / 255.0f;
        RenderSystem.setShaderColor(h2, h3, h4, alpha);
    }

    public static float[] convertToRGB(int color, float alpha) {
        var h2 = (color >> 16 & 0xff) / 255.0F;
        var h3 = (color >> 8 & 0xff) / 255.0f;
        var h4 = (color & 0xff) / 255.0f;
        return new float[]{h2, h3, h4, alpha};
    }

    public static float[] convertToRGB(int color, int alpha) {
        return convertToRGB(color, alpha / 255.0F);
    }

    public static float[] convertToRGB(int color) {
        return convertToRGB(color, (color >> 24 & 0xff) / 255.0F);
    }

    public static int convertToHex(int r, int g, int b, int a) {
        return ((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
    }

    public static int convertToHex(float r, float g, float b, float a) {
        return convertToHex((int)(r * 255), (int)(g * 255), (int)(b * 255), (int)(a * 255));
    }

    public static int convertToHex(Vec3d color, float alpha) {
        return convertToHex((float)color.x, (float)color.y, (float)color.z, alpha);
    }

    public static int convertToHex(Vec3d color) {
        return convertToHex((float)color.x, (float)color.y, (float)color.z, 1F);
    }
    public static int convertToHex(int color, float a) {
        return (((int)(a*255) & 0xff) << 24) | ((color & 0xff) << 16) | ((color & 0xff) << 8) | (color & 0xff);
    }
}
