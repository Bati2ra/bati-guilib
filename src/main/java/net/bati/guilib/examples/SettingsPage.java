package net.bati.guilib.examples;

import net.bati.guilib.navigation.Page;
import net.bati.guilib.navigation.PageContext;
import net.bati.guilib.widget.FlexContainer;
import net.bati.guilib.widget.Label;
import net.bati.guilib.widget.Widget;
import net.minecraft.network.chat.Component;

public class SettingsPage implements Page {

    @Override
    public String getId() { return "settings"; }

    @Override
    public Component getTitle() { return Component.literal("Settings"); }

    @Override
    public Widget build(PageContext context) {
        FlexContainer page = FlexContainer.column("settings-content");
        page.gap(8);

        Label title = new Label("title", Component.literal("Settings"));
        title.withColor(-1);
        page.addChild(title);

        // Opciones de settings...

        return page;
    }

    @Override
    public void onShow() {
        System.out.println("Settings page shown - load preferences");
    }

    @Override
    public void onHide() {
        System.out.println("Settings page hidden - save changes");
    }
}