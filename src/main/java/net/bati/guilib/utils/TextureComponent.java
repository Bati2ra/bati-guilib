package net.bati.guilib.utils;

import net.minecraft.util.Identifier;

public class TextureComponent {
    protected int u;
    protected int v;
    protected int textureWidth;
    protected int textureHeight;
    protected Identifier resource;

    public TextureComponent(int u, int v, int textureWidth, int textureHeight, Identifier resource) {
        this.u = u;
        this.v = v;
        this.textureWidth = textureWidth;
        this.textureHeight =textureHeight;
        this.resource = resource;
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }

    public Identifier getResource() {
        return resource;
    }
}
