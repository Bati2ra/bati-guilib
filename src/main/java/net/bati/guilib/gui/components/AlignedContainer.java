package net.bati.guilib.gui.components;

import lombok.Getter;
import lombok.Setter;
import net.bati.guilib.utils.DrawUtils;
import net.bati.guilib.utils.Orientation;
import net.bati.guilib.utils.Vec2;

import java.util.ArrayList;
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
 * @version 1.1 14 Aug 2022
 */
@Setter
@Getter
public class AlignedContainer extends Container {
    protected Orientation align = Orientation.HORIZONTAL;
    protected double              spacing = 20;
    protected int                 contentSize = 0;

    protected boolean ignoreInvisibles;
    protected ArrayList<Boolean> lastStates;

    protected HashMap<String, Vec2> offsets = new HashMap<>();
    protected boolean lookForVisibilityChanges;

    protected boolean dynamicClose = true;

    protected boolean withOffsets = false;



    public AlignedContainer(String identifier) {
        super(identifier);
       // setIgnoreBox(true); // isFocused/isHovered retornará siempre verdadero, este contenedor por defecto no posee un tamaño.
    }

}
