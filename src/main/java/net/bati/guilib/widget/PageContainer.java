package net.bati.guilib.widget;

import lombok.Getter;
import net.bati.guilib.layout.LayoutConstraints;
import net.bati.guilib.navigation.PageRouter;

/**
 * Container widget that displays the current page from a router.
 */
@Getter
public class PageContainer extends Widget implements PageRouter.NavigationListener {

    private final PageRouter router;
    private Widget currentPage;

    public PageContainer(String id, PageRouter router) {
        super(id);
        this.router = router;
        this.router.setNavigationListener(this);
    }

    @Override
    public void onNavigate(String pageId, Widget pageWidget) {
        // Remove old page
        if (currentPage != null) {
            removeChild(currentPage);
        }

        // Add new page
        currentPage = pageWidget;

        // Make page fill the container
        currentPage.setConstraints(
                LayoutConstraints.builder()
                        .width(LayoutConstraints.SizeConstraint.fillParent())
                        .height(LayoutConstraints.SizeConstraint.fillParent())
                        .build()
        );

        addChild(currentPage);
    }

    @Override
    protected Size measureContent(float availableWidth, float availableHeight) {
        // Container sizes to its constraints, not content
        //return super.measureContent(availableWidth, availableHeight);
        return new Size(0, 0);
    }
}
