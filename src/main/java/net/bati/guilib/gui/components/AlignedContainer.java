package net.bati.guilib.gui.components;

import lombok.Getter;
import lombok.Setter;
import net.bati.guilib.utils.DrawUtils;
import net.bati.guilib.utils.Orientation;
import net.bati.guilib.utils.Vec2;
import net.minecraft.client.util.math.MatrixStack;

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
    private Orientation align = Orientation.HORIZONTAL;
    private double              spacing = 20;
    private int                 contentSize = 0;

    private boolean dynamicClose = true;

    public AlignedContainer(String identifier) {
        super(identifier);
       // setIgnoreBox(true); // isFocused/isHovered retornará siempre verdadero, este contenedor por defecto no posee un tamaño.
    }


    /**
     * Acomoda los Widgets dependiendo el alineamiento establecido, en este componente no se respeta el offset de los Widgets hijos,
     * por lo que se ignorará las posiciones originales a excepción de {@link Widget#getPositionListener()} el cual tiene mayor jerarquía.
     *
     * Actualmente, se ejecuta solo al crear el Widget, por lo que cambios al tamaño de los hijos no se verán afectados en su posición en este Widget.
     * Para un futuro cambio podría implementar dicha opción
     */
    @Override
    public void fit() {

        var tempWidth = 0.0;
        var tempHeight = 0.0;
        var pivotOffsetX = 0.0;
        var pivotOffsetY = 0.0;
        var entryWidth = 0.0F;
        var entryHeight = 0.0F;
        for (Map.Entry<String, Widget> stringWidgetEntry : getWidgets().entrySet()) {
            var entry = stringWidgetEntry.getValue();

            entryWidth = entry.getBoxWidth() * entry.getSize();
            entryHeight = entry.getBoxHeight() * entry.getSize();

            // Calculo el offset dependiendo el pivote que tenga el widget para aplicar el desplazamiento correcto
            pivotOffsetX = entry.getPivot().getX( entryWidth);
            pivotOffsetY = entry.getPivot().getY( entryHeight);

            if (align.equals(Orientation.HORIZONTAL)) {
                entry.setOffsetPosition(new Vec2((int) (tempWidth + pivotOffsetX), (int) pivotOffsetY));

                tempHeight = Math.max(tempHeight, entryHeight);

                tempWidth = entry.getX() + entryWidth + spacing;
            } else {
                entry.setOffsetPosition(new Vec2((int) pivotOffsetX, (int) (tempHeight + pivotOffsetY)));

                tempWidth = Math.max(tempWidth, entryWidth);

                tempHeight = entry.getY() + entryHeight + spacing;
            }
        }
        if(align.equals(Orientation.HORIZONTAL)) {
            tempWidth -= spacing;
        } else {
            tempHeight -= spacing;
        }
        setBoxHeight((int) tempHeight);
        setBoxWidth((int) tempWidth);

    }

    public void setDynamicClose(boolean b) {
        dynamicClose = b;
    }

    public boolean shouldDynamicClose() {
        return dynamicClose;
    }

/*
    @Override
    protected void draw(MatrixStack matrices, float mouseX, float mouseY, float delta) {
        DrawUtils.drawHorizontalGradient(matrices, getX(), getY(), getX() + getBoxWidth()*getSize(), getY() + getBoxHeight() * getSize(), 0, 1, 1, 0.5f, 0.5f);

        super.draw(matrices, mouseX, mouseY, delta);
    }*/
}
