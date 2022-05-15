package net.bati.guilib.utils;

public class ColorUtils {

    public static int toHex(int color) {
        return (int) Long.parseLong(String.format("%02x%02x%02x%02x", (color >> 24) & 0xFF, (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF), 16);
    }

    public static int toHex(int color, float alpha) {
        return (int) Long.parseLong(String.format("%02x%02x%02x%02x", (int)(alpha*255), (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF), 16);
    }

}
