package net.bati.miniui.widget;

import net.bati.miniui.layout.MeasureResult;
import net.bati.miniui.rendering.RenderPassInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Renders a single line of text (or a multi-line {@link Component} component).
 */
public class Label extends Widget {

    private Component    text;
    private int     color        = 0xFFFFFFFF;
    private boolean shadow       = true;
    private boolean wrap         = false;   // word-wrap
    private boolean center       = false;
    private boolean truncate     = false;

    public Label(String id, Component text) {
        super(id);
        this.text = text;
    }

    public Label(String id, String text) {
        this(id, Component.literal(text));
    }

    // ─── Fluent ───────────────────────────────────────────────────────────────

    public Label setText(Component t)       { this.text   = t;      invalidateLayout(); return this; }
    public Label setText(String s)     { return setText(Component.literal(s)); }
    public Label setColor(int argb)    { this.color  = argb;   return this; }
    public Label setShadow(boolean s)  { this.shadow = s;      return this; }
    public Label setWrap(boolean w)    { this.wrap   = w;      invalidateLayout(); return this; }
    public Label setCenter(boolean c)  { this.center = c;      return this; }
    /** Clip text with "…" if it exceeds the available content width. */
    public Label setTruncate(boolean t)  { this.truncate = t;    return this; }

    // ─── Measure ──────────────────────────────────────────────────────────────

    @Override
    protected MeasureResult measureContent(float availableWidth, float availableHeight) {
        Font tr = Minecraft.getInstance().font;
        if (wrap) {
            int wrapWidth = availableWidth > 0 ? (int) availableWidth : Integer.MAX_VALUE;
            int lines = tr.getSplitter().splitLines(text, wrapWidth, text.getStyle()).size();
            return new MeasureResult(Math.min(availableWidth, tr.width(text)), lines * (tr.lineHeight + 1));
        }
        // In truncate mode the natural width is capped to availableWidth so the
        // label doesn't push its parent wider than the available space.
        float textW = tr.width(text);
        if (truncate && availableWidth > 0) textW = Math.min(textW, availableWidth);
        return new MeasureResult(textW, tr.lineHeight);
    }

    // ─── Render ───────────────────────────────────────────────────────────────

    @Override
    protected void renderContent(RenderPassInfo rp) {
        if (computedLayout == null) return;

        Font tr = Minecraft.getInstance().font;
        GuiGraphics  gfx = rp.graphics;

        var   content = computedLayout.getContentBounds();
        float x       = content.getX();
        float y       = content.getY();
        int   maxW    = (int) content.getWidth();

        if (wrap) {
            gfx.drawWordWrap(tr, text, (int) x, (int) y, maxW, color);
            return;
        }

        // Resolve the text to render — truncate with ellipsis if needed
        Component rendered = text;
        if (truncate && maxW > 0 && tr.width(text) > maxW) {
            // plainSubstrByWidth strips formatting; use the ordered string path
            // to preserve style while still truncating.
            String ellipsis = "…";
            int ellipsisW   = tr.width(ellipsis);
            String clipped  = tr.plainSubstrByWidth(text.getString(), maxW - ellipsisW);
            rendered        = Component.literal(clipped + ellipsis);
        }

        if (center) {
            x = content.getX() + (maxW - tr.width(rendered)) / 2f;
        }

        if (shadow) {
            gfx.drawString(tr, rendered, (int) x, (int) y, color);
        } else {
            gfx.drawString(tr, rendered, (int) x, (int) y, color, false);
        }
    }
}