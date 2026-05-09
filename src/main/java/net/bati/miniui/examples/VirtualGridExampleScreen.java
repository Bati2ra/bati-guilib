package net.bati.miniui.examples;

import net.bati.miniui.layout.EdgeInsets;
import net.bati.miniui.layout.LayoutConstraints;
import net.bati.miniui.layout.SizeConstraint;
import net.bati.miniui.layout.flex.FlexLayout;
import net.bati.miniui.rendering.Background;
import net.bati.miniui.screen.ModernScreen;
import net.bati.miniui.widget.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Cosmetics screen: sidebar with category filters + virtual grid of items.
 *
 *  ┌──────────────────────────────────────────────────────┐
 *  │  HEADER                                              │
 *  ├────────┬─────────────────────────────────────────────┤
 *  │        │  SCROLL                                     │
 *  │ SIDE   │  ┌──────────────────────────────────────┐   │
 *  │  BAR   │  │  VirtualGridContainer (4 cols)       │   │
 *  │        │  │  [ ][ ][ ][ ]  ← only visible rows   │   │
 *  │ [cat1] │  │  [ ][ ][ ][ ]    are real widgets    │   │
 *  │ [cat2] │  │  [ ][ ][ ][ ]                        │   │
 *  │ [cat3] │  └──────────────────────────────────────┘   │
 *  │        │                                             │
 *  └────────┴─────────────────────────────────────────────┘
 */
public class VirtualGridExampleScreen extends ModernScreen {
// ─── Data model ───────────────────────────────────────────────────────────

    public record CosmeticItem(String id, String name, String category, int color) {}

    private static final String[] CATEGORIES = { "All", "Hats", "Capes", "Wings", "Auras", "Pets" };

    private static final int[] PALETTE = {
            0xFF2563EB, 0xFF16A34A, 0xFFDC2626, 0xFF9333EA,
            0xFFEA580C, 0xFF0891B2, 0xFFCA8A04, 0xFF4F46E5,
            0xFF0D9488, 0xFFDB2777, 0xFF65A30D, 0xFF7C3AED,
    };

    // ─── State ────────────────────────────────────────────────────────────────

    private final List<CosmeticItem>         allItems;
    private       String                     selectedCategory = "All";
    private       VirtualScrollGrid<Button, CosmeticItem> grid;
    private       Label                      countLabel;

    // ─── Constructor ──────────────────────────────────────────────────────────

    public VirtualGridExampleScreen() {
        super(Component.literal("Cosmetics"));
        this.allItems = generateDummyItems(500);
    }

    @Override
    protected void init() {
        super.init();
        setRoot(buildRoot());
        applyFilter(selectedCategory);
    }

    // ─── Root layout ──────────────────────────────────────────────────────────

    private Widget buildRoot() {
        Panel root = new Panel("root");
        root.setConstraints(LayoutConstraints.fillParent());
        root.setBackground(Background.color(0xFF0F0F1A));

        FlexContainer mainCol = FlexContainer.column("main_col").gap(0)
                .alignItems(FlexLayout.AlignItems.STRETCH);
        mainCol.setConstraints(LayoutConstraints.fillParent());

        mainCol.add(buildHeader())
                .add(Separator.horizontal("sep").setColor(0xFF2D2D50))
                .add(buildBody());

        root.addChild(mainCol);
        return root;
    }

    // ─── Header ───────────────────────────────────────────────────────────────

    private Widget buildHeader() {
        FlexContainer header = FlexContainer.row("header")
                .alignItems(FlexLayout.AlignItems.CENTER)
                .justifyContent(FlexLayout.JustifyContent.SPACE_BETWEEN)
                .gap(8);
        header.setBackground(Background.color(0xFF16213E));
        header.setPadding(EdgeInsets.symmetric(0, 10));
        header.setConstraints(LayoutConstraints.DEFAULT
                .withHeight(SizeConstraint.fixed(28)));

        Label title = new Label("title", Component.literal("§b§lCosmetics"))
                .setColor(0xFFCCCCCC);

        countLabel = new Label("count", Component.literal(""))
                .setColor(0xFF888888);

        Button close = new Button("close", Component.literal("§c✕"))
                .setColors(0xFF8B0000, 0xFFCC0000)
                .useVanillaStyle(false);
        close.setConstraints(LayoutConstraints.DEFAULT.withWidth(SizeConstraint.fixed(16)));
        close.onClick(() -> Minecraft.getInstance().setScreen(null));

        return header.add(title).add(Spacer.grow("sp")).add(countLabel).add(close);
    }

    // ─── Body = sidebar + grid ────────────────────────────────────────────────

    private Widget buildBody() {
        FlexContainer body = FlexContainer.row("body").gap(0)
                .alignItems(FlexLayout.AlignItems.STRETCH);
        body.setFlex(body.getFlexConstraints().withFlexGrow(1f));

        body.add(buildSidebar());
        body.add(Separator.vertical("vsep").setColor(0xFF2D2D50));
        body.add(buildGrid());

        return body;
    }

    // ─── Sidebar ──────────────────────────────────────────────────────────────

    private Widget buildSidebar() {
        FlexContainer sidebar = FlexContainer.column("sidebar")
                .gap(4)
                .alignItems(FlexLayout.AlignItems.STRETCH)
                .justifyContent(FlexLayout.JustifyContent.FLEX_START);
        sidebar.setBackground(Background.color(0xFF111827));
        sidebar.setPadding(EdgeInsets.all(6));
        sidebar.setConstraints(LayoutConstraints.DEFAULT
                .withWidth(SizeConstraint.fixed(64)));

        for (String category : CATEGORIES) {
            Button btn = new Button("cat_" + category, Component.literal(category))
                    .setColors(0xFF1E293B, 0xFF334155)
                    .useVanillaStyle(false);
            btn.setConstraints(LayoutConstraints.DEFAULT
                    .withWidth(SizeConstraint.fillParent()));
            btn.onClick(() -> {
                selectedCategory = category;
                applyFilter(category);
            });
            sidebar.add(btn);
        }

        return sidebar;
    }

    // ─── Virtual grid ─────────────────────────────────────────────────────────

    private Widget buildGrid() {
        grid = new VirtualScrollGrid<>(
                "cosmetic_grid",
                4,
                () -> {
                    Button btn = new Button("_cell", Component.literal(""))
                            .useVanillaStyle(false);
                    btn.setConstraints(LayoutConstraints.DEFAULT.withAspectRatio(1f));
                    return btn;
                },
                (btn, item, index) -> {
                    btn.clearClickHandlers();
                    btn.setLabel(Component.literal("§f" + item.name()));
                    btn.setColors(item.color(), brighten(item.color()));
                    btn.onClick(() -> onCosmeticClicked(item));
                }
        );
        grid.gap(6)
                .cellAspectRatio(1f)
                .bufferRows(2)
                .setPadding(EdgeInsets.all(8));
        grid.setFlex(grid.getFlexConstraints().withFlexGrow(1f));

        return grid;
    }

    // ─── Filter ───────────────────────────────────────────────────────────────

    private void applyFilter(String category) {
        List<CosmeticItem> filtered = "All".equals(category)
                ? allItems
                : allItems.stream()
                .filter(i -> i.category().equals(category))
                .toList();

        grid.setItems(filtered);
        countLabel.setText(Component.literal("§7" + filtered.size() + " items"));
    }

    private void onCosmeticClicked(CosmeticItem item) {
        System.out.println("Selected: " + item.name() + " [" + item.category() + "]");
    }

    // ─── Dummy data ───────────────────────────────────────────────────────────

    private static List<CosmeticItem> generateDummyItems(int count) {
        String[] cats = Arrays.copyOfRange(CATEGORIES, 1, CATEGORIES.length);
        List<CosmeticItem> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String cat   = cats[i % cats.length];
            int    color = PALETTE[i % PALETTE.length];
            list.add(new CosmeticItem("item_" + i, cat + " #" + (i + 1), cat, color));
        }
        return list;
    }

    private static int brighten(int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = Math.min(255, ((argb >> 16) & 0xFF) + 35);
        int g = Math.min(255, ((argb >>  8) & 0xFF) + 35);
        int b = Math.min(255, ( argb        & 0xFF) + 35);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}