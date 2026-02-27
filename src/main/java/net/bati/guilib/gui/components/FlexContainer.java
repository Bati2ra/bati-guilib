package net.bati.guilib.gui.components;

import net.bati.guilib.utils.Orientation;
import net.bati.guilib.utils.Vec2;

import java.util.ArrayList;
import java.util.Map;

public class FlexContainer extends AlignedContainer{
    private int breakLine;
    public FlexContainer(String identifier) {
        super(identifier);
    }

    public void setBreakLine(int breakLine) {
        this.breakLine = breakLine;
    }

}
