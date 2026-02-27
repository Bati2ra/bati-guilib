package net.bati.guilib.utils

import net.bati.guilib.gui.components.Widget
import net.minecraft.client.gui.GuiGraphics
import org.jetbrains.annotations.Nullable
import java.awt.Window

class Callback {
    interface Drawable {
        fun draw(@Nullable widget: Widget?, context: GuiGraphics, x: Float, y: Float, delta: Float)
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
        fun get(widget : Widget, currentPos: Vec2, window : Window) : Vec2
    }
    interface Text {
        fun get() : String
    }
}