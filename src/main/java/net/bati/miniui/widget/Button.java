package net.bati.miniui.widget;

import lombok.Getter;
import net.bati.miniui.layout.EdgeInsets;
import net.bati.miniui.layout.MeasureResult;
import net.bati.miniui.rendering.ButtonAppearance;
import net.bati.miniui.rendering.RenderPassInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * A clickable button with label and optional hover/press states.
 */
public class Button extends Widget {

    @Getter
    private Component    label;
    @Getter
    private int     textColor         = 0xFFFFFFFF;
    @Getter
    private int     textColorDisabled = 0xFFA0A0A0;
    @Getter
    private int     textColorHovered  = 0xFFFFFFA0;

    private ButtonAppearance appearance = ButtonAppearance.vanilla();

    public Button(String id, Component label) {
        super(id);
        this.label = label;
        setPadding(EdgeInsets.symmetric(4, 8));
    }

    public Button(String id, String label) {
        this(id, Component.literal(label));
    }

    // ─── Fluent ───────────────────────────────────────────────────────────────

    public Button setLabel(Component t)         { this.label = t; invalidateLayout(); return this; }
    public Button setLabel(String s)       { return setLabel(Component.literal(s)); }
    public Button setTextColor(int c)      { this.textColor = c; return this; }

    /** Replace the full appearance. */
    public Button setAppearance(ButtonAppearance a) {
        this.appearance = a;
        return this;
    }

    /**
     * Shorthand for a flat two-color appearance.
     * Equivalent to {@code setAppearance(ButtonAppearance.flat(idle, hovered))}.
     */
    public Button setColors(int idle, int hovered) {
        return setAppearance(ButtonAppearance.flat(idle, hovered));
    }

    /**
     * Switch to (or back to) vanilla Minecraft button textures.
     * Equivalent to {@code setAppearance(ButtonAppearance.vanilla())}.
     */
    public Button useVanillaStyle(boolean vanilla) {
        if (vanilla) setAppearance(ButtonAppearance.vanilla());
        return this;
    }

    /**
     * Returns the current state key used to resolve the appearance.
     * Override in subclasses to add custom states.
     */
    protected String resolveState() {
        if (!enabled) return ButtonAppearance.DISABLED;
        if (hovered)  return ButtonAppearance.HOVERED;
        return ButtonAppearance.IDLE;
    }

    // ─── Measure ──────────────────────────────────────────────────────────────

    @Override
    protected MeasureResult measureContent(float aw, float ah) {
        Font tr  = Minecraft.getInstance().font;
        return new MeasureResult(tr.width(label), tr.lineHeight);
    }

    // ─── Render ───────────────────────────────────────────────────────────────

    @Override
    protected void renderBackground(RenderPassInfo rp) {
        if (computedLayout == null) return;
        appearance.resolve(resolveState()).render(rp.graphics, computedLayout);
    }

    @Override
    protected void renderContent(RenderPassInfo rp) {
        if (computedLayout == null) return;

        Font tr  = Minecraft.getInstance().font;
        GuiGraphics  gfx = rp.graphics;

        var content = computedLayout.getContentBounds();
        float cx = content.getX() + content.getWidth()  / 2f - tr.width(label) / 2f;
        float cy = content.getY() + content.getHeight() / 2f - tr.lineHeight / 2f;

        int color = !enabled ? textColorDisabled
                : hovered  ? textColorHovered
                :            textColor;

        gfx.drawString(tr, label, (int)cx, (int)cy, color);
    }
}