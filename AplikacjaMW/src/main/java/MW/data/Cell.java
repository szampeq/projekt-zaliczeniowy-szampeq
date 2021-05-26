package MW.data;

import java.awt.*;
import java.util.Random;

public class Cell {
    int id;
    Point coords;
    static int number = 1;
    boolean isActive;
    Color color;
    int energy = 0;
    double dislocation = 0;
    boolean isRecrystallized;
    final static Random r = new Random();

    Cell(boolean isActive, Color color, Point coords) {
        this.isActive = isActive;
        this.color = color;
        this.coords = coords;
        this.isRecrystallized = false;
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

    public Point getCoords() {
        return coords;
    }

    public void setCoords(Point coords) {
        this.coords = coords;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public double getDislocation() {
        return dislocation;
    }

    public void setDislocation(double dislocation) {
        this.dislocation = dislocation;
    }

    public void born() {
        this.setId();
        isActive = true;
        color = new Color(128 + r.nextInt(128),128+ r.nextInt(128),128 + r.nextInt(128));
    }

    public void recrystallization() {
        this.setId();
        this.dislocation = 0;
        this.isRecrystallized = true;
        color = new Color(r.nextInt(128),r.nextInt(128),r.nextInt(128));
    }
    public static void resetNumber() {
        number = 1;
    }

    public void copyData(Cell source) {
        this.id = source.id;
        this.color = source.color;
        this.isActive = source.isActive;
        this.energy = source.energy;
        this.isRecrystallized = source.isRecrystallized;
    }
}
