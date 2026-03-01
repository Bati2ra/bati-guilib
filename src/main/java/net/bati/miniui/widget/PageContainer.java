package net.bati.miniui.widget;

import net.bati.miniui.layout.MeasureResult;
import net.bati.miniui.navigation.PageRouter;
import org.jetbrains.annotations.Nullable;

/**
 * A widget that hosts the active page's widget tree.
 * Registers itself as the router's navigation listener.
 */
public class PageContainer extends Panel
        implements PageRouter.NavigationListener {

    private final PageRouter router;
    private @Nullable Widget currentPage;

    public PageContainer(String id, PageRouter router) {
        super(id);
        this.router = router;
        router.setListener(this);
    }

    @Override
    public void onNavigate(String pageId, Widget pageWidget) {
        // Remove old page
        if (currentPage != null) removeChild(currentPage);

        currentPage = pageWidget;
        addChild(currentPage);

        // If already laid out, lay out the new child immediately
        layoutNewChild(currentPage);
    }

    @Override
    protected MeasureResult measureContent(float aw, float ah) {
        if (currentPage == null) return new MeasureResult(aw, ah);
        return super.measureContent(aw, ah);
    }
}