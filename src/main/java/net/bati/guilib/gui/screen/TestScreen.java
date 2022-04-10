package net.bati.guilib.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bati.guilib.gui.components.Button;
import net.bati.guilib.gui.components.Container;
import net.bati.guilib.utils.DrawHelper;
import net.bati.guilib.utils.PIVOT;
import net.bati.guilib.utils.Vec2;
import net.bati.guilib.utils.font.FontStyle;
import net.bati.guilib.utils.font.TextComponent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class TestScreen extends AdvancedScreen{


    public TestScreen(@Nullable Text title) {
        super(title);
    }

    @Override
    public void build() {
        Container container = (Container) new Container().showArea();
        container.setIdentifier("container");
        container.setZOffset(10);




        Button button = new Button(89,80).textComponent(new TextComponent().setText("Boton 1").setSize(0.5f).setColor(5000));
        button.setIdentifier("boton1");
        button.setZOffset(20);
        button.setSize(1f);

        button.registerLeftClickListener(() -> {
            System.out.println("SOY EL BOTON 1!");
        });
        container.addWidget(button);

        button = new Button(150, 45).textComponent(new TextComponent().setText("Boton 3").setSize(1f).setLineColor(1).setColor(10000).font(FontStyle.ARCADEX));
        button.setIdentifier("boton3");
        button.setBoxWidth(50);
        button.setZOffset(-10);
        container.addWidget(button);

        button = new Button(-20, 10).textComponent(new TextComponent().setText("Boton 2").setSize(1f).setLineColor(1).font(FontStyle.TEN));
        button.setIdentifier("boton2");
        button.setZOffset(4);
        button.registerLeftClickListener(() -> {
            System.out.println("SOY EL BOTON 2");
        });
        container.addWidget(button);
        container.fit();

      //  addWidget(container);
        button = (Button) new Button(-140,0).textComponent(new TextComponent().setText("Boton 4").setSize(1f).setColor(5000).font(FontStyle.TEN)).attach(PIVOT.RIGHT_MIDDLE);
        button.setIdentifier("boton6");
        button.setZOffset(20);
        button.setSize(1.5f);
        button.setPivot(PIVOT.LEFT_MIDDLE);
        button.registerLeftClickListener(() -> {
            System.out.println("SOY EL BOTON 4!");
        });
        addWidget(button);

        button = new Button(140,140).textComponent(new TextComponent().setText("Boton 4").setSize(2f).setOffsetPosition(new Vec2(2,2)).setColor(5000));
        button.setIdentifier("boton5");
        button.setZOffset(20);
        button.setSize(1f);
        button.setPivot(PIVOT.MIDDLE);
        button.registerLeftClickListener(() -> {
            System.out.println("SOY EL BOTON 4!");
        });
        addWidget(button);


        button = (Button) new Button(0,0).textComponent(new TextComponent().setText("Boton 4").setSize(0.5f).setColor(5000)).attach(PIVOT.MIDDLE);
        button.setIdentifier("boton4");
        button.setZOffset(20);
        button.setSize(0.5f);
        button.setPivot(PIVOT.MIDDLE);
        button.registerLeftClickListener(() -> {
            System.out.println("SOY EL BOTON 4!");
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
