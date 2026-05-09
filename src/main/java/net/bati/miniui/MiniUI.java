package net.bati.miniui;

import net.bati.miniui.examples.GridExampleScreen;
import net.bati.miniui.examples.VirtualGridExampleScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiniUI implements ClientModInitializer {
    public static final String ID = "miniui";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    private static boolean showDebugBounds = false;

    @Override
    public void onInitializeClient() {
        /*ClientTickEvents.START_CLIENT_TICK.register((mc) -> {
            if(Minecraft.getInstance().options.keyShift.isDown()) {
                Minecraft.getInstance().setScreen(new VirtualGridExampleScreen());
            }
        });*/
    }

    public static void setShowDebugBounds(boolean showDebugBounds) {
        MiniUI.showDebugBounds = showDebugBounds;
    }

    public static boolean shouldShowDebugBounds() {
        return MiniUI.showDebugBounds;
    }
}
