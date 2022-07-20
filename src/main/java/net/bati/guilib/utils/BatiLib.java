package net.bati.guilib.utils;

import net.minecraft.entity.LivingEntity;


public class BatiLib {
    public static double lerpBetween(LivingEntity entity, double color1, double color2) {
        return lerpBetween(entity, color1, color2, 25);
    }
    public static double lerpBetween(LivingEntity entity, double color1, double color2, int speed) {
        double[] colors = {color1, color2};
        int k = entity.age / speed + entity.getId();
        int l = k % colors.length;
        int i1 = (k + 1) % colors.length;

        float f1 = ((float)(entity.age % speed) ) / (float)speed;
        double rgb = colors[l] * (1.0F - f1) + colors[i1] * f1;

        return rgb;
    }
    public static double lerpTo(LivingEntity entity, double to) {
        return lerpTo(entity, to, 25);
    }
    public static double lerpTo(LivingEntity entity, double to, int speed) {
        return to*(((float)(entity.age % speed) ) / (float)speed);
    }

    public static double redondear(double pValor, int pPresicion) {
        int scale = (int) Math.pow(10, pPresicion);
        return (double) Math.round(pValor * scale) / scale;
    }
}
