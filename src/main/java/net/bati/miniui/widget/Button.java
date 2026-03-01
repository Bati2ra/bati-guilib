package net.bati.miniui.widget;

import net.bati.miniui.layout.EdgeInsets;
import net.bati.miniui.layout.MeasureResult;
import net.bati.miniui.rendering.RenderPassInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * A clickable button with label and optional hover/press states.
 */
public class Button extends Widget {

    /** Vanilla-style button texture (200×20 in widgets.png). */
    private static final Identifier BUTTON_TEXTURE =
            Identifier.withDefaultNamespace("textures/gui/sprites/widget/button.png");
    private static final Identifier BUTTON_HOVER_TEXTURE =
            Identifier.withDefaultNamespace("textures/gui/sprites/widget/button_highlighted.png");
    private static final Identifier BUTTON_DISABLED_TEXTURE =
            Identifier.withDefaultNamespace("textures/gui/sprites/widget/button_disabled.png");

    private Component    label;
    private int     textColor         = 0xFFFFFFFF;
    private int     textColorDisabled = 0xFFA0A0A0;
    private int     textColorHovered  = 0xFFFFFFA0;
    private boolean useVanillaStyle   = true;
    private int     bgColor           = 0xFF555555;
    private int     bgColorHover      = 0xFF777777;

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
    public Button useVanillaStyle(boolean v){ this.useVanillaStyle = v; return this; }
    public Button setColors(int bg, int bgHover) {
        this.bgColor = bg; this.bgColorHover = bgHover;
        this.useVanillaStyle = false;
        return this;
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
        var b = computedLayout.getBorderBounds();
        var gfx = rp.graphics;

        if (useVanillaStyle) {
            Identifier tex = !enabled ? BUTTON_DISABLED_TEXTURE
                    : hovered  ? BUTTON_HOVER_TEXTURE
                    :            BUTTON_TEXTURE;
            // Simple stretch blit (no nine-slice for vanilla button – it has its own internal slice)
            gfx.blit(RenderPipelines.GUI_TEXTURED, tex,
                    (int)b.getX(), (int)b.getY(), 0f, 0f,
                    (int)b.getWidth(), (int)b.getHeight(),
                    (int)b.getWidth(), (int)b.getHeight(),
                    (int)b.getWidth(), (int)b.getHeight(), -1);
        } else {
            int col = hovered && enabled ? bgColorHover : bgColor;
            float op = computedLayout.getOpacity();
            int a = (int)((col >> 24 & 0xFF) * op);
            int filled = (a << 24) | (col & 0x00FFFFFF);
            gfx.fill((int)b.getX(), (int)b.getY(),
                    (int)(b.getX()+b.getWidth()), (int)(b.getY()+b.getHeight()), filled);
        }
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