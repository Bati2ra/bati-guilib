package net.bati.miniui.tooltip;

import org.jetbrains.annotations.Nullable;

/**
 * Provides a {@link Tooltip} given the current mouse position.
 *
 * <p>Evaluated every frame by {@link TooltipRenderer} — returning {@code null}
 * hides the tooltip.
 *
 * <h3>Static tooltip</h3>
 * Content never changes — use {@link #fixed(Tooltip)}:
 * <pre>
 * widget.setTooltip(TooltipProvider.fixed(
 *     Tooltip.of(TooltipEntry.title("Angel Halo"))
 * ));
 * </pre>
 *
 * <h3>Dynamic tooltip — content depends on data</h3>
 * The tooltip object is created once; entries use {@code Supplier<String>} to
 * re-evaluate their content each frame. No need for a dynamic provider:
 * <pre>
 * widget.setTooltip(TooltipProvider.fixed(
 *     Tooltip.builder()
 *         .entry(TooltipEntry.text(() -> "Kills: " + stats.getKills()))
 *         .build()
 * ));
 * </pre>
 *
 * <h3>Dynamic tooltip — content depends on mouse position</h3>
 * Use a lambda when the tooltip itself changes based on where the mouse is:
 * <pre>
 * widget.setTooltip((mx, my) -> {
 *     MapZone zone = map.getZoneAt(mx, my);
 *     if (zone == null) return null;
 *     return Tooltip.of(TooltipEntry.title(zone.getName()));
 * });
 * </pre>
 */
@FunctionalInterface
public interface TooltipProvider {

    /**
     * Return the tooltip to show at the given mouse position, or {@code null}
     * to show nothing.
     */
    @Nullable Tooltip get(float mouseX, float mouseY);

    /** Wrap a fixed tooltip — ignores mouse position. */
    static TooltipProvider fixed(@Nullable Tooltip tooltip) {
        return (mx, my) -> tooltip;
    }
}


