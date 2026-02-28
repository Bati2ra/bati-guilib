package net.bati.guilib.widget;

import net.bati.guilib.layout.Alignment;
import net.bati.guilib.layout.LayoutConstraints;
import net.bati.guilib.rendering.Background;

/**
 * Full-screen dimmed overlay that centers a modal dialog widget.
 * Add to a Panel or root widget to show a modal.
 */
public class ModalLayer extends Panel {

    private static int modalCounter = 0;

    public ModalLayer(Widget content) {
        super("modal_layer_" + (modalCounter++));

        // Semi-transparent dark overlay that fills the parent
        setConstraints(LayoutConstraints.fillParent());
        setBackground(Background.color(0x80000000));
        setConstraints(getConstraints().withZIndex(1000));

        // Center the content
        content.setConstraints(content.getConstraints()
                .withAlignment(Alignment.MIDDLE_CENTER));

        addChild(content);
    }

    /** Factory – also adds the modal to a parent widget immediately. */
    public static ModalLayer show(Widget parent, Widget content) {
        ModalLayer modal = new ModalLayer(content);
        parent.addChild(modal);
        parent.layoutNewChild(modal);
        return modal;
    }

    public void dismiss() {
        if (getParent() != null) getParent().removeChild(this);
    }

    /** Close modal when clicking the overlay (outside the dialog). */
    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (computedLayout == null) return false;

        // Check if click is inside content
        for (Widget child : getChildren()) {
            if (child.getComputedLayout() != null &&
                    child.getComputedLayout().containsPoint((float)mx, (float)my)) {
                return child.mouseClicked(mx, my, button);
            }
        }

        // Click was on the overlay itself → dismiss
        dismiss();
        return true;
    }
}