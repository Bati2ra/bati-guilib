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

    // ─── Measure ──────────────────────────────────────────────────────────────

    @Override
    protected MeasureResult measureContent(float availableWidth, float availableHeight) {
        Font tr = Minecraft.getInstance().font;
        if (wrap) {
            int wrapWidth = availableWidth > 0 ? (int)availableWidth : Integer.MAX_VALUE;
            // Count lines for wrapped text
            int lines =  tr.getSplitter().splitLines(text, wrapWidth, text.getStyle()).size();
            return new MeasureResult(Math.min(availableWidth, tr.width(text)), lines * (tr.lineHeight + 1));
        }
        return new MeasureResult(tr.width(text), tr.lineHeight);
    }

    // ─── Render ───────────────────────────────────────────────────────────────

    @Override
    protected void renderContent(RenderPassInfo rp) {
        if (computedLayout == null) return;

        Font tr = Minecraft.getInstance().font;
        GuiGraphics  gfx = rp.graphics;

        var content = computedLayout.getContentBounds();
        float x = content.getX();
        float y = content.getY();

        if (wrap) {
            int wrapW = (int) content.getWidth();
            if (center) {
                gfx.drawWordWrap(tr, text, (int)x, (int)y, wrapW, color);
            } else {
                gfx.drawWordWrap(tr, text, (int)x, (int)y, wrapW, color);
            }
        } else {
            if (center) {
                int textW = tr.width(text);
                x = content.getX() + (content.getWidth() - textW) / 2f;
            }
            if (shadow) {
                gfx.drawString(tr, text, (int)x, (int)y, color);
            } else {
                gfx.drawString(tr, text, (int)x, (int)y, color, false);
            }
        }
    }
}