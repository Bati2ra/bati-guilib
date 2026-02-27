package net.bati.guilib.widget;

import net.bati.guilib.layout.BoxModel;
import net.bati.guilib.rendering.NineSlice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Example button widget using the new architecture
 */
public class Button extends Widget {

    private Component text;
    private int textColor = 0xFFFFFFFF;
    private int hoveredTextColor = 0xFFFFFFA0;
    private int pressedTextColor = 0xFFFFFF00;

    public Button(String id, Component text) {
        super(id);
        this.text = text;

        // Set default styling
        setBackgroundColor(0xFF404040);
        setPadding(BoxModel.Insets.symmetric(4, 12));

        // Default size based on text
        Font textRenderer = Minecraft.getInstance().font;
        int textWidth = textRenderer.width(text);
        setContentSize(textWidth, textRenderer.lineHeight);

        // Setup event handlers
        getEventHandlers().onClick(this::handleClick);
    }

    private void handleClick(Widget widget) {
        // Override in usage or add custom handler
    }

    @Override
    protected void renderContent(GuiGraphics context, float mouseX, float mouseY, float delta) {
        if (getComputedLayout() == null) return;

        Font textRenderer = Minecraft.getInstance().font;

        // Get content bounds (inside padding)
        var contentBounds = getComputedLayout().getContentBounds();

        // Choose text color based on state
        int color = switch (getState()) {
            case PRESSED -> pressedTextColor;
            case HOVERED -> hoveredTextColor;
            default -> textColor;
        };

        // Center text in content area
        float textX = contentBounds.getX() + (contentBounds.getWidth() - textRenderer.width(text)) / 2;
        float textY = contentBounds.getY() + (contentBounds.getHeight() - textRenderer.lineHeight) / 2;

        context.drawString(
                textRenderer,
                text,
                (int)textX,
                (int)textY,
                color,
                true
        );
    }

    public Button withNineSlice(Identifier texture, int slice) {
        setBackground(NineSlice.uniform(texture, 0, 0, 200, 20, slice));
        return this;
    }

    public Button withColors(int normal, int hovered, int pressed) {
        this.textColor = normal;
        this.hoveredTextColor = hovered;
        this.pressedTextColor = pressed;
        return this;
    }
}