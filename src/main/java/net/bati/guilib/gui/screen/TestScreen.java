package net.bati.guilib.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bati.guilib.gui.components.Button;
import net.bati.guilib.gui.components.Container;
import net.bati.guilib.utils.BatiLib;
import net.bati.guilib.utils.DrawHelper;
import net.bati.guilib.utils.PIVOT;
import net.bati.guilib.utils.Vec2;
import net.bati.guilib.utils.font.FontStyle;
import net.bati.guilib.utils.font.TextComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class TestScreen extends AdvancedScreen{


    public TestScreen(@Nullable Text title) {
        super(title);
    }

    @Override
    public void build() {
        Button button2 = new Button(0,0).textComponent(new TextComponent().setText("+").setSize(0.5f).setColor(5000));
        button2.setIdentifier("boton+");
        button2.setZOffset(20);
        button2.setSize(1f);
        button2.setBoxWidth(20);
        button2.setBoxHeight(20);
        button2.registerLeftClickListener(() -> {
            getWidget("container").setSize(getWidget("container").getSize() + 0.1F);
        });
        button2.registerRightClickListener(() -> {
            getWidget("container").setSize(getWidget("container").getSize() - 0.1F);
        });
        addWidget(button2);

        button2 = new Button(0,40).textComponent(new TextComponent().setText("+2").setSize(0.5f).setColor(5000));
        button2.setIdentifier("boton+2");
        button2.setZOffset(20);
        button2.setSize(1f);
        button2.setBoxWidth(20);
        button2.setBoxHeight(20);

        button2.registerLeftClickListener(() -> {
            ((Container)getWidget("container")).getWidget("container2").setSize(((Container)getWidget("container")).getWidget("container2").getSize() + 0.1F);
        });
        button2.registerRightClickListener(() -> {
            ((Container)getWidget("container")).getWidget("container2").setSize(((Container)getWidget("container")).getWidget("container2").getSize() - 0.1F);

        });
        addWidget(button2);

        Container container = (Container) new Container().shouldIgnoreContainerHitbox(true).showArea().attach(PIVOT.MIDDLE);
        container.setPivot(PIVOT.MIDDLE);
        container.setIdentifier("container");
        container.setPosition(0, 0);
        container.setBoxWidth(150);
        container.setBoxHeight(150);
        container.setSize(1f);
        container.setOpacity(0.4F);

        container.setZOffset(10);

        Container container2 = (Container) new Container().shouldIgnoreContainerHitbox(true).showArea();
        container2.setIdentifier("container2");
        container2.setPivot(PIVOT.MIDDLE);
        container2.setPosition(20,20);
        container2.setBoxWidth(90);
        container2.setBoxHeight(90);
        container2.setZOffset(11);
        container2.setSize(0.5F);



        Button button = new Button(99,80).textComponent(new TextComponent().setText("Boton 1").setSize(0.5f).setColor(5000));
        button.setIdentifier("boton1");
        button.setPivot(PIVOT.RIGHT_MIDDLE);
        button.setZOffset(20);
        button.setSize(1f);

        button.registerLeftClickListener(() -> {
            System.out.println("SOY EL BOTON 1!");
        });
        container2.addWidget(button);

        button = new Button(250, 45).textComponent(new TextComponent().setText("Boton 3").setSize(1f).setLineColor(1).setColor(10000).font(FontStyle.ARCADEX));
        button.setPivot(PIVOT.MIDDLE);
        button.setIdentifier("boton3");
        button.setBoxWidth(70);
        button.setSize(1f);
        button.setZOffset(-10);
        container2.addWidget(button);

        button = new Button(-20, 10).textComponent(new TextComponent().setText("Boton 2").setSize(1f).setLineColor(1).font(FontStyle.TEN));
        button.setIdentifier("boton2");
        button.setPivot(PIVOT.MIDDLE);
        button.setSize(1f);
        button.setZOffset(4);
        button.registerLeftClickListener(() -> {
            System.out.println("SOY EL BOTON 2");
        });
        container2.addWidget(button);
        container2.fit();

        container.addWidget(container2);
        container.fit();
        addWidget(container);

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
