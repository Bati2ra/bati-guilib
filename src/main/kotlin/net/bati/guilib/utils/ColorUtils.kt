package net.bati.guilib.utils

import com.mojang.blaze3d.systems.RenderSystem

object ColorUtils {
    @JvmStatic
    fun toHex(color: Int): Int {
        return String.format("%02x%02x%02x%02x", color shr 24 and 0xFF, color shr 16 and 0xFF, color shr 8 and 0xFF, color and 0xFF).toLong(16).toInt()
    }
    @JvmStatic
    fun toHex(color: Int, alpha: Float): Int {
        return String.format("%02x%02x%02x%02x", (alpha * 255).toInt(), color shr 16 and 0xFF, color shr 8 and 0xFF, color and 0xFF).toLong(16).toInt()
    }
    @JvmStatic
    fun hexColor(pColor: Int, alpha: Float) {
        val h2 = (pColor shr 16 and 0xFF) / 255.0f
        val h3 = (pColor shr 8 and 0xFF) / 255.0f
        val h4 = (pColor and 0xFF) / 255.0f
        val h1 = 1.0f
        RenderSystem.setShaderColor(h1 * h2, h1 * h3, h1 * h4, alpha)
    }
}
