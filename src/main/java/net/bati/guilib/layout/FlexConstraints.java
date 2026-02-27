package net.bati.guilib.layout;

import lombok.Builder;
import lombok.Getter;
import net.bati.guilib.layout.flex.FlexLayout;

/**
 * Flex-specific properties for children of a FlexContainer.
 * Kept separate from LayoutConstraints to avoid mixing static-layout
 * and flex-layout concerns.
 */
@Getter
@Builder(toBuilder = true)
public class FlexConstraints {

    @Builder.Default private final int   flexGrow   = 0;
    @Builder.Default private final int   flexShrink = 1;
    private final Float                  flexBasis;  // null = auto

    /** Override parent's alignItems for this child only. */
    private final FlexLayout.AlignItems alignSelf;  // null = use parent default
}
