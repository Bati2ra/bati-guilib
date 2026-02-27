package net.bati.guilib.gui.components;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.bati.guilib.utils.Callback;
import net.bati.guilib.utils.font.TextComponent;
@Getter
@Setter
@SuperBuilder
public class TextLabel extends Widget {
    private TextComponent textComponent;
    private Callback.Text callback;



}
