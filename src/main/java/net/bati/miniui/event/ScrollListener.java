package net.bati.miniui.event;

/**
 * Implemented by widgets that need to react to scroll offset changes
 * without a full layout pass. ScrollContainer calls this on its direct
 * child whenever the offset changes.
 */
public interface ScrollListener {
    /**
     * @param scrollOffset  new scroll offset in logical pixels
     * @param viewportH     visible height of the scroll viewport
     */
    void onScrollChanged(float scrollOffset, float viewportH);
}


