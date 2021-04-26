package MW.data;

import java.awt.*;
import java.util.Random;

public class Cell {
    int id;
    static int number = 0;
    boolean isActive;
    Color color;

    Cell(boolean isActive, Color color) {
        this.isActive = isActive;
        this.color = color;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getId() {
        return id;
    }

    public void setId() {
        id = number++;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void born() {
        final Random r = new Random();
        id = number++;
        isActive = true;
        color = new Color(r.nextInt(256),r.nextInt(256),r.nextInt(256),r.nextInt(256));
    }

}
