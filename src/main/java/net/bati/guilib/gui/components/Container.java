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

    public Container(String identifier) {
        super(identifier, 1, 1);
        setWidgets(new HashMap<>()); // SuperBuilder ignora los valores por defecto si no se usa el builder, por lo que hay que inicializar todos los atributos
    }

    @Override
    protected void draw(MatrixStack matrices, float mouseX, float mouseY, float delta) {
        matrices.push();
        matrices.translate(0,0, getZ());
        RenderSystem.setShaderColor(1,1,1, getRecursiveOpacity());

        DrawHelper.drawWithPivot(
                matrices,
                getOffsetX(),
                getOffsetY(),
                getBoxWidth(),
                getBoxHeight(),
                getSize(),
                delta,
                getPivot(),
                () -> ScreenUtils.renderWidgets(getWidgets(), matrices, mouseX, mouseY, delta)
        );
        matrices.translate(0,0, -getZ());
        RenderSystem.setShaderColor(1,1,1, 1);
        matrices.pop();

    }

    @Override
    public void lastRender(MatrixStack matrices, float mouseX, float mouseY, float delta) {
        widgets.forEach((key, value) -> value.lastRender(matrices, mouseX, mouseY, delta));
    }

    @Override
    public void onMouseClick(double mouseX, double mouseY, int mouseButton) {
        if(!isEnabled() || !isHovered(mouseX, mouseY)) return;
        widgets.forEach((key, value) ->  {
            if(value.isVisible())
                value.mouseClicked(mouseX, mouseY, mouseButton);

        });
    }

    @Override
    public void onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(!isEnabled() || !isHovered(mouseX, mouseY)) return;
        widgets.forEach((key, value) ->  {
            if(value.isVisible())
                value.onMouseDrag(mouseX, mouseY, button, deltaX, deltaY);
        });
    }

    @Override
    public void onMouseRelease(double mouseX, double mouseY, int state) {
        if(!isEnabled() || !isHovered(mouseX, mouseY)) return;
        widgets.forEach((key, value) -> {
            if(value.isVisible())
                value.mouseReleased(mouseX, mouseY, state);
        });
    }

    @Override
    public void onKeyPress(int keyCode, int scanCode, int modifiers) {
        if(!isEnabled() || !isHovered(getMouseX(), getMouseY())) return;
        widgets.forEach((key, value) -> {
            if(value.isVisible())
                value.onKeyPress(keyCode, scanCode, modifiers);
        });
    }

    @Override
    public void onKeyRelease(int keyCode, int scanCode, int modifiers) {
        if(!isEnabled() || !isHovered(getMouseX(), getMouseY())) return;
        widgets.forEach((key, value) -> {
            if(value.isVisible())
                value.onKeyRelease(keyCode, scanCode, modifiers);
        });
    }

    @Override
    public void onCharType(char chr, int modifiers) {
        if(!isEnabled() || !isHovered(getMouseX(), getMouseY())) return;
        widgets.forEach((key, value) -> {
            if(value.isVisible())
                value.onCharType(chr, modifiers);
        });
    }

    @Override
    public void onMouseScroll(double mouseX, double mouseY, double amount) {
        if(!isEnabled() || !isHovered(getMouseX(), getMouseY())) return;
        widgets.forEach((key, value) -> {
            if(value.isVisible())
                value.onMouseScroll(mouseX, mouseY, amount);
        });
        super.onMouseScroll(mouseX, mouseY, amount);
    }

    public void fit() {}
    @Override
    public HashMap<String, Widget> getWidgets() {
        return widgets;
    }

    @Override
    public boolean isHovered(double mouseX, double mouseY) {
        return isIgnoreBox() || super.isHovered(mouseX, mouseY);
    }
}
