package net.bati.guilib.utils

import net.minecraft.client.util.Window
import net.minecraft.client.util.math.MatrixStack

class Callback {
    interface Drawable {
        fun draw(matrices: MatrixStack?, x: Int, y: Int, delta: Float)
    }
    interface  Hoverable {
        fun isHovering(x: Int, y: Int): Boolean
    }
    interface Mouse {
        fun call(x: Double, y: Double, mouseButton: Int)
    }
    interface Pressable {
        fun call(keyCode : Int, scanCode : Int, modifiers : Int)
    }
    interface ScreenPosition {
        fun get(window : Window) : Vec2
    }
    interface Text {
        fun get() : String
    }
}