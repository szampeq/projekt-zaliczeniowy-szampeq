package MW.data;

import MW.Main;

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
                    else
                        g2.setColor(Color.BLACK);
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