package custom.component;

import javafx.beans.property.SimpleIntegerProperty;
import lombok.Getter;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

@Getter
public class JFxIntegerField extends JFormattedTextField {
    private JFormattedTextFieldProperty prop;
    private SimpleIntegerProperty maxValue;
    private NumberFormatter formatter;

    public JFxIntegerField() {
        super();
        prop = new JFormattedTextFieldProperty(this);
        maxValue = new SimpleIntegerProperty();

        NumberFormat format = NumberFormat.getInstance();
        formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        maxValue.addListener((observable, oldValue, newValue) -> {
            formatter.setMaximum(newValue.intValue());
        });
        setFormatterFactory(new DefaultFormatterFactory(formatter));

    }
}
