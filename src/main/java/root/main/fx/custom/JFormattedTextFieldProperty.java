package root.main.fx.custom;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

@Slf4j
public class JFormattedTextFieldProperty extends SimpleDoubleProperty {
    private JFormattedTextField node;

    public JFormattedTextFieldProperty(JFormattedTextField node){
        this.node = node;
        node.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange(e);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange(e);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                onChange(e);
            }
        });
        addListener((observable, oldValue, newValue) -> {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace.length > 20) {
                node.setValue(newValue);
            }
        });
    }

    private void onChange(DocumentEvent e){
        Platform.runLater(() -> {
            try {
                String text = e.getDocument().getText(0, e.getDocument().getLength()).replaceAll(" ", "");
                if (StringUtils.hasText(text)) {
                    setValue(Double.valueOf(text.replaceAll(",",".")));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

    }



}
