package net.bati.guilib.navigation;

import lombok.Getter;
import net.bati.guilib.widget.Widget;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages navigation between registered {@link Page}s.
 */
public final class PageRouter {

    public interface NavigationListener {
        void onNavigate(String pageId, Widget pageWidget);
    }

    private final Map<String, Page> pages         = new LinkedHashMap<>();
    private final Deque<String>     history        = new ArrayDeque<>();
    private @Nullable String        currentPageId  = null;
    private @Nullable NavigationListener listener  = null;

    private final PageContext context = new PageContext(this);

    // ─── Registration ─────────────────────────────────────────────────────────

    public PageRouter register(Page page) {
        pages.put(page.getId(), page);
        return this;
    }

    public PageRouter setListener(NavigationListener l) {
        this.listener = l;
        return this;
    }

    // ─── Navigation ───────────────────────────────────────────────────────────

    public void navigateTo(String pageId) {
        Page page = pages.get(pageId);
        if (page == null) throw new IllegalArgumentException("Unknown page: " + pageId);

        // Hide current
        if (currentPageId != null) {
            Page current = pages.get(currentPageId);
            if (current != null) current.onHide();
            history.push(currentPageId);
        }

        currentPageId = pageId;
        Widget widget = page.build(context);
        page.onShow();

        if (listener != null) listener.onNavigate(pageId, widget);
    }

    public void goBack() {
        if (history.isEmpty()) return;
        String prev = history.pop();
        // Don't push to history again
        Page current = currentPageId != null ? pages.get(currentPageId) : null;
        if (current != null) current.onHide();

        currentPageId = prev;
        Page page = pages.get(prev);
        if (page != null) {
            Widget widget = page.build(context);
            page.onShow();
            if (listener != null) listener.onNavigate(prev, widget);
        }
    }

    public boolean canGoBack()                         { return !history.isEmpty(); }
    public @Nullable String getCurrentPageId()         { return currentPageId; }
    public @Nullable Page   getPage(String id)         { return pages.get(id); }
    public Collection<Page> getPages()                 { return Collections.unmodifiableCollection(pages.values()); }
}


