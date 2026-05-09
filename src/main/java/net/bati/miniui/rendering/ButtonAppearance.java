package net.bati.miniui.rendering;

import net.bati.miniui.utils.ColorUtils;
import net.minecraft.resources.Identifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ButtonAppearance {
    public static final String IDLE = "idle";
    public static final String HOVERED = "hovered";
    public static final String DISABLED = "disabled";

    private static final Identifier TEX_IDLE =
            Identifier.withDefaultNamespace("textures/gui/sprites/widget/button.png");
    private static final Identifier TEX_HOVERED =
            Identifier.withDefaultNamespace("textures/gui/sprites/widget/button_highlighted.png");
    private static final Identifier TEX_DISABLED =
            Identifier.withDefaultNamespace("textures/gui/sprites/widget/button_disabled.png");

    private static final Background FALLBACK = Background.color(0xFF555555);
    private final Map<String, Background> states;

    private ButtonAppearance(Map<String, Background> states) {
        this.states = Collections.unmodifiableMap(states);
    }

    /**
     * Minecraft's built-in nine-slice button textures for idle, hovered and
     * disabled states. This is the default appearance for new buttons.
     */
    public static ButtonAppearance vanilla() {
        return new ButtonAppearance(Map.of(
                IDLE, Background.texture(TEX_IDLE, 200, 20),
                HOVERED, Background.texture(TEX_HOVERED, 200, 20),
                DISABLED, Background.texture(TEX_DISABLED, 200, 20)
        ));
    }

    /**
     * Two solid colors: one for idle (and disabled, slightly dimmed), one for
     * hovered. Equivalent to the old {@code setColors(bg, bgHover)}.
     */
    public static ButtonAppearance flat(int idleArgb, int hoveredArgb) {
        // Derive a dimmed disabled color from idle (reduce alpha by 40%)
        int disabledArgb = ColorUtils.dimmed(idleArgb, 0.6f);
        return new ButtonAppearance(Map.of(
                IDLE, Background.color(idleArgb),
                HOVERED, Background.color(hoveredArgb),
                DISABLED, Background.color(disabledArgb)
        ));
    }

    /**
     * Single background for all states — useful for panels or icon buttons
     * where you manage the state appearance elsewhere.
     */
    public static ButtonAppearance of(Background idle) {
        return new ButtonAppearance(Map.of(IDLE, idle));
    }

    /**
     * Start from an empty appearance and build it up with {@link #with}.
     * The fallback chain ensures undefined states always resolve safely.
     */
    public static ButtonAppearance empty() {
        return new ButtonAppearance(Map.of());
    }

    /**
     * Return a new {@code ButtonAppearance} with {@code state} mapped to
     * {@code background}. Does not modify this instance.
     *
     * <pre>
     * // Override idle for a "selected" state defined outside the library:
     * appearance.with("selected", Background.color(0xFF4A90E2))
     * </pre>
     */
    public ButtonAppearance with(String state, Background background) {
        Map<String, Background> copy = new HashMap<>(states);
        copy.put(state, background);
        return new ButtonAppearance(copy);
    }

    /**
     * Return a new appearance without the given state mapping.
     * Useful for stripping a state so it falls back to {@link #IDLE}.
     */
    public ButtonAppearance without(String state) {
        if (!states.containsKey(state)) return this;
        Map<String, Background> copy = new HashMap<>(states);
        copy.remove(state);
        return new ButtonAppearance(copy);
    }

    /**
     * Resolve the {@link Background} for {@code state} following the fallback
     * chain: exact match → {@link #IDLE} → hardcoded grey. Never returns null.
     */
    public Background resolve(String state) {
        Background bg = states.get(state);
        if (bg != null) return bg;
        bg = states.get(IDLE);
        if (bg != null) return bg;
        return FALLBACK;
    }

    /** Returns true if this appearance has an explicit mapping for {@code state}. */
    public boolean hasState(String state) {
        return states.containsKey(state);
    }
}