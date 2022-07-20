package net.bati.guilib.gui.screen;

import net.bati.guilib.gui.components.Button;
import net.bati.guilib.utils.Vec2;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;


public class TestScreen extends AdvancedScreen{


    public TestScreen(@Nullable Text title) {
        super(title);
    }

    @Override
    public void build() {
        Button button = Button.builder("uniqueID")
                .boxWidth(200)
                .boxHeight(20)
                .positionCallback((window) -> new Vec2(window.getScaledWidth()/2, 60))
                .clickCallback((x, y, type) -> System.out.println("Click!"))
                .postDrawCallback((matrices, x, y, delta) -> {
                    // ...
                })
                .build();

        addWidget(button);

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
