package net.bati.guilib.widget;

import net.bati.guilib.layout.BoxModel;
import net.bati.guilib.rendering.NineSlice;
import net.minecraft.resources.Identifier;

/**
 * Panel widget - a simple container with background
 */
public class Panel extends Widget {

    public Panel(String id) {
        super(id);
        setBackgroundColor(0x80000000); // Semi-transparent black
        setPadding(BoxModel.Insets.all(8));
    }

    public Panel withBackground(int color) {
        setBackgroundColor(color);
        return this;
    }

    public Panel withNineSlice(Identifier texture) {
        setBackground(NineSlice.Presets.panel(texture));
        return this;
    }
}