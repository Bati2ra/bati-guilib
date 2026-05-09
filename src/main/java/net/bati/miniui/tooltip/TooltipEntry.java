package net.bati.miniui.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

/**
 * A single visual row (or block) within a {@link Tooltip}.
 *
 * <p>Each entry knows two things:
 * <ol>
 *   <li>How tall it is given an available width — {@link #measureHeight(int)}</li>
 *   <li>How to draw itself at a given position — {@link #render(GuiGraphics, int, int, int)}</li>
 * </ol>
 *
 * <p>There is no layout pass, no constraints, no ComputedLayout. Entries are
 * rendered sequentially top-to-bottom — the tooltip advances Y by
 * {@code measureHeight} after each entry.
 *
 * <h3>Dynamic content</h3>
 * Entries that accept a {@code Supplier<String>} re-evaluate it on every render
 * call, so the tooltip stays up-to-date without being recreated:
 *
 * <pre>
 * TooltipEntry.text(() -> "Kills: " + stats.getKills())
 * </pre>
 *
 * <h3>Extending</h3>
 * Subclass {@code TooltipEntry} to create custom entries (e.g. 3D model previews):
 *
 * <pre>
 * public class ModelEntry extends TooltipEntry {
 *     {@literal @}Override public int measureHeight(int availableWidth) { return 64; }
 *     {@literal @}Override public void render(GuiGraphics gfx, int x, int y, int w) {
 *         // render model here
 *     }
 * }
 * </pre>
 */
public abstract class TooltipEntry {

    // ─── Core contract ────────────────────────────────────────────────────────

    /**
     * Returns the height in pixels this entry will occupy when rendered at
     * {@code availableWidth}. Called once before rendering to determine total
     * tooltip height.
     */
    public abstract int measureHeight(int availableWidth);

    /**
     * Draw this entry. {@code x}/{@code y} are the top-left corner of this
     * entry's allocated area; {@code availableWidth} is the content width.
     */
    public abstract void render(GuiGraphics gfx, int x, int y, int availableWidth);

    // ═══════════════════════════════════════════════════════════════════════════
    // BUILT-IN ENTRIES
    // ═══════════════════════════════════════════════════════════════════════════

    // ─── Text ─────────────────────────────────────────────────────────────────

    /**
     * A single line of text. If it exceeds {@code availableWidth} it is
     * clipped with an ellipsis — wrapping is handled by {@link #multiline}.
     */
    public static TooltipEntry text(String value) {
        return text(() -> value);
    }

    public static TooltipEntry text(Supplier<String> value) {
        return new TextEntry(value, 0xFFDDDDDD, false);
    }

    public static TooltipEntry text(String value, int color) {
        return text(() -> value, color);
    }

    public static TooltipEntry text(Supplier<String> value, int color) {
        return new TextEntry(value, color, false);
    }

    // ─── Title ────────────────────────────────────────────────────────────────

    /** Bold white title — rendered with Minecraft's § formatting. */
    public static TooltipEntry title(String value) {
        return title(() -> value);
    }

    public static TooltipEntry title(Supplier<String> value) {
        return new TextEntry(() -> "§f§l" + value.get(), 0xFFFFFFFF, false);
    }

    // ─── Multiline ────────────────────────────────────────────────────────────

    /** Wraps text to {@code availableWidth}. Height varies with content. */
    public static TooltipEntry multiline(String value) {
        return multiline(() -> value);
    }

    public static TooltipEntry multiline(Supplier<String> value) {
        return new MultilineEntry(value, 0xFFAAAAAA);
    }

    public static TooltipEntry multiline(String value, int color) {
        return multiline(() -> value, color);
    }

    public static TooltipEntry multiline(Supplier<String> value, int color) {
        return new MultilineEntry(value, color);
    }

    // ─── Separator ────────────────────────────────────────────────────────────

    /** Horizontal rule — 1px line with configurable color. */
    public static TooltipEntry separator() {
        return new SeparatorEntry(0xFF555577, 1);
    }

    public static TooltipEntry separator(int color) {
        return new SeparatorEntry(color, 1);
    }

    public static TooltipEntry separator(int color, int thickness) {
        return new SeparatorEntry(color, thickness);
    }

    // ─── Spacer ───────────────────────────────────────────────────────────────

    /** Empty vertical space. */
    public static TooltipEntry spacer(int height) {
        return new SpacerEntry(height);
    }

    // ─── Image ────────────────────────────────────────────────────────────────

    /**
     * A texture rendered at a fixed size.
     * If {@code width <= 0}, fills {@code availableWidth}.
     */
    public static TooltipEntry image(Identifier texture, int texW, int texH,
                                     int displayW, int displayH) {
        return new ImageEntry(texture, texW, texH, displayW, displayH);
    }

    // ─── Icon + Text ──────────────────────────────────────────────────────────

    /** Small icon (16×16) followed by text on the same line — useful for stats/costs. */
    public static TooltipEntry iconText(Identifier icon, String text) {
        return iconText(icon, () -> text);
    }

    public static TooltipEntry iconText(Identifier icon, Supplier<String> text) {
        return iconText(icon, text, 0xFFDDDDDD);
    }

    public static TooltipEntry iconText(Identifier icon, Supplier<String> text, int color) {
        return new IconTextEntry(icon, 16, 16, text, color);
    }

    public static TooltipEntry iconText(Identifier icon, int iconW, int iconH,
                                        Supplier<String> text, int color) {
        return new IconTextEntry(icon, iconW, iconH, text, color);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // IMPLEMENTATIONS
    // ═══════════════════════════════════════════════════════════════════════════

    // ─── TextEntry ────────────────────────────────────────────────────────────

    private static final class TextEntry extends TooltipEntry {
        private final Supplier<String> value;
        private final int color;
        private final boolean shadow;

        TextEntry(Supplier<String> value, int color, boolean shadow) {
            this.value  = value;
            this.color  = color;
            this.shadow = shadow;
        }

        @Override
        public int measureHeight(int availableWidth) {
            return Minecraft.getInstance().font.lineHeight;
        }

        @Override
        public void render(GuiGraphics gfx, int x, int y, int availableWidth) {
            Font tr   = Minecraft.getInstance().font;
            String       text = value.get();

            // Clip with ellipsis if text exceeds available width
            if (tr.width(text) > availableWidth) {
                String ellipsis = "…";
                String clipped  = tr.plainSubstrByWidth(text, availableWidth - tr.width(ellipsis));
                text = clipped + ellipsis;
            }

            if (shadow) gfx.drawString(tr, text, x, y, color);
            else        gfx.drawString(tr, text, x, y, color, false);
        }
    }

    // ─── MultilineEntry ───────────────────────────────────────────────────────

    private static final class MultilineEntry extends TooltipEntry {
        private final Supplier<String> value;
        private final int color;

        MultilineEntry(Supplier<String> value, int color) {
            this.value = value;
            this.color = color;
        }

        @Override
        public int measureHeight(int availableWidth) {
            Font tr   = Minecraft.getInstance().font;
            Component component = Component.literal(value.get());
            int          lines = tr.getSplitter().splitLines(component, availableWidth, component.getStyle()).size();
            return lines * (tr.lineHeight + 1);
        }

        @Override
        public void render(GuiGraphics gfx, int x, int y, int availableWidth) {
            Font tr   = Minecraft.getInstance().font;
            gfx.drawWordWrap(tr, Component.literal(value.get()), x, y, availableWidth, color);
        }
    }

    // ─── SeparatorEntry ───────────────────────────────────────────────────────

    private static final class SeparatorEntry extends TooltipEntry {
        private final int color;
        private final int thickness;

        SeparatorEntry(int color, int thickness) {
            this.color     = color;
            this.thickness = thickness;
        }

        @Override public int measureHeight(int availableWidth) { return thickness + 2; } // 1px margin each side

        @Override
        public void render(GuiGraphics gfx, int x, int y, int availableWidth) {
            gfx.fill(x, y + 1, x + availableWidth, y + 1 + thickness, color);
        }
    }

    // ─── SpacerEntry ──────────────────────────────────────────────────────────

    private static final class SpacerEntry extends TooltipEntry {
        private final int height;
        SpacerEntry(int height) { this.height = height; }

        @Override public int measureHeight(int availableWidth) { return height; }
        @Override public void render(GuiGraphics gfx, int x, int y, int w) {}
    }

    // ─── ImageEntry ───────────────────────────────────────────────────────────

    private static final class ImageEntry extends TooltipEntry {
        private final Identifier texture;
        private final int texW, texH, displayW, displayH;

        ImageEntry(Identifier texture, int texW, int texH, int displayW, int displayH) {
            this.texture  = texture;
            this.texW     = texW;
            this.texH     = texH;
            this.displayW = displayW;
            this.displayH = displayH;
        }

        @Override
        public int measureHeight(int availableWidth) { return displayH; }

        @Override
        public void render(GuiGraphics gfx, int x, int y, int availableWidth) {
            int w = displayW <= 0 ? availableWidth : displayW;
            gfx.blit(RenderPipelines.GUI_TEXTURED, texture,
                    x, y, 0f, 0f, w, displayH, texW, texH, texW, texH, -1);
        }
    }

    // ─── IconTextEntry ────────────────────────────────────────────────────────

    private static final class IconTextEntry extends TooltipEntry {
        private static final int GAP = 4;

        private final Identifier    icon;
        private final int           iconW, iconH;
        private final Supplier<String> text;
        private final int           color;

        IconTextEntry(Identifier icon, int iconW, int iconH,
                      Supplier<String> text, int color) {
            this.icon  = icon;
            this.iconW = iconW;
            this.iconH = iconH;
            this.text  = text;
            this.color = color;
        }

        @Override
        public int measureHeight(int availableWidth) {
            return Math.max(iconH, Minecraft.getInstance().font.lineHeight);
        }

        @Override
        public void render(GuiGraphics gfx, int x, int y, int availableWidth) {
            Font tr = Minecraft.getInstance().font;

            // Center icon and text vertically relative to each other
            int rowH    = measureHeight(availableWidth);
            int iconY   = y + (rowH - iconH) / 2;
            int textY   = y + (rowH - tr.lineHeight) / 2;

            gfx.blit(RenderPipelines.GUI_TEXTURED, icon,
                    x, iconY, 0f, 0f, iconW, iconH, iconW, iconH, iconW, iconH, -1);

            gfx.drawString(tr, text.get(), x + iconW + GAP, textY, color, false);
        }
    }
}