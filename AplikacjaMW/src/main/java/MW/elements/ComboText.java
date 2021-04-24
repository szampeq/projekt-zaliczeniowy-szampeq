package MW.elements;

import javax.swing.*;
import java.awt.*;

public class ComboText extends JComboBox<String> {
    public JComboBox<String> comboBox;

    public ComboText(String[] strings, JPanel buttonPanel) {
        this.comboBox = new JComboBox<>(strings);
        this.comboBox.setBackground(new Color(0x5555F5));
        this.comboBox.setForeground(Color.WHITE);
        this.comboBox.setFont(new Font("Mangal",Font.BOLD,14));
        buttonPanel.add(this.comboBox);
    }
}
