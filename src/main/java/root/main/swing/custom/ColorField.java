package root.main.swing.custom;

import de.lars.colorpicker.ColorPicker;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ColorField extends JLabel {

    @Getter
    private ObjectProperty<javafx.scene.paint.Color> colorProperty = new SimpleObjectProperty<>();

    public ColorField() {
        super();
        setOpaque(true);
        colorProperty.addListener((observable, oldValue, newValue) -> {
            this.setBackground(new Color((float) newValue.getRed(), (float) newValue.getGreen(), (float) newValue.getBlue()));
//            this.setBorder(null);
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                javafx.scene.paint.Color oldColor = colorProperty.get();
                Color newColor = new Color((float) oldColor.getRed(), (float) oldColor.getGreen(), (float) oldColor.getBlue());
                openColorPickerDialog(newColor);
            }
        });
    }

    private void openColorPickerDialog(Color initialColor) {
        final ColorPicker cp = new ColorPicker(initialColor != null ? initialColor : Color.RED);
        cp.addColorListener(selected -> colorProperty.set(new javafx.scene.paint.Color(selected.getRed() / 255.0, selected.getGreen() / 255.0, selected.getBlue() / 255.0, selected.getAlpha() / 255.0)));

        JDialog dialog = ColorPicker.createDialog(cp, "Choose Color", "Ok", "Cancel", new Dimension(500, 350), initialColor);
        dialog.pack();
        dialog.setLocationRelativeTo(null);

        dialog.setVisible(true);
        dialog.dispose();
    }
}
