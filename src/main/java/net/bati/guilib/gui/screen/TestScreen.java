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
                .positionListener((window) -> new Vec2(window.getScaledWidth()/2, 60))
                .onClick((x, y, type) -> System.out.println("Click!"))
                .onPostDraw((widget, matrices, x, y, delta) -> {
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
