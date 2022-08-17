package net.bati.guilib.utils

import net.bati.guilib.gui.components.Widget
import net.minecraft.client.util.Window
import net.minecraft.client.util.math.MatrixStack
import org.jetbrains.annotations.Nullable

class Callback {
    interface Drawable {
        fun draw(@Nullable widget: Widget?,matrices: MatrixStack?, x: Float, y: Float, delta: Float)
    }
    interface DrawableBasic {
        fun draw()
    }
    interface  Hoverable {
        fun isHovering(x: Double, y: Double): Boolean
    }
    interface Mouse {
        fun call(widget : Widget, x: Double, y: Double, mouseButton: Int)
    }
    interface Pressable {
        fun call(widget : Widget, keyCode : Int, scanCode : Int, modifiers : Int)
    }
    interface ScreenPosition {
        fun get(widget : Widget, window : Window) : Vec2
    }
    interface Text {
        fun get() : String
    }
}