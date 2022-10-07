package custom.component;

import javafx.beans.property.SimpleIntegerProperty;
import lombok.Getter;
import root.main.fx.custom.JFormattedTextFieldProperty;

import javax.swing.*;
import javax.swing.text.*;
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
        formatter.setOverwriteMode(true);

        maxValue.addListener((observable, oldValue, newValue) -> {
            formatter.setMaximum(newValue.intValue());
        });
        setFormatterFactory(new DefaultFormatterFactory(formatter));
//        ((AbstractDocument) getDocument ()).setDocumentFilter(new DocumentFilter() {
//            @Override
//            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
//                String textAfterReplacement = new StringBuilder (fb.getDocument ().getText (0, fb.getDocument ().getLength ())).replace (offset, offset + length, text).toString ();
//                System.out.println("asd");
//                int value = Integer.parseInt (textAfterReplacement);
//                if (value < 0) value = 0;
//                else if (value > maxValue.get()) value = maxValue.get();
//                super.replace (fb, 0, fb.getDocument ().getLength (), String.valueOf (value), attrs);
//            }
//        });

    }
}
