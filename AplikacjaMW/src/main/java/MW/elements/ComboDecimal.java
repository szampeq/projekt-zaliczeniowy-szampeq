package MW.elements;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class ComboDecimal extends JComboBox<Integer> {
    public JComboBox<Double> comboBox;
    Double[] array;

    public ComboDecimal(int start, int end, JPanel buttonPanel) {
        int elements = (end-start + 1) * 10;
        this.array = new Double[elements];

        for (int i = 0; i < elements; i++){
            double value = start + i * 0.1 + 0.1;
            this.array[i] = (double) Math.round(value*100)/100;
        }

        this.comboBox = new JComboBox<>(this.array);
        this.comboBox.setBackground(new Color(0x5555F5));
        this.comboBox.setForeground(Color.WHITE);
        this.comboBox.setFont(new Font("Mangal",Font.ITALIC,14));
        this.comboBox.setSelectedItem(this.array[(elements/2-1)]);
        buttonPanel.add(this.comboBox);
    }
}
