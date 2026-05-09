package net.bati.miniui.tooltip;

import net.bati.miniui.layout.ComputedLayout;
import net.bati.miniui.rendering.Background;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An immutable tooltip built from a list of {@link TooltipEntry} instances.
 *
 * <p>Use the {@link Builder} to configure background, border, padding and entries:
 *
 * <pre>
 * Tooltip tooltip = Tooltip.builder()
 *     .background(Background.color(0xF0100010))
 *     .border(TooltipBorder.vanilla())
 *     .padding(6)
 *     .entry(TooltipEntry.title("Angel Halo"))
 *     .entry(TooltipEntry.separator())
 *     .entry(TooltipEntry.text("§7Head slot"))
 *     .entry(TooltipEntry.spacer(4))
 *     .entry(TooltipEntry.iconText(STAR_ICON, () -> String.valueOf(stars)))
 *     .build();
 * </pre>
 *
 * <h3>Dynamic content</h3>
 * The {@code Tooltip} object is created once. Entries that accept
 * {@code Supplier<String>} re-evaluate their content on every render call —
 * no need to rebuild the tooltip when the underlying data changes.
 *
 * <h3>Simple shortcuts</h3>
 * <pre>
 * Tooltip.of("Simple text")
 * Tooltip.of(TooltipEntry.title("X"), TooltipEntry.text("Y"))
 * </pre>
 */
public final class Tooltip {

    // ─── Defaults ─────────────────────────────────────────────────────────────

    private static final Background     DEFAULT_BACKGROUND = Background.color(0xF0100010);
    private static final TooltipBorder  DEFAULT_BORDER     = TooltipBorder.vanilla();
    private static final int            DEFAULT_PADDING    = 6;
    private static final int            DEFAULT_GAP        = 2;  // px between entries
    private static final int            MAX_WIDTH          = 200;

    // ─── Fields ───────────────────────────────────────────────────────────────

    private final List<TooltipEntry> entries;
    private final Background         background;
    private final TooltipBorder      border;
    private final int                padding;
    private final int                entryGap;
    private final int                maxWidth;

    // ─── Constructor ──────────────────────────────────────────────────────────

    private Tooltip(Builder b) {
        this.entries    = List.copyOf(b.entries);
        this.background = b.background;
        this.border     = b.border;
        this.padding    = b.padding;
        this.entryGap   = b.entryGap;
        this.maxWidth   = b.maxWidth;
    }

    // ─── Shortcut factories ───────────────────────────────────────────────────

    /** Single line of text with default styling. */
    public static Tooltip of(String text) {
        return builder().entry(TooltipEntry.text(text)).build();
    }

    /** Arbitrary entries with default styling. */
    public static Tooltip of(TooltipEntry... entries) {
        Builder b = builder();
        for (TooltipEntry e : entries) b.entry(e);
        return b.build();
    }

    public static Builder builder() { return new Builder(); }

    /** Natural content width — what the tooltip wants to be before any constraints. */
    public int getMaxWidth() { return maxWidth; }

    /**
     * Measure the outer size (border + padding + content) at the given content width.
     * Returns [outerWidth, outerHeight]. Call this before positioning.
     */
    public int[] measure(int contentW) {
        int bt     = border.getThickness();
        int totalH = 0;
        for (int i = 0; i < entries.size(); i++) {
            totalH += entries.get(i).measureHeight(contentW);
            if (i < entries.size() - 1) totalH += entryGap;
        }
        return new int[]{ contentW + padding * 2 + bt * 2,
                totalH   + padding * 2 + bt * 2 };
    }

    // ─── Render ───────────────────────────────────────────────────────────────

    /**
     * Draw the tooltip.
     *
     * <p>The caller ({@link TooltipRenderer}) is fully responsible for
     * positioning and scale — this method only draws at the given coordinates.
     *
     * @param x        top-left X already resolved by TooltipRenderer
     * @param y        top-left Y already resolved by TooltipRenderer
     * @param contentW logical content width (before scale)
     * @param scale    uniform scale — 1.0 = no scaling
     */
    public void render(GuiGraphics gfx, int x, int y, int contentW, float scale) {
        int bt = border.getThickness();
        int[] size = measure(contentW);
        int outerW = size[0], outerH = size[1];

        gfx.pose().pushMatrix();
        if (scale != 1f) {
            gfx.pose().translate(x, y);
            gfx.pose().scale(scale, scale);
            gfx.pose().translate(-x, -y);
        }

        background.render(gfx, ComputedLayout.flat(x, y, outerW, outerH));
        border.render(gfx, x, y, outerW, outerH);

        int cx = x + bt + padding;
        int cy = y + bt + padding;
        for (int i = 0; i < entries.size(); i++) {
            TooltipEntry entry = entries.get(i);
            entry.render(gfx, cx, cy, contentW);
            cy += entry.measureHeight(contentW);
            if (i < entries.size() - 1) cy += entryGap;
        }

        gfx.pose().popMatrix();
    }

    // ─── Builder ──────────────────────────────────────────────────────────────

    public static final class Builder {
        private final List<TooltipEntry> entries    = new ArrayList<>();
        private Background               background = DEFAULT_BACKGROUND;
        private TooltipBorder            border     = DEFAULT_BORDER;
        private int                      padding    = DEFAULT_PADDING;
        private int                      entryGap   = DEFAULT_GAP;
        private int                      maxWidth   = MAX_WIDTH;

        private Builder() {}

        public Builder background(Background bg)     { this.background = bg;  return this; }
        public Builder border(TooltipBorder border)  { this.border = border;  return this; }
        public Builder padding(int px)               { this.padding = px;     return this; }
        public Builder entryGap(int px)              { this.entryGap = px;    return this; }
        public Builder maxWidth(int px)              { this.maxWidth = px;    return this; }

        public Builder entry(TooltipEntry entry) {
            this.entries.add(entry);
            return this;
        }

        public Builder entries(TooltipEntry... entries) {
            for (TooltipEntry e : entries) this.entries.add(e);
            return this;
        }

        public Tooltip build() { return new Tooltip(this); }
    }
}