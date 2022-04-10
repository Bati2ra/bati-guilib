package net.bati.guilib.utils.font;

import net.minecraft.util.Identifier;

public class Font {
    private Identifier id;
    private float height;
    public Font(Identifier id, float height) {
        this.id = id;
        this.height = height;
    }

    public float getHeight() {
        return height;
    }

    public Identifier getId() {
        return id;
    }
}