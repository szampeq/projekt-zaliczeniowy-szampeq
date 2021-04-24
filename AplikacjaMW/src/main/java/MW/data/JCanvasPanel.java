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
        int MS = dataManager.getMeshSize();
        int CS = dataManager.getCellSize();
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.RED);
        for (int j = 0; j < MS; j++) {
            for (int i = 0; i < MS; i++) {
                if(dataManager.matrix[i][j] == 1)
                    g2.fillRect(i * CS + CS, j * CS + CS, CS, CS);
            }
        }

        g2.setColor(Color.white);
        g2.drawRect(CS, CS, MS * CS, MS * CS);
        for (int i = CS; i <= MS * CS; i += CS) {
            g2.drawLine(i, CS, i, MS * CS + CS);
            g2.drawLine(CS, i, MS * CS + CS, i);
        }
        super.repaint();
    }

    @Override
    public void repaint() {
        super.repaint();
    }
}