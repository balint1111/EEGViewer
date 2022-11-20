package root.main.swing.custom;

import javafx.beans.property.DoubleProperty;
import lombok.Getter;
import root.main.fx.custom.JFormattedTextFieldProperty;

import javax.swing.*;

public class JFxFormattedField extends JFormattedTextField {

    @Getter
    private DoubleProperty doubleProperty = new JFormattedTextFieldProperty(this);

    public JFxFormattedField() {
        super();
    }
}
