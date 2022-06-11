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
        /*Button button = Button.builder("button")
                .positionCallback((a) -> new Vec2(20,20))
                .boxWidth(200)
                .boxHeight(20)
                .enabled(true)
                .build();

        addWidget(button);*/

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
