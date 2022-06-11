package net.bati.guilib;

import net.bati.guilib.gui.screen.TestScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(MinecraftClient.getInstance().options.sprintKey.isPressed())
                MinecraftClient.getInstance().setScreen(new TestScreen(null));
        });
    }
}
