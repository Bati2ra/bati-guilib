package net.bati.guilib.examples;


import net.bati.guilib.layout.EdgeInsets;
import net.bati.guilib.layout.LayoutConstraints;
import net.bati.guilib.layout.SizeConstraint;
import net.bati.guilib.layout.flex.FlexLayout;
import net.bati.guilib.rendering.Background;
import net.bati.guilib.screen.ModernScreen;
import net.bati.guilib.widget.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * Demonstrates GridContainer with square cells sized via percentage.
 *
 *  ┌──────────────────────────────────────────────┐
 *  │  HEADER: [Title]           [cols -] [cols +] │
 *  ├──────────────────────────────────────────────┤
 *  │  SCROLL                                      │
 *  │  ┌──────────────────────────────────────┐    │
 *  │  │  GRID (n columns, gap 8, ratio 1:1)  │    │
 *  │  │  ┌───────┐ ┌───────┐ ┌───────┐      │    │
 *  │  │  │  §1   │ │  §2   │ │  §3   │      │    │
 *  │  │  └───────┘ └───────┘ └───────┘      │    │
 *  │  │  ┌───────┐ ┌───────┐ ┌───────┐      │    │
 *  │  │  │  §4   │ │  §5   │ │  §6   │      │    │
 *  │  │  └───────┘ └───────┘ └───────┘      │    │
 *  │  │  ...                                │    │
 *  │  └──────────────────────────────────────┘    │
 *  ├──────────────────────────────────────────────┤
 *  │  FOOTER: [status]                            │
 *  └──────────────────────────────────────────────┘
 *
 * Key points shown:
 *  - GridContainer with configurable column count at runtime
 *  - Cell width derived from percentage of available space (no hardcoding)
 *  - cellAspectRatio(1f) keeps every button square regardless of width
 *  - Grid inside ScrollContainer so it scrolls when items overflow
 */
public class GridExampleScreen extends ModernScreen {

    // Items to display in the grid
    private static final int ITEM_COUNT = 24;

    private static final int[] ITEM_COLORS = {
            0xFF2563EB, 0xFF16A34A, 0xFFDC2626, 0xFF9333EA,
            0xFFEA580C, 0xFF0891B2, 0xFFCA8A04, 0xFF4F46E5,
    };

    private static final String[] ITEM_LABELS = {
            "§bSword", "§aShield", "§cBow", "§5Staff",
            "§6Axe",   "§3Wand",   "§eHelm", "§9Boots",
    };

    public GridExampleScreen() {
        super(Component.literal("GridContainer Demo"));
    }

    @Override
    protected void init() {
        super.init();
        setRoot(buildRoot());
    }

    // ─── Root ─────────────────────────────────────────────────────────────────

    private Widget buildRoot() {
        Panel root = new Panel("root");
        root.setConstraints(LayoutConstraints.fillParent());
        root.setBackground(Background.color(0xFF0F0F1A));

        FlexContainer mainCol = FlexContainer.column("main_col")
                .gap(0)
                .alignItems(FlexLayout.AlignItems.STRETCH);
        mainCol.setConstraints(LayoutConstraints.fillParent());

        mainCol.add(buildHeader())
                .add(Separator.horizontal("sep_top").setColor(0xFF2D2D50))
                .add(buildScrollArea())
                .add(Separator.horizontal("sep_bot").setColor(0xFF2D2D50))
                .add(buildFooter());

        root.addChild(mainCol);
        return root;
    }

    // ─── Header ───────────────────────────────────────────────────────────────

    private FlexContainer buildHeader() {
        FlexContainer header = FlexContainer.row("header")
                .justifyContent(FlexLayout.JustifyContent.SPACE_BETWEEN)
                .alignItems(FlexLayout.AlignItems.CENTER)
                .gap(6);
        header.setBackground(Background.color(0xFF16213E));
        header.setPadding(EdgeInsets.symmetric(0, 10));
        header.setConstraints(LayoutConstraints.DEFAULT
                .withHeight(SizeConstraint.fixed(28)));

        Label title = new Label("title",
                Component.literal("§b§lGrid Demo §r§7— square cells, percentage width"))
                .setColor(0xFFCCCCCC);

        Spacer spacer = Spacer.grow("spacer");

        Button close = new Button("close", Component.literal("§c✕"))
                .setColors(0xFF8B0000, 0xFFCC0000)
                .useVanillaStyle(false);
        close.setConstraints(LayoutConstraints.DEFAULT.withWidth(SizeConstraint.fixed(16)));
        close.onClick(() -> Minecraft.getInstance().setScreen(null));

        return header.add(title).add(spacer).add(close);
    }

    // ─── Scroll area with grid ────────────────────────────────────────────────

    private Widget buildScrollArea() {
        // The grid itself — 3 columns by default, 8px gap, square cells
        GridContainer grid = buildGrid(4);

        // Wrap in a panel with padding so the grid has breathing room
        Panel padded = new Panel("grid_wrapper");
        padded.setBackground(Background.color(0xFF0F0F1A));
        padded.setPadding(EdgeInsets.all(12));
        padded.add(grid);

        ScrollContainer scroll = new ScrollContainer("scroll");
        scroll.add(padded);
        scroll.setFlex(scroll.getFlexConstraints().withFlexGrow(1f));

        return scroll;
    }

    /**
     * Build the grid with {@code cols} columns.
     * Cell width = (availableWidth - gap*(cols-1)) / cols  — no hardcoding.
     * cellAspectRatio(1f) ensures every cell is a perfect square.
     */
    private GridContainer buildGrid(int cols) {
        GridContainer grid = new GridContainer("item_grid", cols)
                .gap(8)
                .cellAspectRatio(1f); // ← square cells

        for (int i = 0; i < ITEM_COUNT; i++) {
            grid.add(buildItemButton(i));
        }

        return grid;
    }

    private Button buildItemButton(int index) {
        int    colorIdx = index % ITEM_COLORS.length;
        String label    = ITEM_LABELS[colorIdx];
        int    bg       = ITEM_COLORS[colorIdx];
        int    hover    = brighten(bg);

        Button btn = new Button("item_" + index, Component.literal(label + "\n§7#" + (index + 1)))
                .setColors(bg, hover)
                .useVanillaStyle(false);

        // No width/height constraints — GridContainer sizes each cell.
        // The only thing we declare is the aspect ratio so the button stays
        // square even when the grid column width changes.
        btn.setConstraints(LayoutConstraints.DEFAULT.withAspectRatio(1f));

        final int idx = index;
        btn.onClick(() -> System.out.println("Clicked item #" + (idx + 1) + " — " + label));

        return btn;
    }

    // ─── Footer ───────────────────────────────────────────────────────────────

    private FlexContainer buildFooter() {
        FlexContainer footer = FlexContainer.row("footer")
                .alignItems(FlexLayout.AlignItems.CENTER)
                .justifyContent(FlexLayout.JustifyContent.SPACE_BETWEEN)
                .gap(8);
        footer.setBackground(Background.color(0xFF16213E));
        footer.setPadding(EdgeInsets.symmetric(0, 10));
        footer.setConstraints(LayoutConstraints.DEFAULT
                .withHeight(SizeConstraint.fixed(24)));

        Label status = new Label("status",
                Component.literal("§7" + ITEM_COUNT + " items · 3 columns · gap 8px · aspect 1:1"))
                .setColor(0xFF888888);

        Label hint = new Label("hint",
                Component.literal("§8Scroll to see all items"))
                .setColor(0xFF555555);

        return footer.add(status).add(hint);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /** Lighten a color slightly for hover state. */
    private static int brighten(int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = Math.min(255, ((argb >> 16) & 0xFF) + 30);
        int g = Math.min(255, ((argb >>  8) & 0xFF) + 30);
        int b = Math.min(255, ( argb        & 0xFF) + 30);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}