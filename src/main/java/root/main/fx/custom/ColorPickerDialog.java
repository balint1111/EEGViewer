package root.main.fx.custom;

import com.sun.javafx.scene.control.DoubleField;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import root.main.fx.custom.LineProperty;

public class ColorPickerDialog {

    public static LineProperty display(LineProperty lineProperty) {

        //Stage init
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Dialog");

        stage.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                stage.close();
            }
        });
        //layout init
        VBox layout = new VBox();
        layout.setPadding(new Insets(10, 10, 10, 10));
        //Scene setUp
        Scene scene = new Scene(layout, 250, 150);
        stage.setScene(scene);

        //object declarations
        ColorPicker colorPicker = new ColorPicker();
        Button button = new Button("Submit");
        DoubleField doubleField = new DoubleField();

        //layout addChildren
        layout.getChildren().addAll(colorPicker, doubleField, button);

        //submit OnClick -> close
        button.setOnAction(e -> stage.close());

        //bindings
        bindProperties(lineProperty, colorPicker, doubleField);


        stage.showAndWait();
        return lineProperty;
    }

    private static void bindProperties(LineProperty lineProperty, ColorPicker colorPicker, DoubleField doubleField) {
        //colorBinding
        colorPicker.setValue((Color) lineProperty.getStrokeProperty().getValue());
        colorPicker.setOnAction(e  -> {
            lineProperty.getStrokeProperty().setValue(colorPicker.getValue());
        });
        //ScaleY binding
        doubleField.valueProperty().bindBidirectional(lineProperty.getAmplitude());
    }
}
