package net.bati.guilib.gui.components;

import lombok.Getter;
import lombok.Setter;
import net.bati.guilib.utils.DrawUtils;
import net.bati.guilib.utils.Orientation;
import net.bati.guilib.utils.Vec2;
import net.minecraft.client.util.math.MatrixStack;

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

        if(ignoreInvisibles) {
            lastStates = new ArrayList<>();
        }
        int entryOffsetX = 0;
        int entryOffsetY = 0;

        for (Map.Entry<String, Widget> stringWidgetEntry : getWidgets().entrySet()) {
            var entry = stringWidgetEntry.getValue();
            if(ignoreInvisibles) {
                lastStates.add(entry.isVisible());
                if(!entry.isVisible()) continue;
            }


            entryWidth = entry.getBoxWidth() * entry.getSize();
            entryHeight = entry.getBoxHeight() * entry.getSize();

            // Calculo el offset dependiendo el pivote que tenga el widget para aplicar el desplazamiento correcto
            pivotOffsetX = entry.getPivot().getX( entryWidth);
            pivotOffsetY = entry.getPivot().getY( entryHeight);


            // Widget.offsetPosition pasa a ser utilizado como offset dentro del AlignedContainer siempre y cuando este activado.
            if(withOffsets) {
                if (!offsets.containsKey(entry.getIdentifier())) {
                    offsets.put(entry.getIdentifier(), entry.getOffsetPosition());
                }
                entryOffsetX = offsets.get(entry.getIdentifier()).getX();
                entryOffsetY = offsets.get(entry.getIdentifier()).getY();
            }

            if (align.equals(Orientation.HORIZONTAL)) {
                entry.setOffsetPosition(new Vec2((int) (tempWidth + pivotOffsetX + entryOffsetX), (int) pivotOffsetY + entryOffsetY));

                tempHeight = Math.max(tempHeight, entryHeight + entryOffsetY);

                tempWidth = entry.getX() + entryWidth + spacing;
            } else {
                entry.setOffsetPosition(new Vec2((int) pivotOffsetX + entryOffsetX, (int) (tempHeight + pivotOffsetY + entryOffsetY)));

                tempWidth = Math.max(tempWidth, entryWidth + entryOffsetX);

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

    public void lookForUpdates() {
        if(!ignoreInvisibles || lastStates == null) return;

        var entrySet = getWidgets().entrySet();
        if(entrySet.size() != lastStates.size()) {
            fit();
        }

        int index = 0;
        for (Map.Entry<String, Widget> stringWidgetEntry : entrySet) {
            if(stringWidgetEntry.getValue().isVisible() != lastStates.get(index)) {
                fit();
            }
            index++;
        }
    }

    @Override
    protected void draw(MatrixStack matrices, float mouseX, float mouseY, float delta) {
        if(lookForVisibilityChanges) {
            lookForUpdates();
        }
        super.draw(matrices, mouseX, mouseY, delta);
    }

    public void setDynamicClose(boolean b) {
        dynamicClose = b;
    }

    public boolean shouldDynamicClose() {
        return dynamicClose;
    }

    public void setIgnoreInvisibles(boolean ignoreInvisibles) {
        this.ignoreInvisibles = ignoreInvisibles;
    }

    public void setLookForVisibilityChanges(boolean lookForVisibilityChanges) {
        this.lookForVisibilityChanges = lookForVisibilityChanges;
    }

    public void withOffsets(boolean b) {
        this.withOffsets = b;
    }

    /*
    @Override
    protected void draw(MatrixStack matrices, float mouseX, float mouseY, float delta) {
        DrawUtils.drawHorizontalGradient(matrices, getX(), getY(), getX() + getBoxWidth()*getSize(), getY() + getBoxHeight() * getSize(), 0, 1, 1, 0.5f, 0.5f);

        super.draw(matrices, mouseX, mouseY, delta);
    }*/
}
