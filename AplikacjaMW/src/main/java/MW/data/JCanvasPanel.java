package MW.data;

import javax.swing.*;
import java.awt.*;

public class JCanvasPanel extends JPanel {

    public DataManager dataManager;

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
                g2.setColor(dataManager.cellMatrix[i][j].getColor());
                //if(dataManager.cellMatrix[i][j].isActive())
                    g2.fillRect(i * CS + CS, j * CS + CS, CS, CS);
            }
        }

/*
        g2.setColor(Color.white);
        g2.drawRect(CS, CS, MSX * CS, MSY * CS);
        for (int i = CS; i <= MSX * CS; i += CS) {
            g2.drawLine(i, CS, i, MSY * CS + CS);
            g2.drawLine(CS, i, MSX * CS + CS, i);
        }

 */
        super.repaint();
    }

    @Override
    public void repaint() {
        super.repaint();
    }
}