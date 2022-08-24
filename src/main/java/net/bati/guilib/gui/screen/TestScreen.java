package net.bati.guilib.gui.screen;

import net.bati.guilib.gui.components.*;
import net.bati.guilib.utils.Orientation;
import net.bati.guilib.utils.Pivot;
import net.bati.guilib.utils.Sound;
import net.bati.guilib.utils.Vec2;
import net.bati.guilib.utils.font.TextComponent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;


public class TestScreen extends AdvancedScreen{


    public TestScreen(@Nullable Text title) {
        super(title);
    }

    @Override
    public void build() {
        Radio radio = new Radio("radio");
        radio.setAlign(Orientation.VERTICAL);
        radio.setSpacing(20);
        for(int i=0; i<5; i++) {
            Checkbox checkbox = new Checkbox("checkbox" + i);
            checkbox.setBoxWidth(10);
            checkbox.setBoxHeight(10);
            checkbox.setCheckType(Checkbox.CHECK_TYPE.BOX);
            checkbox.setCheckColor(16777215);
            checkbox.setDisplayText("Textura por defecto " + i, 0.5F);
            checkbox.setAlignRight(true);
            checkbox.setClickEnabledSound(SoundEvents.UI_BUTTON_CLICK);
            checkbox.setOnChange((check, action) -> Sound.ENABLED);
            radio.addOption(checkbox);
        }
       // addWidget(radio);



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
        ScrollContainer asd = createRightSliderContainer();
        asd.addWidget(createNavigationBar());
        addWidgets(createAdvancedContainer(), asd);
    }
    private FlexContainer createNavigationBar() {
        FlexContainer alignedContainer = new FlexContainer("buttonsx");
        alignedContainer.setAlign(Orientation.HORIZONTAL);
        alignedContainer.setPivot(Pivot.LEFT_TOP);
        alignedContainer.setSpacing(20);
        alignedContainer.setBreakLine(4);
        alignedContainer.setHide(false);
        alignedContainer.setZ(-20);
        alignedContainer.withOffsets(true);
        alignedContainer.setShowArea(true);
        alignedContainer.setIgnoreInvisibles(true);
        alignedContainer.setLookForVisibilityChanges(true);
        alignedContainer.setOffsetPosition(new Vec2(20, 20));
       // alignedContainer.setPositionListener((w,window) -> new Vec2(window.getScaledWidth()/2, (int) ((window.getScaledHeight()/2 ))));

        Button button;
        for(int i = 0; i< 10; i++) {
            int k = i;
            button = Button.builder("testx"+i)
                    .boxWidth(40)
                    .boxHeight(20)
                    .visible(!(i == 2 || i==3 || i==4))
                    .textComponent(new TextComponent().text("s"+i))
                    .z(10*i)
                    .onClick((w,a,b,c) -> {
                        alignedContainer.getWidget("testx"+2).setVisible(true);
                        alignedContainer.getWidget("testx"+3).setVisible(true);
                        alignedContainer.getWidget("testx"+4).setVisible(true);
                    })
                    .build();

            if(i==2) {
                button.setOffsetPosition(new Vec2(40, -10));
            }
            alignedContainer.addWidget(button);
        }


        return alignedContainer;
    }
    private Container createAdvancedContainer() {
        Container container = Container.builder()
                .identifier("advanced")
                .z(45)
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
        container.setBoxWidth(300);
        container.setBoxHeight(100);
        container.setSize(1f);
        container.setZ(34);
        container.setOffsetPosition(new Vec2(90,10));
        for(int i=0;i <5;i++) {
         //   container.addWidget(Button.builder("asa"+i).offsetPosition(new Vec2(10, 100*i)).boxWidth(20).boxHeight(20).size(2).onClick((w,a,b,c) -> System.out.println("SEX")).build());

        }
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
        container.setIgnoreBox(false);
        container.setShowArea(true);
        container.withOffsets(true);
        container.setOffsetPosition(new Vec2(20, 0));
        container.setOnUpdate(widget -> {
            widget.getParent().setBoxWidth(widget.getBoxWidth());
            widget.getParent().setBoxHeight(widget.getBoxHeight());
        });
        container.setZ(45);

        Accordion accordion;
        Button button;
        for( int i=0; i<5; i++) {
            accordion = new Accordion("accordion" + i);
            accordion.setZ(5*i);
            if(i==1) {
                accordion.setOffsetPosition(new Vec2(10, 2));
            }
            if(i==4) {
                accordion.setOffsetPosition(new Vec2(-2, 20));
            }

            button = Button.builder("test-accordion"+i).boxWidth(30).boxHeight(30).z(-4 * i).onClick((w,a,b,c) -> System.out.println("A")).build();
            Slider slider = Slider.builder().identifier("aaa"+i).min(0).max(1).boxWidth(100).boxHeight(4).build();
            accordion.addWidget(slider);
            accordion.addWidget(button);
            container.addWidget(accordion);
        }
        container.fit();

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
