package net.bati.guilib.examples;

import net.bati.guilib.layout.Alignment;
import net.bati.guilib.layout.EdgeInsets;
import net.bati.guilib.layout.SizeConstraint;
import net.bati.guilib.layout.flex.FlexLayout;
import net.bati.guilib.layout.LayoutConstraints;
import net.bati.guilib.rendering.Background;
import net.bati.guilib.screen.ModernScreen;
import net.bati.guilib.widget.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * Demonstrates the UI system with a complex nested layout:
 *
 *  ┌─────────────────────────────────────────────┐
 *  │  HEADER ROW: [Title]   [spacer]   [Close]  │
 *  ├─────────────────────────────────────────────┤
 *  │  CONTENT ROW                                │
 *  │  ┌──────────────┐  ┌───────────────────┐   │
 *  │  │  SIDEBAR     │  │  MAIN PANEL       │   │
 *  │  │  (column)    │  │  (scroll + flex)  │   │
 *  │  │  [Nav btn 1] │  │  [Card 1]         │   │
 *  │  │  [Nav btn 2] │  │  [Card 2]         │   │
 *  │  │  [Nav btn 3] │  │  ...              │   │
 *  │  └──────────────┘  └───────────────────┘   │
 *  ├─────────────────────────────────────────────┤
 *  │  FOOTER ROW: [Status label]  [Action btn]  │
 *  └─────────────────────────────────────────────┘
 */
public class ExampleScreen extends ModernScreen {
    public ExampleScreen() {
        super(Component.literal("ModernUI Example"));
    }

    @Override
    protected void init() {
        super.init();
        setRoot(buildRoot());
    }

    private Widget buildRoot() {
        // Root: full-screen dark panel
        Panel root = new Panel("root");
        root.setConstraints(LayoutConstraints.fillParent());
        root.setBackground(Background.color(0xFF1A1A2E));

        // Main layout: vertical flex column — STRETCH so all children fill width
        FlexContainer mainCol = FlexContainer.column("main_col")
                .gap(0)
                .alignItems(FlexLayout.AlignItems.STRETCH);
        mainCol.setConstraints(LayoutConstraints.fillParent());

        // ── HEADER ─────────────────────────────────────────────────────────────
        FlexContainer header = buildHeader();
        header.setConstraints(LayoutConstraints.DEFAULT
                .withWidth(SizeConstraint.fillParent())
                .withHeight(SizeConstraint.fixed(28)));

        // ── CONTENT AREA ───────────────────────────────────────────────────────
        FlexContainer content = buildContent();
        content.setFlex(content.getFlexConstraints().withFlexGrow(1f));
        content.setConstraints(LayoutConstraints.DEFAULT
                .withWidth(SizeConstraint.fillParent()));

        // ── FOOTER ─────────────────────────────────────────────────────────────
        FlexContainer footer = buildFooter();
        footer.setConstraints(LayoutConstraints.DEFAULT
                .withWidth(SizeConstraint.fillParent())
                .withHeight(SizeConstraint.fixed(24)));

        mainCol.add(header).add(Separator.horizontal("sep1").setColor(0xFF3A3A5C))
                .add(content)
                .add(Separator.horizontal("sep2").setColor(0xFF3A3A5C))
                .add(footer);

        root.addChild(mainCol);
        return root;
    }

    // ── Header ──────────────────────────────────────────────────────────────

    private FlexContainer buildHeader() {
        FlexContainer header = FlexContainer.row("header")
                .justifyContent(FlexLayout.JustifyContent.SPACE_BETWEEN)
                .alignItems(FlexLayout.AlignItems.CENTER)
                .gap(8);
        header.setBackground(Background.color(0xFF16213E));
        header.setPadding(EdgeInsets.symmetric(0, 10));

        Label title = new Label("title", Component.literal("§b§lModernUI §r§7Demo"))
                .setColor(0xFFCCCCCC);
        title.setMargin(EdgeInsets.only(0, 0, 0, 4));

        Spacer spacer = Spacer.grow("header_spacer");

        Button closeBtn = new Button("close", Component.literal("§c✕"))
                .setColors(0xFF8B0000, 0xFFCC0000)
                .useVanillaStyle(false);
        closeBtn.setConstraints(LayoutConstraints.DEFAULT.withWidth(SizeConstraint.fixed(16)));
        closeBtn.onClick(() -> Minecraft.getInstance().setScreen(null));

        return header.add(title).add(spacer).add(closeBtn);
    }

    // ── Content (sidebar + main) ─────────────────────────────────────────────

    private FlexContainer buildContent() {
        FlexContainer row = FlexContainer.row("content")
                .alignItems(FlexLayout.AlignItems.STRETCH)
                .gap(0);

        // Sidebar
        FlexContainer sidebar = buildSidebar();
        sidebar.setConstraints(LayoutConstraints.DEFAULT
                .withWidth(SizeConstraint.fixed(80)));

        // Main area
        Widget main = buildMain();
        main.setFlex(main.getFlexConstraints().withFlexGrow(1f));

        return row.add(sidebar).add(Separator.vertical("vsep").setColor(0xFF3A3A5C)).add(main);
    }

    private FlexContainer buildSidebar() {
        FlexContainer col = FlexContainer.column("sidebar")
                .justifyContent(FlexLayout.JustifyContent.FLEX_START)
                .gap(4);
        col.setBackground(Background.color(0xFF162032));
        col.setPadding(EdgeInsets.all(8));

        String[] sections = { "§eGeneral", "§aItems", "§bStats", "§6Settings" };
        for (int i = 0; i < sections.length; i++) {
            final int idx = i;
            Button btn = new Button("nav_" + i, Component.literal(sections[i]))
                    .setColors(0xFF2A2A4A, 0xFF3A3A6A)
                    .useVanillaStyle(false);
            btn.setConstraints(LayoutConstraints.DEFAULT
                    .withWidth(SizeConstraint.fillParent())
                    .withHeight(SizeConstraint.fixed(18)));
            btn.onClick(() -> System.out.println("Navigate to section " + idx));
            col.add(btn);
        }

        return col;
    }

    private Widget buildMain() {
        // Scrollable list of cards
        FlexContainer cardList = FlexContainer.column("card_list")
                .gap(8)
                .alignItems(FlexLayout.AlignItems.STRETCH)
                .justifyContent(FlexLayout.JustifyContent.FLEX_START);
        cardList.setPadding(EdgeInsets.all(10));

        for (int i = 1; i <= 8; i++) {
            cardList.add(buildCard("Card " + i, "Description for card number " + i));
        }

        ScrollContainer scroll = new ScrollContainer("main_scroll");
        scroll.add(cardList);
        return scroll;
    }

    /** A card: row with icon area + info column + action button. */
    private FlexContainer buildCard(String title, String desc) {
        FlexContainer card = FlexContainer.row("card_" + title.replace(" ", "_"))
                .alignItems(FlexLayout.AlignItems.CENTER)
                .gap(8);
        card.setBackground(Background.color(0xFF1E2D50));
        card.setPadding(EdgeInsets.symmetric(6, 8));
        card.setConstraints(LayoutConstraints.DEFAULT
                .withHeight(SizeConstraint.fixed(40)));
        card.setMargin(EdgeInsets.vertical(0));

        // Icon placeholder
        Panel icon = new Panel("icon_" + title);
        icon.setBackground(Background.color(0xFF2563EB));
        icon.setConstraints(LayoutConstraints.DEFAULT
                .withWidth(SizeConstraint.fixed(28))
                .withHeight(SizeConstraint.fixed(28)));

        // Info column (title + description)
        FlexContainer info = FlexContainer.column("info_" + title)
                .justifyContent(FlexLayout.JustifyContent.CENTER)
                .gap(2);
        info.setFlex(info.getFlexConstraints().withFlexGrow(1f));

        Label titleLabel = new Label("title_" + title, Component.literal("§b" + title))
                .setColor(0xFFFFFFFF)
                .setShadow(true);
        Label descLabel = new Label("desc_" + title, Component.literal("§7" + desc))
                .setColor(0xFFAAAAAA)
                .setShadow(false);

        info.add(titleLabel).add(descLabel);

        // Action button
        Button action = new Button("act_" + title, Component.literal("§aOpen"))
                .setColors(0xFF166534, 0xFF15803D)
                .useVanillaStyle(false);
        action.setConstraints(LayoutConstraints.DEFAULT
                .withWidth(SizeConstraint.fixed(40))
                .withHeight(SizeConstraint.fixed(16)));
        action.onClick(() -> System.out.println("Action: " + title));

        return card.add(icon).add(info).add(action);
    }

    // ── Footer ──────────────────────────────────────────────────────────────

    private FlexContainer buildFooter() {
        FlexContainer footer = FlexContainer.row("footer")
                .justifyContent(FlexLayout.JustifyContent.SPACE_BETWEEN)
                .alignItems(FlexLayout.AlignItems.CENTER);
        footer.setBackground(Background.color(0xFF16213E));
        footer.setPadding(EdgeInsets.symmetric(0, 10));

        Label status = new Label("status", Component.literal("§7ModernUI v1.0 | Minecraft 1.21.11 Fabric"))
                .setColor(0xFF888888);

        Button openModal = new Button("modal_btn", Component.literal("Show Modal"))
                .setColors(0xFF7C3AED, 0xFF9F67FF)
                .useVanillaStyle(false);
        openModal.setConstraints(LayoutConstraints.DEFAULT.withWidth(SizeConstraint.fixed(80)));
        openModal.onClick(() -> {
            Widget dialog = buildModalDialog();
            // Find root to attach modal
            Widget root = this.getRoot();
            if (root != null) ModalLayer.show(root, dialog);
        });

        return footer.add(status).add(openModal);
    }

    private Widget buildModalDialog() {
        FlexContainer dialog = FlexContainer.column("dialog")
                .gap(10)
                .justifyContent(FlexLayout.JustifyContent.FLEX_START);
        dialog.setBackground(Background.color(0xFF1A1A2E));
        dialog.setPadding(EdgeInsets.all(16));
        dialog.setConstraints(LayoutConstraints.DEFAULT
                .withWidth(SizeConstraint.fixed(200))
                .withHeight(SizeConstraint.fixed(120)));

        Label title = new Label("dialog_title", Component.literal("§b§lDialog"))
                .setColor(0xFFFFFFFF);

        Label body = new Label("dialog_body", Component.literal("§7This is a modal dialog.\nClick outside to dismiss."))
                .setColor(0xFFCCCCCC);

        Button ok = new Button("dialog_ok", Component.literal("OK"))
                .setColors(0xFF2563EB, 0xFF3B82F6)
                .useVanillaStyle(false);
        ok.setConstraints(LayoutConstraints.DEFAULT
                .withWidth(SizeConstraint.fixed(60))
                .withAlignment(Alignment.TOP_CENTER));

        dialog.add(title).add(Separator.horizontal("dsep").setColor(0xFF3A3A5C)).add(body).add(ok);
        return dialog;
    }
}