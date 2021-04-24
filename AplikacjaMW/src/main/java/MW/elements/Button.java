package MW.elements;

import javax.swing.*;
import java.awt.*;

public class Button extends JButton {
    public JButton button;
    String text;

    public Button(String text, JPanel buttonPanel) {
        this.text = text;
        this.button = new JButton(text);
        this.button.setBackground(new Color(0xD96102));
        this.button.setForeground(Color.WHITE);
        this.button.setFont(new Font("Mangal",Font.BOLD,14));
        buttonPanel.add(this.button);
    }

}
