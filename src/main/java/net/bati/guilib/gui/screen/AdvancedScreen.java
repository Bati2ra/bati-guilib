package net.bati.guilib.gui.screen;

import net.bati.guilib.CommonInitializer;
import net.bati.guilib.gui.components.Button;
import net.bati.guilib.gui.components.Widget;
import net.bati.guilib.utils.Mouse;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AdvancedScreen extends Screen {
    public HashMap<String, Widget> widgets = new HashMap<>();
    private Screen parent;
    private Mouse mouse = new Mouse();
    private float partialTicks;
    private MatrixStack matrix;

    private Widget emptyWidget = new Button(0, 0);

    protected AdvancedScreen(@Nullable Text title) {
        super((title == null) ? new LiteralText("") : title);
    }

    public abstract void init();

    public abstract void update();

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        updateMouse(mouseX, mouseY);
        partialTicks = delta;
        matrix = matrices;
        update();

        List<Widget> result = new ArrayList<>();

        widgets.forEach((key, value)-> {
            value.setFocused(false);
            if(value.isVisible() && value.isEnabled() && value.isHovered(mouseX, mouseY))
                result.add(value);
        });

        Widget widget = (result.size() > 1) ? Collections.max(result, Comparator.comparingInt(current -> current.getZOffset())) : (result.size() == 1) ? result.get(0) : null;
        if(widget != null)
            widget.setFocused(true);


        widgets.forEach((key, value) -> {
            value.render(matrices, mouseX, mouseY, delta);
        });

    }

    public void openGui(@Nullable Screen gui) {
        MinecraftClient.getInstance().setScreen(gui);
    }

    public void setParent(Screen parent) {
        this.parent = parent;
    }

    public void updateMouse(int x, int y) {
        if(mouse == null) return;
        mouse.setX(x);
        mouse.setY(y);
    }
    public Mouse getMouse() {
        return mouse;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public MatrixStack getMatrix() {
        return matrix;
    }

    public abstract boolean shouldPauseGame();

    public abstract boolean shouldGuiCloseOnEsc();

    @Override
    public boolean shouldPause() {
        return shouldPauseGame();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return shouldGuiCloseOnEsc();
    }

    public void addWidget(Widget widget) {
        if(widgets.containsKey(widget.getIdentifier())) {
            CommonInitializer.LOGGER.warn("[{}] Widget name [{}] is repeated, skipping....", widget, widget.getIdentifier());
            return;
        }
        this.widgets.put(widget.getIdentifier(), widget);
    }

    public void addWidgets(Widget... widgets) {
        for(Widget widget : widgets)
            addWidget(widget);
    }

    public void removeWidget(String widgetIdentifier) {
        if(!widgets.containsKey(widgetIdentifier)) return;
        widgets.remove(widgetIdentifier);
    }

    public Widget getWidget(String widgetIdentifier) {
        return (widgets.containsKey(widgetIdentifier)) ? widgets.get(widgetIdentifier) : emptyWidget;
    }

    public void clear() {
        widgets.clear();
        CommonInitializer.LOGGER.info("[{}] Widgets map cleared.", this);
    }

    public void close() {
        openGui(parent);
        clear();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        widgets.forEach((key, value) ->  {
            if(value.isVisible() && value.isFocused((int)mouseX, (int)mouseY))
                value.mouseClicked(mouseX, mouseY, mouseButton);

        });

        return true;
    }


}
