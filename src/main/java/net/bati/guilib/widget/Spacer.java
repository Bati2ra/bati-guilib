package net.bati.guilib.widget;

import net.bati.guilib.layout.FlexConstraints;
import net.bati.guilib.layout.LayoutConstraints;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Spacer widget for flex layouts
 */
public class Spacer extends Widget {

    public Spacer(String id, float width, float height) {
        super(id);
        setContentSize(width, height);
    }

    /**
     * Create flexible spacer that grows to fill available space
     */
    public static Spacer flexible(String id) {
        Spacer spacer = new Spacer(id, 0, 0);
        spacer.setFlexConstraints(FlexConstraints.builder().flexGrow(1).build());
        return spacer;
    }

    @Override
    protected void renderContent(GuiGraphics context, float mouseX, float mouseY, float delta) {
        // Spacers don't render anything
    }
}