package net.bati.miniui.examples;

import net.bati.miniui.rendering.Background;
import net.bati.miniui.tooltip.Tooltip;
import net.bati.miniui.tooltip.TooltipBorder;
import net.bati.miniui.tooltip.TooltipEntry;
import net.bati.miniui.tooltip.TooltipProvider;
import net.minecraft.resources.Identifier;

/**
 * Example of an advanced RPG weapon tooltip using the full tooltip system.
 *
 * Result looks like:
 *
 *   ┌─────────────────────────────────────┐
 *   │  [sword image]                      │
 *   │  Soulreaver                  EPIC   │
 *   │  ─────────────────────────────────  │
 *   │  Two-Handed Sword                   │
 *   │                                     │
 *   │  ⚔  Attack Damage    148 (+32%)     │
 *   │  ⚡  Attack Speed     1.6 / sec     │
 *   │  🩸  Life Steal        8%           │
 *   │  ❄  Crit Chance       24%           │
 *   │                                     │
 *   │  ─────────────────────────────────  │
 *   │  Effects                            │
 *   │  Soul Rend  —  On hit, reduces      │
 *   │  enemy armor by 15% for 3s.         │
 *   │  Void Strike  —  Every 5th attack   │
 *   │  deals 300% bonus shadow damage.    │
 *   │                                     │
 *   │  ─────────────────────────────────  │
 *   │  "Forged in the void between        │
 *   │   worlds, it hungers for souls."    │
 *   │                                     │
 *   │  Requires Level 45                  │
 *   └─────────────────────────────────────┘
 *
 * Usage:
 *   button.setTooltip(SoulreaverTooltip.create(stats));
 *   // or dynamic — stats update without recreating:
 *   button.setTooltip(SoulreaverTooltip.dynamic(statsSupplier));
 */
public final class SoulReaverTooltip {

    // ─── Vanilla texture identifiers ──────────────────────────────────────────

    // Item sprite — 16x16 pulled from the items atlas
    private static final Identifier SWORD_ICON =
            Identifier.withDefaultNamespace("textures/item/netherite_sword.png");

    // Minecraft's icons texture (hearts, armor, etc.)
    private static final Identifier ICONS =
            Identifier.withDefaultNamespace("textures/gui/icons.png");

    // Effect icons from the mob_effect atlas
    private static final Identifier EFFECT_STRENGTH =
            Identifier.withDefaultNamespace("textures/mob_effect/strength.png");
    private static final Identifier EFFECT_WITHER =
            Identifier.withDefaultNamespace("textures/mob_effect/wither.png");

    // ─── Rarity colors ────────────────────────────────────────────────────────

    private static final int COLOR_EPIC        = 0xFFAA00FF;
    private static final int COLOR_STAT_LABEL  = 0xFF9E9E9E;
    private static final int COLOR_STAT_VALUE  = 0xFFFFFFFF;
    private static final int COLOR_STAT_BONUS  = 0xFF55FF55;
    private static final int COLOR_EFFECT_NAME = 0xFFFFD700;
    private static final int COLOR_LORE        = 0xFFAAAAAA;
    private static final int COLOR_SEPARATOR   = 0xFF44004A;
    private static final int COLOR_REQUIRES    = 0xFFFF5555;

    // ─── Factory — static (stats captured at creation time) ──────────────────

    public static TooltipProvider create(WeaponStats stats) {
        return TooltipProvider.fixed(build(
                stats::getDamage,
                stats::getDamageBonus,
                stats::getAttackSpeed,
                stats::getLifeSteal,
                stats::getCritChance
        ));
    }

    // ─── Factory — dynamic (suppliers re-evaluated every frame) ──────────────

    public static TooltipProvider dynamic(WeaponStats stats) {
        // Suppliers are lambdas — the tooltip object is created once,
        // but the values are read fresh on every render call.
        return TooltipProvider.fixed(build(
                stats::getDamage,
                stats::getDamageBonus,
                stats::getAttackSpeed,
                stats::getLifeSteal,
                stats::getCritChance
        ));
    }

    // ─── Builder ──────────────────────────────────────────────────────────────

    private static Tooltip build(
            java.util.function.IntSupplier damage,
            java.util.function.IntSupplier damageBonus,
            java.util.function.DoubleSupplier attackSpeed,
            java.util.function.IntSupplier lifeSteal,
            java.util.function.IntSupplier critChance
    ) {
        return Tooltip.builder()
                .background(Background.color(0xF0140014))
                .border(TooltipBorder.gradient(0xFFAA00FF, 0xFF550088))
                .padding(7)
                .maxWidth(180)
                .entryGap(2)

                // ── Weapon image ───────────────────────────────────────────
                .entry(TooltipEntry.image(SWORD_ICON, 16, 16, 32, 32))
                .entry(TooltipEntry.spacer(2))

                // ── Name + rarity ──────────────────────────────────────────
                .entry(TooltipEntry.title("Soulreaver"))
                .entry(TooltipEntry.text("EPIC", COLOR_EPIC))
                .entry(TooltipEntry.separator(COLOR_SEPARATOR))

                // ── Item type ──────────────────────────────────────────────
                .entry(TooltipEntry.text("Two-Handed Sword", 0xFFCCCCCC))
                .entry(TooltipEntry.spacer(4))

                // ── Stats — dynamic suppliers ──────────────────────────────
                .entry(statLine(EFFECT_STRENGTH,
                        "Attack Damage",
                        () -> damage.getAsInt() + " §a(+" + damageBonus.getAsInt() + "%)"))
                .entry(statLine(ICONS,
                        "Attack Speed",
                        () -> String.format("%.1f / sec", attackSpeed.getAsDouble())))
                .entry(statLine(EFFECT_WITHER,
                        "Life Steal",
                        () -> lifeSteal.getAsInt() + "%"))
                .entry(statLine(ICONS,
                        "Crit Chance",
                        () -> critChance.getAsInt() + "%"))
                .entry(TooltipEntry.spacer(4))

                // ── Effects ────────────────────────────────────────────────
                .entry(TooltipEntry.separator(COLOR_SEPARATOR))
                .entry(TooltipEntry.text("Effects", 0xFFEEEEEE))
                .entry(TooltipEntry.spacer(2))
                .entry(TooltipEntry.text("Soul Rend", COLOR_EFFECT_NAME))
                .entry(TooltipEntry.multiline(
                        "On hit, reduces enemy armor by 15% for 3 seconds.",
                        COLOR_LORE))
                .entry(TooltipEntry.spacer(3))
                .entry(TooltipEntry.text("Void Strike", COLOR_EFFECT_NAME))
                .entry(TooltipEntry.multiline(
                        "Every 5th attack deals 300% bonus shadow damage.",
                        COLOR_LORE))
                .entry(TooltipEntry.spacer(4))

                // ── Lore ───────────────────────────────────────────────────
                .entry(TooltipEntry.separator(COLOR_SEPARATOR))
                .entry(TooltipEntry.multiline(
                        "§o\"Forged in the void between worlds, it hungers for souls.\"",
                        0xFF888888))
                .entry(TooltipEntry.spacer(4))

                // ── Requirements ───────────────────────────────────────────
                .entry(TooltipEntry.text("Requires Level 45", COLOR_REQUIRES))

                .build();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /**
     * A stat row: small icon + label on the left, value on the right.
     * Since iconText doesn't support right-aligned values, we format it
     * inline with padding for readability.
     */
    private static TooltipEntry statLine(Identifier icon,
                                         String label,
                                         java.util.function.Supplier<String> value) {
        return TooltipEntry.iconText(icon, 9, 9,
                () -> "§7" + label + "  §f" + value.get(),
                COLOR_STAT_VALUE);
    }

    // ─── WeaponStats interface (implement in your domain model) ──────────────

    public interface WeaponStats {
        int    getDamage();
        int    getDamageBonus();   // percentage over base
        double getAttackSpeed();
        int    getLifeSteal();     // percentage
        int    getCritChance();    // percentage
    }

    // ─── Example implementation for testing ───────────────────────────────────

    public static WeaponStats dummyStats() {
        return new WeaponStats() {
            public int    getDamage()      { return 148; }
            public int    getDamageBonus() { return 32;  }
            public double getAttackSpeed() { return 1.6; }
            public int    getLifeSteal()   { return 8;   }
            public int    getCritChance()  { return 24;  }
        };
    }
}