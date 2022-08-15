package net.bati.guilib.gui.screen;

import net.bati.guilib.gui.components.*;
import net.bati.guilib.utils.Orientation;
import net.bati.guilib.utils.Pivot;
import net.bati.guilib.utils.Vec2;
import net.bati.guilib.utils.font.TextComponent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;


public class TestScreen extends AdvancedScreen{


    public TestScreen(@Nullable Text title) {
        super(title);
    }

    @Override
    public void build() {
       /* AlignedContainer alignedContainer = new AlignedContainer("buttons");
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

        addWidget(alignedContainer);*/
        addWidgets(createAdvancedContainer(), createNavigationBar());
    }
    private AlignedContainer createNavigationBar() {
        AlignedContainer alignedContainer = new AlignedContainer("buttonsx");
        alignedContainer.setAlign(Orientation.HORIZONTAL);
        alignedContainer.setPivot(Pivot.MIDDLE_BOT);
        alignedContainer.setSpacing(10);
        alignedContainer.setPositionListener((window) -> new Vec2(window.getScaledWidth()/2, (int) (window.getScaledHeight() - 10* getOptions().getScaleY())));

        Button button;
        for(int i = 0; i< 4; i++) {
            int k = i;
            button = Button.builder("testx"+i)
                    .boxWidth(20)
                    .boxHeight(20)
                    .onClick((a,b,c) -> System.out.println("E"))
                    .build();
            alignedContainer.addWidget(button);
        }

        alignedContainer.fit();
        return alignedContainer;
    }
    private Container createAdvancedContainer() {
        Container container = Container.builder()
                .identifier("advanced")
                .ignoreBox(true)
                .onInit(widget -> {

                    if(widget instanceof Container widgetContainer) {
                        widgetContainer.addWidget(createRightAccordions());
                        //widgetContainer.addWidget(createLeftSliderContainer());
                    }

                })
                .build();
        return container;
    }

    private ScrollContainer createRightSliderContainer() {
        ScrollContainer container = new ScrollContainer("right_sliders");
        container.setBoxWidth(100);
        container.setBoxHeight(200);
        container.setOffsetPosition(new Vec2(10,10));
        container.addWidget(createRightAccordions());
        container.disableObjectCulling();
        container.setBarPosition(ScrollContainer.BAR.RIGHT);
        container.setHideScrollAnimation(true);
        container.setBarWidth(3);
        container.fit();
        return container;
    }

    private AlignedContainer createRightAccordions() {
        AlignedContainer container = new AlignedContainer("right_accordions");
        container.setSpacing(20);
        container.setAlign(Orientation.VERTICAL);

        Accordion accordion;
        Button button;
        for( int i=0; i<8; i++) {
            accordion = new Accordion("accordion" + i);
            button = Button.builder("test-accordion"+i).boxWidth(30).boxHeight(30).onClick((a,b,c) -> System.out.println("A")).build();
            Slider slider = Slider.builder().identifier("aaa"+i).min(0).max(1).boxWidth(100).boxHeight(4).build();
            accordion.addWidget(slider);
            accordion.addWidget(button);
            container.addWidget(accordion);
        }
        container.fit();
        System.out.println(container.getBoxHeight());
        return container;
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
