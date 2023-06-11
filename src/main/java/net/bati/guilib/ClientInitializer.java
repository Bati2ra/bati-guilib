package net.bati.guilib;

import net.bati.guilib.gui.screen.TestScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientInitializer implements ClientModInitializer {
    public static final String ID = "bati-guilib";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    @Override
    public void onInitializeClient() {
        /*ClientTickEvents.START_CLIENT_TICK.register((mc) -> {
            if(MinecraftClient.getInstance().options.sneakKey.isPressed()) {
                MinecraftClient.getInstance().setScreen(new TestScreen(null));
            }
        });*/
    }
}
