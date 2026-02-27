package net.bati.guilib.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import net.bati.guilib.utils.DrawHelper;
import net.bati.guilib.utils.DrawUtils;
import net.bati.guilib.utils.Orientation;

/**
 * TODO ORIENTATION!!!!
 */
@SuperBuilder
public class Slider extends Widget {
    @Builder.Default private Orientation orientation = Orientation.HORIZONTAL;
    private double value;
    private double min;
    private double max;
    private double tempValue;

    public Slider(String identifier) {
        this(identifier, 50, 10);
    }

    public Slider(String identifier, int width, int height) {
        super(identifier, width, height);
        max = 1;
        orientation = Orientation.HORIZONTAL;
    }

}