package net.bati.miniui.navigation;

import net.bati.miniui.widget.Widget;
import net.minecraft.network.chat.Component;

/**
 * A self-contained "screen page" – similar to a React component.
 * Pages are built lazily and rebuilt on each navigation (no stale state).
 */
public interface Page {

    /** Unique identifier for routing. */
    String getId();

    /** Title displayed in the screen header. */
    Component getTitle();

    /**
     * Build the widget tree for this page.
     * Called each time this page becomes visible.
     *
     * @param context provides routing utilities and shared state
     */
    Widget build(PageContext context);

    /** Called just before the page's widget is shown. */
    default void onShow() {}

    /** Called just before the page is replaced. */
    default void onHide() {}
}


