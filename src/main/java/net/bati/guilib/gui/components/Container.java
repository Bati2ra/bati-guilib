package net.bati.guilib.gui.components;

import net.bati.guilib.gui.screen.ScreenUtils;
import net.bati.guilib.utils.DrawHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.util.*;
import java.util.stream.Collectors;

public class Container extends Widget implements IWidgetsStorage {
    protected HashMap<String, Widget> widgets;
    private int contentX, contentY;


    public Container() {
        widgets = new HashMap<>();
    }
    @Override
    public void mouseClick(double mouseX, double mouseY, int mouseButton) {
        if(!isEnabled()) return;
        widgets.forEach((key, value) ->  {
            if(value.isVisible() && value.isFocused((int)mouseX, (int)mouseY))
                value.mouseClicked(mouseX, mouseY, mouseButton);

        });
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(!isEnabled()) return false;
        widgets.forEach((key, value) ->  {
            if(value.isVisible() && value.isFocused((int)mouseX, (int)mouseY))
                value.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        });
        return true;
    }

    @Override
    public void mouseRelease(double mouseX, double mouseY, int state) {
        if(!isVisible()) return;
        widgets.forEach((key, value) -> {
            if(value.isVisible())
                value.mouseReleased(mouseX, mouseY, state);
        });
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        return false;
    }


    @Override
    public int getBoxWidth() {
        return boxWidth + contentX;
    }

    @Override
    public int getBoxHeight() {
        return boxHeight + contentY;
    }

    @Override
    public int getX() {
        return x + getOffsetX();
    }


    @Override
    public int getY() {
        return y + getOffsetY();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        ScreenUtils.renderWidgets(getWidgets(), matrices, mouseX, mouseY, delta);
    }

    public void fit() {
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Integer> list1 = new ArrayList<>();
        for(Map.Entry<String, Widget> entry : widgets.entrySet()) {
            Widget value = entry.getValue();
            list.add(value.getY());
            list1.add((value.getX()));
        }
        Collections.sort(list);
        Collections.sort(list1);
        offsetY = list.get(0);
        offsetX = list1.get(0);

        widgets.forEach((key, widget) -> {

            if(widget.getX() + widget.getBoxWidth() > contentX)
                contentX = widget.getX() - offsetX + widget.getBoxWidth();



            if(widget.getY() + widget.getBoxHeight() > contentY)
                contentY = widget.getY() - offsetY + widget.getBoxHeight();



        });

    }

    @Override
    public HashMap<String, Widget> getWidgets() {
        return widgets;
    }
}
