package net.bati.guilib.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.bati.guilib.gui.screen.ScreenUtils;
import net.bati.guilib.utils.DrawHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.util.*;
@Getter
@Setter
@SuperBuilder
public class Container extends Widget implements IWidgetsStorage {

    @Builder.Default private HashMap<String, Widget> widgets = new HashMap<>();

    @Override
    protected void draw(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        matrices.translate(0,0, getZ());
        RenderSystem.setShaderColor(1,1,1, getOpacity());

        DrawHelper.drawWithPivot(
                matrices,
                getX(),
                getY(),
                getBoxWidth(),
                getBoxHeight(),
                getTransformSize(),
                delta,
                getPivot(),
                (matrixStack, x, y, deltaTime) -> ScreenUtils.renderWidgets(getWidgets(), matrices, mouseX, mouseY, delta)
        );
        matrices.translate(0,0, -getZ());
        RenderSystem.setShaderColor(1,1,1, 1);

    }

    @Override
    protected void mouseClickCallback(double mouseX, double mouseY, int mouseButton) {
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
    protected void mouseReleaseCallback(double mouseX, double mouseY, int state) {
        if(!isVisible()) return;
        widgets.forEach((key, value) -> {
            if(value.isVisible())
                value.mouseReleased(mouseX, mouseY, state);
        });
    }

    @Override
    protected void drawBoxArea(MatrixStack matrices) {
        if(!isShowArea()) return;
        DrawHelper.fillGradient(matrices, 0, 0, getBoxWidth(),getBoxHeight(), getRandomColor(), getOpacity(), getZ());
    }

    @Deprecated
    public void fit() {
        /*
        ArrayList<Float> list = new ArrayList<>();
        ArrayList<Float> list1 = new ArrayList<>();
        ArrayList<Float> width = new ArrayList<>();
        for(Map.Entry<String, Widget> entry : widgets.entrySet()) {
            Widget value = entry.getValue();
            list.add(value.getRelativeY() - this.getY());
            list1.add((value.getRelativeX() - this.getRelativeX()));
            width.add(value.getRelativeX() + value.getRelativeBoxWidth());
        }
        Collections.sort(list);
        Collections.sort(list1);
        Collections.sort(width);
        offsetY = list.get(0);
        offsetX = list1.get(0);
       // contentX = (int) (width.get(width.size()-1).intValue() - x - offsetX);
      //  contentY = 100;
        System.out.println(width);
        widgets.forEach((key, widget) -> {
              if((widget.getRelativeX() - getX() - getOffsetX() + widget.getRelativeBoxWidth()) > contentX)
                  contentX = (int)(widget.getRelativeX() - getX() - getOffsetX() + widget.getRelativeBoxWidth());

            if((widget.getRelativeY() - getY() - getOffsetY() + widget.getRelativeBoxHeight()) > contentY)
                contentY = (int)(widget.getRelativeY() - getY() - getOffsetY() + widget.getRelativeBoxHeight());

        });
        */
    }

    @Override
    public HashMap<String, Widget> getWidgets() {
        return widgets;
    }
}
