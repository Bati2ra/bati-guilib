package net.bati.guilib.navigation;

import lombok.Getter;
import net.bati.guilib.widget.Widget;

import java.util.*;

/**
 * Manages page navigation and history.
 * Similar to React Router or Android Navigation Component.
 */
public class PageRouter {

    private final Map<String, Page> pages = new LinkedHashMap<>();
    private final Deque<String> history = new ArrayDeque<>();
    private final PageContext context;

    @Getter
    private String currentPageId;

    @Getter
    private Widget currentPageWidget;

    private NavigationListener listener;

    public PageRouter() {
        this.context = new PageContext(this);
    }

    /** Register a page */
    public void registerPage(Page page) {
        pages.put(page.getId(), page);
    }

    /** Set initial page */
    public void setInitialPage(String pageId) {
        if (currentPageId == null) {
            navigateTo(pageId, false);
        }
    }

    /** Navigate to a page */
    public void navigateTo(String pageId) {
        navigateTo(pageId, true);
    }

    private void navigateTo(String pageId, boolean addToHistory) {
        Page page = pages.get(pageId);
        if (page == null) {
            throw new IllegalArgumentException("Page not found: " + pageId);
        }

        // Cleanup current page
        if (currentPageId != null) {
            Page currentPage = pages.get(currentPageId);
            if (currentPage != null) currentPage.onHide();
            if (addToHistory) history.push(currentPageId);
        }

        // Build and show new page
        currentPageId = pageId;
        currentPageWidget = page.build(context);
        page.onShow();

        // Notify listener
        if (listener != null) {
            listener.onNavigate(pageId, currentPageWidget);
        }
    }

    /** Go back to previous page */
    public void goBack() {
        if (!history.isEmpty()) {
            String previousPageId = history.pop();
            navigateTo(previousPageId, false);
        }
    }

    /** Check if can go back */
    public boolean canGoBack() {
        return !history.isEmpty();
    }

    /** Set navigation listener (typically the container widget) */
    public void setNavigationListener(NavigationListener listener) {
        this.listener = listener;
    }

    /** Get all registered pages (for building nav menus) */
    public Collection<Page> getAllPages() {
        return pages.values();
    }

    @FunctionalInterface
    public interface NavigationListener {
        void onNavigate(String pageId, Widget pageWidget);
    }
}