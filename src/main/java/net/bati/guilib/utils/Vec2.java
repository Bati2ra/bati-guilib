package net.bati.guilib.utils;

public class Vec2 {
    private int x;
    private int y;

    public Vec2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2 getVec(int x, int y) {
        return new Vec2(x,y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}