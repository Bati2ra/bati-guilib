package net.bati.guilib.gui.screen;

import net.bati.guilib.gui.components.Button;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class TestScreen extends AdvancedScreen{


    public TestScreen(@Nullable Text title) {
        super(title);
    }

    @Override
    public void init() {
        Button button = new Button(10,10);
        button.setIdentifier("boton1");
        button.setZOffset(0);
        button.registerLeftClickListener(() -> {
            System.out.println("SOY EL BOTON 1!");
        });
        addWidget(button);

        button = new Button(40, 10);
        button.setIdentifier("botton2");
        button.setZOffset(2);
        button.registerLeftClickListener(() -> {
            System.out.println("SOY EL BOTON 2");
        });
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
