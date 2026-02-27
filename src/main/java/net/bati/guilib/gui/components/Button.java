package net.bati.guilib.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class Button extends Widget {

    public Button(String identifier) {
        super(identifier, 0, 0);

    }
    public Button(String identifier, int boxWidth, int boxHeight) {
        super(identifier, boxWidth, boxHeight);

    }

    
}
