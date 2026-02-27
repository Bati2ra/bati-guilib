package net.bati.guilib.navigation;

import net.bati.guilib.widget.Widget;
import net.minecraft.network.chat.Component;

/**
 * Represents a page/view that can be displayed in a container.
 * Similar to React components or Android Fragments.
 */
public interface Page {

    /**
     * Unique identifier for this page
     */
    String getId();

    /**
     * Display name (for navigation buttons, breadcrumbs, etc.)
     */
    Component getTitle();

    /**
     * Build the widget tree for this page.
     * Called when the page is first shown.
     */
    Widget build(PageContext context);

    /**
     * Called when navigating away from this page.
     * Use for cleanup, saving state, etc.
     */
    default void onHide() {}

    /**
     * Called when navigating to this page.
     * Use for loading data, animations, etc.
     */
    default void onShow() {}
}