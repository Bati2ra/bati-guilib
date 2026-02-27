package net.bati.guilib.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bati.guilib.gui.screen.ScreenUtils;
import net.bati.guilib.utils.DrawHelper;
import net.bati.guilib.utils.DrawUtils;
import net.bati.guilib.utils.Pivot;
import net.bati.guilib.utils.font.TextUtils;

/**
 * Accordion con funcionamiento similar al equivalente de Bootstrap, usado en conjunto con {@link AlignedContainer} y
 * Align Vertical se consigue el efecto correcto.
 * @author Bautista Picca
 * @version 1.0 7 Aug 2022
 * @since 1.0 7 Aug 2022
 * @see AlignedContainer
 */
public class Accordion extends Container {
    public Accordion(String identifier) {
        super(identifier);
    }
}
