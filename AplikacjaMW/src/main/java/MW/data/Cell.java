package MW.data;

import java.awt.*;

public class Cell {
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
}
