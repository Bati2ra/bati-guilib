package net.bati.guilib.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bati.guilib.gui.screen.ScreenUtils;
import net.bati.guilib.utils.DrawHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.util.*;


public class Container extends Widget implements IWidgetsStorage {
    protected HashMap<String, Widget> widgets;
    private int contentX, contentY;

    protected boolean ignoreContainerHitbox = true;


    public Container() {
        widgets = new HashMap<>();
    }

    @Deprecated
    public Container shouldIgnoreContainerHitbox(boolean ignore) {
        this.ignoreContainerHitbox = ignore;
        return this;
    }

    @Override
    public boolean isHovered(int mouseX, int mouseY) {
        return /*ignoreContainerHitbox || */super.isHovered(mouseX, mouseY);
    }

    @Override
    protected void draw(MatrixStack matrices, int mouseX, int mouseY, float delta) {

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
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(!isVisible()) return;


        matrices.push();
        double scaleFactor = MinecraftClient.getInstance().getWindow().getScaleFactor();


        matrices.push();
        float offsetX = getPivot().getX(getBoxWidth());
        float offsetY = getPivot().getY(getBoxHeight());

        matrices.push();
        matrices.translate(-offsetX, -offsetY, 0);
        matrices.push();
        matrices.translate(getX() + offsetX, getY() + offsetY, 0);
        matrices.translate(0,0,50);
        matrices.translate(0,0,-50);

        matrices.scale(getTransformSize(), getTransformSize(), 1);
        matrices.translate(-offsetX, -offsetY, 0);

        /*if(!ignoreContainerHitbox) {
            if(hasParent()) {
                RenderSystem.enableScissor((int) (parent.getPivotX() * scaleFactor), (int) (MinecraftClient.getInstance().getWindow().getFramebufferHeight() - ((parent.getPivotY() + parent.getBoxHeight() * parent.getRelativeSize()) * scaleFactor)), (int) (parent.getBoxWidth() * parent.getRelativeSize() * scaleFactor), (int) (parent.getBoxHeight() * parent.getRelativeSize() * scaleFactor));

            } else {
                RenderSystem.enableScissor((int) (getPivotX() * scaleFactor), (int) (MinecraftClient.getInstance().getWindow().getFramebufferHeight() - ((getPivotY() + getBoxHeight() * getRelativeSize()) * scaleFactor)), (int) (getBoxWidth() * getRelativeSize() * scaleFactor), (int) (getBoxHeight() * getRelativeSize() * scaleFactor));

            }

        }*/
        drawBoxArea(matrices);
        /*if(!ignoreContainerHitbox) {
            RenderSystem.disableScissor();
        }*/
        matrices.translate(0,0, getZ());
        RenderSystem.setShaderColor(1,1,1, getOpacity());

        ScreenUtils.renderWidgets(getWidgets(), matrices, mouseX, mouseY, delta);

        matrices.translate(0,0, -getZ());
        RenderSystem.setShaderColor(1,1,1, 1);


        matrices.pop();
        matrices.pop();

        matrices.pop();

        matrices.pop();

    }

    @Override
    protected void drawBoxArea(MatrixStack matrices) {
        if(!isShowArea()) return;
        DrawHelper.INSTANCE.fillGradient(matrices, 0, 0, getBoxWidth(),getBoxHeight(), getRandomColor(), getOpacity(), getZ());
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
