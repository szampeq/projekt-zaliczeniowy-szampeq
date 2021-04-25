package MW.data;

import java.awt.*;

public class Cell {
    int id;
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

    public void setId(int id) {
        this.id = id;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }


}
