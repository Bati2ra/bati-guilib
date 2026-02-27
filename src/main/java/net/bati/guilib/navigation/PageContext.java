package net.bati.guilib.navigation;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Context passed to pages, allowing them to:
 * - Navigate to other pages
 * - Share data between pages
 * - Access screen-level services
 */
@Getter
public class PageContext {
    private final PageRouter router;
    private final Map<String, Object> sharedData = new HashMap<>();

    public PageContext(PageRouter router) {
        this.router = router;
    }

    /** Navigate to another page */
    public void navigateTo(String pageId) {
        router.navigateTo(pageId);
    }

    /** Navigate back */
    public void goBack() {
        router.goBack();
    }

    /** Share data between pages */
    public void putData(String key, Object value) {
        sharedData.put(key, value);
    }

    public <T> T getData(String key, Class<T> type) {
        return type.cast(sharedData.get(key));
    }
}
