package net.bati.guilib.utils;

import net.bati.guilib.gui.screen.AdvancedScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public class WindowOptions {
    double scaleX;
    double scaleY;
    MinecraftClient client;

    AdvancedScreen screen;

    public WindowOptions(AdvancedScreen screen, MinecraftClient mc) {
        client = mc;
        this.screen = screen;
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

        scaleX *= screen.mediaWidth(getWindow());
        scaleY *= screen.mediaHeight(getWindow());
    }

    public Window getWindow() {
        return client.getWindow();
    }


}
