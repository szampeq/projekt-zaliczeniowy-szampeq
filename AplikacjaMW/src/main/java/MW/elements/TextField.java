package MW.elements;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;

import static java.text.NumberFormat.getInstance;
import static java.text.NumberFormat.getNumberInstance;

public class TextField extends JTextField {
    public JFormattedTextField textField;


    public TextField(Double defaultValue, Double min, Double max, JPanel buttonPanel) {
        final NumberFormat format = getNumberInstance(new Locale("en_US"));
        NumberFormatter formatter = new NumberFormatter(format);
        format.setGroupingUsed(false);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(min);
        formatter.setMaximum(max);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(false);
        textField = new JFormattedTextField(formatter);
        textField.setValue(defaultValue);
        // visual
        textField.setHorizontalAlignment(SwingConstants.LEFT);
        textField.setFont(new Font("Tahoma",Font.PLAIN,12));
        textField.setBackground(Color.WHITE);
        textField.setValue(textField.getValue());
        // button panel
        buttonPanel.add(this.textField);
    }
}
