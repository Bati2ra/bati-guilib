package net.bati.guilib.gui.screen;

import net.bati.guilib.gui.components.AlignedContainer;
import net.bati.guilib.gui.components.Button;
import net.bati.guilib.utils.Orientation;
import net.bati.guilib.utils.Pivot;
import net.bati.guilib.utils.Vec2;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;


public class TestScreen extends AdvancedScreen{


    public TestScreen(@Nullable Text title) {
        super(title);
    }

    @Override
    public void build() {
        AlignedContainer alignedContainer = new AlignedContainer("buttons");
        alignedContainer.setAlign(Orientation.VERTICAL);
        alignedContainer.setPivot(Pivot.MIDDLE_TOP);
        alignedContainer.setSpacing(10);
        alignedContainer.setPositionListener((window) -> new Vec2(window.getScaledWidth()/2, 10));
        alignedContainer.setSize(0.7f);

        Button button;
        for(int i=0; i<5; i++) {
            button = Button.builder("test"+i)
                    .boxWidth(25)
                    .boxHeight(25)
                    .build();

            alignedContainer.addWidget(button);
        }
        alignedContainer.fit();

        addWidget(alignedContainer);
    }

    @Override
    public void update() {
        
    }

    @Override
    public boolean shouldPauseGame() {
        return false;
    }

    @Override
    public boolean shouldGuiCloseOnEsc() {
        return true;
    }
}
