package net.bati.guilib.examples;

import net.bati.guilib.layout.BoxModel;
import net.bati.guilib.layout.FlexConstraints;
import net.bati.guilib.layout.flex.FlexLayout;
import net.bati.guilib.layout.LayoutConstraints;
import net.bati.guilib.navigation.PageRouter;
import net.bati.guilib.rendering.NineSlice;
import net.bati.guilib.screen.ModernScreen;
import net.bati.guilib.widget.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ExampleScreen extends ModernScreen {

    public ExampleScreen() {
        super(Component.literal("Modern UI Example"));
    }

    @Override
    protected void buildUI() {
        // Example 1: Simple button with padding
        Button simpleButton = new Button("simple-button", Component.literal("DAFUCK YA SAYIN"));
        simpleButton.setConstraints(
                LayoutConstraints.builder()
                        .alignment(LayoutConstraints.Alignment.TOP_CENTER)
                        .offsetY(20)
                        .width(LayoutConstraints.SizeConstraint.fillParent())
                        .build()
        );
        simpleButton.getEventHandlers().onClick(widget -> {
            System.out.println("Button clicked!");
        });
      //  addWidget(simpleButton);

        // Example 2: Flex container with multiple buttons
        FlexContainer buttonRow = FlexContainer.row("button-row");
        buttonRow.setBackgroundColor(-1);
        buttonRow.setPadding(BoxModel.Insets.all(6));
        buttonRow.gap(10)
                .justifyContent(FlexLayout.JustifyContent.SPACE_BETWEEN)
                .alignItems(FlexLayout.AlignItems.CENTER).direction(FlexLayout.FlexDirection.COLUMN);


        buttonRow.setConstraints(
                LayoutConstraints.builder()
                        .alignment(LayoutConstraints.Alignment.TOP_CENTER)
                        .offsetY(60)
                        .width(LayoutConstraints.SizeConstraint.percentage(0.8f))
                        .height(LayoutConstraints.SizeConstraint.auto())
                        .build()
        );

        // Add buttons to the row
        for (int i = 1; i <= 3; i++) {
            Button btn = new Button("btn-" + i, Component.literal("Button " + i));
            btn.setPadding(BoxModel.Insets.symmetric(6, 16));
            btn.getEventHandlers().onClick(widget -> {
                System.out.println("Button clicked! " + btn.getId());
            });
            buttonRow.addChild(btn);

        }
        FlexContainer testRow = FlexContainer.row("button-row");
        testRow.setBackgroundColor(-5000);
        testRow.gap(10)
                .justifyContent(FlexLayout.JustifyContent.CENTER)
                .alignItems(FlexLayout.AlignItems.CENTER);
        testRow.setConstraints(LayoutConstraints.builder().width(LayoutConstraints.SizeConstraint.fillParent()).build());

        for (int i = 1; i <= 2; i++) {
            Button btn = new Button("btn-b" + i, Component.literal("Buttonb " + i));
            btn.setPadding(BoxModel.Insets.symmetric(6, 16));

            testRow.addChild(btn);

        }
        buttonRow.addChild(testRow);

     //   addWidget(buttonRow);


/*
        // Example 3: Panel with padding and children
        Panel infoPanel = new Panel("info-panel");
        infoPanel.setConstraints(
                LayoutConstraints.builder()
                        .alignment(LayoutConstraints.Alignment.MIDDLE_CENTER)
                        .width(LayoutConstraints.SizeConstraint.fixed(300))
                        .height(LayoutConstraints.SizeConstraint.fixed(200))
                        .build()
        );
        infoPanel.setPadding(BoxModel.Insets.all(16));

        // Add content to panel using flex layout
        FlexContainer panelContent = FlexContainer.column("panel-content");
        panelContent.gap(8)
                .alignItems(FlexLayout.AlignItems.STRETCH);

        panelContent.setConstraints(
                LayoutConstraints.builder()
                        .width(LayoutConstraints.SizeConstraint.fillParent())
                        .height(LayoutConstraints.SizeConstraint.fillParent())
                        .build()
        );

        // Add labels to panel
        Label title = new Label("title", Component.literal("Information Panel"));
        title.withColor(0xFFFF00).withAlignment(Label.TextAlignment.CENTER);
        panelContent.addChild(title);

        Label description = new Label("desc", Component.literal("This demonstrates the new layout system"));
        panelContent.addChild(description);

        infoPanel.addChild(panelContent);
*/      //  addWidget(infoPanel);

        // Example 4: Complex layout with flexbox
        createComplexLayout();
    }

    private void createComplexLayout() {
        // Create a sidebar-content layout
        FlexContainer mainLayout = FlexContainer.row("main-layout");
        mainLayout.gap(0)
                .justifyContent(FlexLayout.JustifyContent.SPACE_BETWEEN)
                .alignItems(FlexLayout.AlignItems.STRETCH);

        mainLayout.setConstraints(
                LayoutConstraints.builder()
                        .alignment(LayoutConstraints.Alignment.BOTTOM_LEFT)
                        .width(LayoutConstraints.SizeConstraint.fillParent())
                        .height(LayoutConstraints.SizeConstraint.fillParent())
                        .build()
        );

        PageRouter router = new PageRouter();
        router.registerPage(new DashboardPage());
        router.registerPage(new SettingsPage());

        // Sidebar (25% width)
        Panel sidebar = new Panel("sidebar");
        sidebar.setConstraints(
                LayoutConstraints.builder()
                        .width(LayoutConstraints.SizeConstraint.percentage(0.15f))
                        .height(LayoutConstraints.SizeConstraint.fillParent())
                        .build()
        );
        Identifier GUI_TEXTURE = Identifier.withDefaultNamespace("textures/gui/sprites/container/inventory/effect_background.png");

        NineSlice inventoryBackground = NineSlice.uniform(
                GUI_TEXTURE,
                0,      // u - posición X en la textura
                0,      // v - posición Y en la textura
                32,    // width - ancho de la región en la textura
                32,    // height - alto de la región en la textura
                7,       // slice - tamaño de las esquinas (7px para GUI vanilla)
                32,
                32
        );
        sidebar.setBackground(inventoryBackground);

        // Sidebar content
        FlexContainer sidebarContent = FlexContainer.column("sidebar-content");
        sidebarContent.gap(4)
                .setConstraints(
                        LayoutConstraints.builder()
                                .width(LayoutConstraints.SizeConstraint.fillParent())
                                .height(LayoutConstraints.SizeConstraint.fillParent())
                                .build()
                );

        for (int i = 1; i <= 5; i++) {
            Button sidebarBtn = new Button("sidebar-btn-" + i, Component.literal("Option " + i));
            sidebarBtn.setPadding(BoxModel.Insets.symmetric(4, 8));
            sidebarBtn.setConstraints(
                    LayoutConstraints.builder()
                            .width(LayoutConstraints.SizeConstraint.fillParent())
                            .build()
            );
            if(i == 1) {
                sidebarBtn.getEventHandlers().onClick(widget -> {
                    router.navigateTo("dashboard");
                });
            } else if(i == 2) {
                sidebarBtn.getEventHandlers().onClick(widget -> {
                    router.navigateTo("settings");
                });
            }
            sidebarContent.addChild(sidebarBtn);
        }

        sidebar.addChild(sidebarContent);
        mainLayout.addChild(sidebar);

        // Content area (75% width)
        Panel content = new Panel ("content");
        content.setConstraints(
                LayoutConstraints.builder()
                        .width(LayoutConstraints.SizeConstraint.percentage(0.85f))
                        .height(LayoutConstraints.SizeConstraint.fillParent())
                        .build()
        );
        router.setInitialPage("dashboard");
        content.setPadding(BoxModel.Insets.all(16));

        // Content layout
        FlexContainer contentLayout = FlexContainer.column("content-layout");
        contentLayout.gap(12)
                .setConstraints(
                        LayoutConstraints.builder()
                                .width(LayoutConstraints.SizeConstraint.fillParent())
                                .height(LayoutConstraints.SizeConstraint.fillParent())
                                .build()
                );
        contentLayout.justifyContent(FlexLayout.JustifyContent.SPACE_BETWEEN);

        // Header row with title and button
        FlexContainer header = FlexContainer.row("header");
        header.justifyContent(FlexLayout.JustifyContent.SPACE_BETWEEN)
                .alignItems(FlexLayout.AlignItems.CENTER)
                .setConstraints(
                        LayoutConstraints.builder()
                                .width(LayoutConstraints.SizeConstraint.fillParent())
                                .build()
                );

        Label contentTitle = new Label("content-title", Component.literal("Main Content"));
        contentTitle.withColor(0xFFFFFFFF);
        header.addChild(contentTitle);

        Button actionBtn = new Button("action-btn", Component.literal("Action"));
        actionBtn.setPadding(BoxModel.Insets.symmetric(4, 12));
        header.addChild(actionBtn);

        contentLayout.addChild(header);

        // Add spacer to push footer to bottom
       // Spacer spacer = Spacer.flexible("spacer");
        PageContainer pageContainer = new PageContainer("page-content", router);
        pageContainer.setConstraints(
                LayoutConstraints.builder()
                        .width(LayoutConstraints.SizeConstraint.fillParent())
                        .height(LayoutConstraints.SizeConstraint.percentage(0.7f))
                        .build()
        );
        pageContainer.setBackgroundColor(-5000);
        contentLayout.addChild(pageContainer);

        // Footer
        Label footer = new Label("footer", Component.literal("Footer Text"));
        footer.withColor(0xFF808080).withAlignment(Label.TextAlignment.CENTER);
        footer.setConstraints(
                LayoutConstraints.builder()
                        .width(LayoutConstraints.SizeConstraint.fillParent())
                        .build()
        );
        contentLayout.addChild(footer);

        content.addChild(contentLayout);
        mainLayout.addChild(content);

        addWidget(mainLayout);
    }
    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);


    }
    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        // Custom gradient background
        //context.fillGradient(0, 0, width, height, 0xFF1a1a2e, 0xFF16213e);
    }
}