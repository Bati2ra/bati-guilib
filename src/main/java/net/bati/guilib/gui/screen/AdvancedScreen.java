package net.bati.guilib.gui.screen;

import net.bati.guilib.CommonInitializer;
import net.bati.guilib.gui.components.Button;
import net.bati.guilib.gui.components.Widget;
import net.bati.guilib.utils.Mouse;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;


public abstract class AdvancedScreen extends Screen {
    protected HashMap<String, Widget> widgets = new HashMap<>();
    private Screen parent;
    private Mouse mouse = new Mouse();
    private float partialTicks;
    private MatrixStack matrix;

    private final Button emptyWidget = Button.builder("").ignoreBox(true).build();

    protected AdvancedScreen(@Nullable Text title) {
        super((title == null) ? new LiteralText("") : title);
        build();
    }

    public abstract void build();

    public abstract void update();

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        updateMouse(mouseX, mouseY);
        partialTicks = delta;
        matrix = matrices;
        update();

        ScreenUtils.renderWidgets(getWidgets(), matrices, mouseX, mouseY, delta);

        getWidgets().forEach((key, value) -> value.lastRender(matrices, mouseX, mouseY, delta));
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

    public void clear() {
        widgets.clear();
        CommonInitializer.LOGGER.info("[{}] Widgets map cleared.", this);
    }
    @Override
    public void close() {
        clear();
        openGui(parent);

    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        widgets.forEach((key, value) ->  {
            if(value.isVisible())
                value.charTyped(chr, modifiers);

        });
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        widgets.forEach((key, value) ->  {
            if(value.isVisible())
                value.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

        });

        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        widgets.forEach((key, value) ->  {
            if(value.isVisible())
                value.mouseClicked(mouseX, mouseY, mouseButton);

        });

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        widgets.forEach((key, value) ->  {
            if(value.isVisible())
                value.mouseReleased(mouseX, mouseY, button);

        });
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        widgets.forEach((key, value) ->  {
            if(value.isVisible())
                value.keyPressed(keyCode, scanCode, modifiers);

        });
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        widgets.forEach((key, value) ->  {
            if(value.isVisible())
                value.keyReleased(keyCode, scanCode, modifiers);

        });
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    public HashMap<String, Widget> getWidgets() {
        return widgets;
    }

    @Nullable
    public Widget getWidget(String key) {
        return widgets.get(key);
    }

    public void addWidget(Widget widget) {
        if(widgets.containsKey(widget.getIdentifier())) {
            CommonInitializer.LOGGER.warn("[{}] Widget name [{}] is repeated, skipping....", widget, widget.getIdentifier());
            return;
        }
        widget.init();
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

}
