package net.bati.guilib.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Text label widget
 */
public class Label extends Widget {

    private Component text;
    private int color = 0xFFFFFF;
    private boolean shadow = true;
    private TextAlignment alignment = TextAlignment.LEFT;

    public Label(String id, Component text) {
        super(id);
        this.text = text;

        // Size based on text
        Font textRenderer = Minecraft.getInstance().font;
        setContentSize(textRenderer.width(text), textRenderer.lineHeight);
    }

    @Override
    protected void renderContent(GuiGraphics context, float mouseX, float mouseY, float delta) {
        if (getComputedLayout() == null) return;

        Font textRenderer = Minecraft.getInstance().font;
        var contentBounds = getComputedLayout().getContentBounds();

        float textWidth = textRenderer.width(text);
        float x = contentBounds.getX();

        // Apply alignment
        x = switch (alignment) {
            case CENTER -> contentBounds.getX() + (contentBounds.getWidth() - textWidth) / 2;
            case RIGHT -> contentBounds.getX() + contentBounds.getWidth() - textWidth;
            default -> contentBounds.getX();
        };

        context.drawString(
                textRenderer,
                text,
                (int)x,
                (int)contentBounds.getY(),
                color,
                shadow
        );
    }

    public Label withColor(int color) {
        this.color = color;
        return this;
    }

    public Label withShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public Label withAlignment(TextAlignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public enum TextAlignment {
        LEFT, CENTER, RIGHT
    }
}