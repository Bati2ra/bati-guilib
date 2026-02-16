package net.bati.guilib.utils

class Vec2(var x: Float, var y: Float) {
    fun update(x: Float, y: Float): Vec2 {
        this.x = x;
        this.y = y;
        return this;
    }
}