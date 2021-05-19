package MW.data;

import MW.Main;
import MW.enums.Neighborhoods;

import javax.swing.*;
import java.awt.*;

public class JCanvasPanel extends JPanel {

    public DataManager dataManager;
    public boolean energyMap = false;

    public JCanvasPanel(DataManager dataManager){
        this.dataManager = dataManager;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int MSX = dataManager.getMeshSizeX();
        int MSY = dataManager.getMeshSizeY();
        int CS = dataManager.getCellSize();
        Graphics2D g2 = (Graphics2D) g;

        for (int j = 0; j < MSY; j++) {
            for (int i = 0; i < MSX; i++) {
                if (!energyMap)
                    g2.setColor(dataManager.cellMatrix[i][j].getColor());
                else {
                    if (dataManager.cellMatrix[i][j].getEnergy() == 0)
                        g2.setColor(Color.ORANGE);
                    else {
                        int changeValue = 10;
                        if (dataManager.neighborhoodType == Neighborhoods.Radius)
                            changeValue = 5;
                        int value = dataManager.cellMatrix[i][j].getEnergy() * changeValue;
                        if (value > 255)
                            value = 255;

                        Color dark = new Color(value, value, value);
                        g2.setColor(dark);
                    }
                }
                g2.fillRect(i * CS + CS, j * CS + CS, CS, CS);
            }
        }

        super.repaint();
    }

    @Override
    public void repaint() {
        super.repaint();
    }
}