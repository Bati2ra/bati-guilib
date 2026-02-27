package net.bati.guilib.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.*;
@Getter
@Setter
@SuperBuilder
public class Container extends Widget implements IWidgetsStorage {

    @Builder.Default private HashMap<String, Widget> widgets = new HashMap<>();

    public Container(String identifier) {
        super(identifier, 1, 1);
        setWidgets(new HashMap<>()); // SuperBuilder ignora los valores por defecto si no se usa el builder, por lo que hay que inicializar todos los atributos
    }
}
