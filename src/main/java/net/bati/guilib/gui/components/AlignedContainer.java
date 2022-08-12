package net.bati.guilib.gui.components;

import lombok.Getter;
import lombok.Setter;
import net.bati.guilib.utils.Vec2;

import java.util.HashMap;
import java.util.Map;


/**
 * Contenedor alineado, similar al alineamiento de Figma, puede ser vertical u horizontal y aplicarse su respectivo
 * espacio entre items con {@link AlignedContainer#spacing}.
 * Importante: Al añadir componentes a este contenedor, se ignorará {@link Widget#getOffsetPosition()} ya que se le colocará uno
 * de forma dinámica, sin embargo, por como está programado el funcionamiento de un Widget, si se le coloca {@link Widget#getPositionListener()}
 * a un hijo de este objeto, se usará dicha posición, lo cual cancelará el objetivo de este contenedor.
 * @author Bautista Picca
 * @since 1.0 7 Aug 2022
 * @version 1.0 7 Aug 2022
 */
@Setter
@Getter
public class AlignedContainer extends Container {
    private Align               align = Align.HORIZONTAL;
    private double              spacing = 20;
    private int                 contentSize = 0;

    public AlignedContainer(String identifier) {
        super(identifier);
        setIgnoreBox(true); // isFocused/isHovered retornará siempre verdadero, este contenedor por defecto no posee un tamaño.
    }

    public enum Align {
        HORIZONTAL, VERTICAL
    }

    /**
     * Acomoda los Widgets dependiendo el alineamiento establecido, en este componente no se respeta el offset de los Widgets hijos,
     * por lo que se ignorará las posiciones originales a excepción de {@link Widget#getPositionListener()} el cual tiene mayor jerarquía.
     */
    @Override
    public void fit() {
        var prevEndPos = 0.0;
        for (Map.Entry<String, Widget> stringWidgetEntry : getWidgets().entrySet()) {
            var entry = stringWidgetEntry.getValue();
            if (align.equals(Align.HORIZONTAL)) {
                entry.setOffsetPosition(new Vec2((int) prevEndPos, 0));
                prevEndPos = entry.getOffsetX() + entry.getBoxWidth() * entry.getSize() + spacing;
            } else {
                entry.setOffsetPosition(new Vec2(0, (int) prevEndPos));
                prevEndPos = entry.getOffsetY() + entry.getBoxHeight() * entry.getSize() + spacing;
            }
        }
    }


}
