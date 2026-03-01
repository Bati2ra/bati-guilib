package net.bati.miniui.navigation;

/**
 * Context provided to a {@link Page} when it builds its widget tree.
 */
public final class PageContext {

    private final PageRouter router;

    PageContext(PageRouter router) {
        this.router = router;
    }

    public PageRouter getRouter() { return router; }

    /** Navigate to a different page by ID. */
    public void navigateTo(String pageId) {
        router.navigateTo(pageId);
    }

    /** Navigate back to the previous page. */
    public void goBack() {
        router.goBack();
    }

    public boolean canGoBack() {
        return router.canGoBack();
    }
}


