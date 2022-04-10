package net.bati.guilib.utils;

public enum PIVOT {
    /*
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
    LEFT_TOP(0,0),
    LEFT_MIDDLE(0, 0.5F),
    LEFT_BOT(0, 1),
    MIDDLE_TOP(0.5F, 0),
    MIDDLE(0.5F, 0.5F),
    MIDDLE_BOT(0.5F, 1),
    RIGHT_TOP(1, 0),
    RIGHT_MIDDLE(1, 0.5F),
    RIGHT_BOT(1,1);

    private float x;
    private float y;
    private PIVOT(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX(float width) {
        return width * x;
    }

    public float getY(float height) {
        return height * y;
    }
}
