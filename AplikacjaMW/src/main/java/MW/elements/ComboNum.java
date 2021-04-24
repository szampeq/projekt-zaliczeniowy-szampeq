package MW.elements;

import javax.swing.*;
import java.awt.*;

public class ComboNum extends JComboBox<Integer> {
    public JComboBox<Integer> comboBox;
    Integer[] array;

    public ComboNum(int start, int end, JPanel buttonPanel) {
        int size = end-start + 1;
        this.array = new Integer[size];

        for (int i = 0; i < size; i++){
            this.array[i] = start + i;
        }

        this.comboBox = new JComboBox<>(this.array);
        this.comboBox.setBackground(new Color(0x5555F5));
        this.comboBox.setForeground(Color.WHITE);
        this.comboBox.setFont(new Font("Mangal",Font.ITALIC,14));
        this.comboBox.setSelectedItem(this.array[(size/2)]);
        buttonPanel.add(this.comboBox);
    }
}
