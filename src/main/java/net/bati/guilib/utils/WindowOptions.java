package net.bati.guilib.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public class WindowOptions {
    double scaleX;
    double scaleY;
    MinecraftClient client;

    public WindowOptions(MinecraftClient mc) {
        client = mc;
    }


    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public void update() {
        scaleX = client.getWindow().getScaledWidth()/427f;
        scaleY = client.getWindow().getScaledHeight()/240f;
    }

    public Window getWindow() {
        return client.getWindow();
    }


}
