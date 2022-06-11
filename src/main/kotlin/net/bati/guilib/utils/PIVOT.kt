package net.bati.guilib.utils

enum class PIVOT(private val x: Float, private val y: Float) {
    /**
           middle top = width/2, 0
           middle = width/2 height/2
           middle bot = width/2, height

           left top = 0 , 0
           left middle = 0, height/2
           left bot = 0 , height

           right top = width , 0
           right middle = width , height/2
           right bot = width, height
      */
    LEFT_TOP(0f, 0f), LEFT_MIDDLE(0f, 0.5f), LEFT_BOT(0f, 1f), MIDDLE_TOP(0.5f, 0f), MIDDLE(0.5f, 0.5f), MIDDLE_BOT(0.5f, 1f), RIGHT_TOP(1f, 0f), RIGHT_MIDDLE(1f, 0.5f), RIGHT_BOT(1f, 1f);

    fun getX(width: Float): Float {
        return width * x
    }

    fun getY(height: Float): Float {
        return height * y
    }
}
