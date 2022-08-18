package net.bati.guilib.gui.components;

import net.bati.guilib.utils.Orientation;
import net.bati.guilib.utils.Vec2;

import java.util.Map;

public class FlexContainer extends AlignedContainer{
    private int breakLine;
    public FlexContainer(String identifier) {
        super(identifier);
    }

    public void setBreakLine(int breakLine) {
        this.breakLine = breakLine;
    }



    @Override
    public void fit() {
        if(breakLine == 0) {
            super.fit();
            return;
        }

        var tempWidth = 0.0;
        var tempHeight = 0.0;
        var pivotOffsetX = 0.0;
        var pivotOffsetY = 0.0;
        var entryWidth = 0.0F;
        var entryHeight = 0.0F;

        int index = 0;
        int jump = 0;
        var actualWidth = 0.0;
        var actualHeight = 0.0;

        for (Map.Entry<String, Widget> stringWidgetEntry : getWidgets().entrySet()) {
            var entry = stringWidgetEntry.getValue();

            entryWidth = entry.getBoxWidth() * entry.getSize();
            entryHeight = entry.getBoxHeight() * entry.getSize();

            // Calculo el offset dependiendo el pivote que tenga el widget para aplicar el desplazamiento correcto
            pivotOffsetX = entry.getPivot().getX( entryWidth);
            pivotOffsetY = entry.getPivot().getY( entryHeight);

            if (align.equals(Orientation.HORIZONTAL)) {

                entry.setOffsetPosition(new Vec2((int) (((index != 0 && (index) % breakLine == 0) ? 0 : tempWidth) + pivotOffsetX), (int) (pivotOffsetY + (entryHeight + spacing) * jump)));
                tempHeight = Math.max(tempHeight, (entryHeight * (jump + 1)) + (spacing * jump));

                tempWidth = entry.getX() +  entryWidth + spacing;
                if((index + 1) % breakLine == 0) {
                    jump++;
                    actualWidth = Math.max(actualWidth, tempWidth);
                }

                index++;
            } else {
                entry.setOffsetPosition(new Vec2((int) (pivotOffsetX + (entryWidth + spacing) * jump), (int) (((index != 0 && (index) % breakLine == 0) ? 0 : tempHeight) + pivotOffsetY)));
                tempWidth = Math.max(tempWidth, (entryWidth * (jump + 1)) + (spacing * jump));

                tempHeight = entry.getY() +  entryHeight + spacing;
                if((index + 1) % breakLine == 0) {
                    jump++;
                    actualHeight = Math.max(actualHeight, tempHeight);
                }

                index++;
            }
        }
        if(align.equals(Orientation.HORIZONTAL)) {
            actualWidth -= spacing;
            actualHeight = tempHeight;
        } else {
            actualHeight -= spacing;
            actualWidth = tempWidth;
        }
        setBoxHeight((int) actualHeight);
        setBoxWidth((int) actualWidth);

    }
}
