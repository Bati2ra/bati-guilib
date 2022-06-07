package net.bati.guilib.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bati.guilib.gui.components.Button;
import net.bati.guilib.gui.components.Container;
import net.bati.guilib.gui.components.Widget;
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
        Button button2 = new Button(100,100).textComponent(new TextComponent().text("Buenas tardes señorita").size(0.5f).color(5000));
        button2.setIdentifier("boton+");
        button2.setZOffset(100);
        button2.setSize(1f);
        button2.setBoxWidth(200);
        button2.setBoxHeight(20);
        button2.registerLeftClickListener(() -> {

            getWidget("container").setSize(getWidget("container").getSize() + 0.1F);
        });
        button2.registerRightClickListener(() -> {
            getWidget("container").setSize(getWidget("container").getSize() - 0.1F);
        });

        button2.drawCallBack((matrices, x, y, delta) -> {
            DrawHelper.fillGradient(matrices, 50, 50, 150, 150, 16777215, 0.5f, 1);


        });
        addWidget(button2);

        Button button = (Button) new Button()
                .pos((window) -> new Vec2(window.getScaledWidth()/2, 10))
                .box(10,10)
                .size(1);

        button.drawCallBack((matrices, x, y, delta) -> {

        });


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
