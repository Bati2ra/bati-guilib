package net.bati.guilib.gui.screen;

import net.bati.guilib.gui.components.Container;
import net.bati.guilib.gui.components.Widget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class ScreenUtils {
    public static void renderWidgets(HashMap<String, Widget> widgets, MatrixStack matrices, float x, float y, float delta) {
        widgets.forEach((key, value) -> value.preRender(matrices, x,y,delta));

        Optional<Map.Entry<String, Widget>> widgetEntry = widgets.entrySet().stream().filter((entry) -> entry.getValue().isVisible() && entry.getValue().isHovered() && !entry.getValue().isIgnoreBox()).max(Comparator.comparingInt(current -> current.getValue().getRecursiveZ()));
        // Si el componente tiene ignoreBox, no se checará si el widget se encuentra hovered, solo se checará la Z
        widgets.forEach((key, value) -> {
            if(value.isIgnoreBox()) {
                value.setFocused(true);
            } else {

                if (value.hasParent())
                    value.setFocused(widgetEntry.isPresent() && key.contentEquals(widgetEntry.get().getKey()) && widgetEntry.get().getValue().getParent().isFocused());
                else
                    value.setFocused(widgetEntry.isPresent() && key.contentEquals(widgetEntry.get().getKey()));
            }


            value.render(matrices, x, y, delta);

            value.lastRender(matrices, x, y, delta);

        });
    }

}
