package MW.elements;

import javax.swing.*;
import java.awt.*;

public class Label extends JLabel {
    public JLabel label;
    String text;

    public Label(String text, JPanel buttonPanel) {
        this.text = text;
        this.label = new JLabel(text);
        this.label.setForeground(Color.WHITE);
        this.label.setOpaque(true);
        this.label.setBackground(new Color(0x0C0C78));
        this.label.setHorizontalAlignment(JLabel.CENTER);
        this.label.setVerticalAlignment(JLabel.CENTER);
        this.label.setFont(new Font("Mangal",Font.BOLD,12));
        buttonPanel.add(this.label);
    }
}
