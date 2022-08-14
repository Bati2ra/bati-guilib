package net.bati.guilib;

import net.bati.guilib.gui.screen.TestScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register((mc) -> {
            if(MinecraftClient.getInstance().options.sneakKey.isPressed()) {
                MinecraftClient.getInstance().setScreen(new TestScreen(null));
            }
        });
    }
}
