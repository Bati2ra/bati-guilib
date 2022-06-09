package net.bati.guilib.gui.screen;

import net.bati.guilib.gui.components.Button;
import net.bati.guilib.gui.components.TextLabel;
import net.bati.guilib.gui.components.WidgetBuilder;
import net.bati.guilib.utils.Vec2;
import net.bati.guilib.utils.font.TextComponent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;


public class TestScreen extends AdvancedScreen{


    public TestScreen(@Nullable Text title) {
        super(title);
    }

    @Override
    public void build() {
        TextLabel textLabel = new WidgetBuilder<>(new TextLabel())
                .identifier("name")
                .pos(100,10)
                .component(new TextComponent())
                .text(() -> "HOLA")
                .build();


        Button button = new WidgetBuilder<>(new Button())
                .identifier("boton")
                .pos(50, 50)
                .box(200, 20)
                .onClick((s, a, c) -> System.out.println("CLICK! "))
                .build();

        addWidgets(button, textLabel);

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
