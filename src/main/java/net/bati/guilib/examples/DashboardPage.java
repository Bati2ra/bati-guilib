package net.bati.guilib.examples;

import net.bati.guilib.navigation.Page;
import net.bati.guilib.navigation.PageContext;
import net.bati.guilib.widget.Button;
import net.bati.guilib.widget.FlexContainer;
import net.bati.guilib.widget.Label;
import net.bati.guilib.widget.Widget;
import net.minecraft.network.chat.Component;

public class DashboardPage implements Page {

    @Override
    public String getId() { return "dashboard"; }

    @Override
    public Component getTitle() { return Component.literal("Dashboard"); }

    @Override
    public Widget build(PageContext context) {
        FlexContainer page = FlexContainer.column("dashboard-content");
        page.gap(12);

        Label title = new Label("title", Component.literal("Dashboard"));
        title.withColor(0xFFFFFFFF);
        page.addChild(title);

        Label stats = new Label("stats", Component.literal("Stats: 42 items"));
        stats.withColor(0xFFFFFFFF);
        page.addChild(stats);

        Button action = new Button("action", Component.literal("View Details"));
        action.getEventHandlers().onClick(w -> context.navigateTo("stats"));
        page.addChild(action);

        return page;
    }
}