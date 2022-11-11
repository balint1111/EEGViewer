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

public class ColorPickerDialog {

    public static MyPolyLineProperty display(MyPolyLineProperty myPolyLineProperty) {

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
        bindProperties(myPolyLineProperty, colorPicker, doubleField);


        stage.showAndWait();
        return myPolyLineProperty;
    }

    private static void bindProperties(MyPolyLineProperty myPolyLineProperty, ColorPicker colorPicker, DoubleField doubleField) {
        //colorBinding
        colorPicker.setValue((Color) myPolyLineProperty.getStrokeProperty().getValue());
        colorPicker.setOnAction(e  -> {
            myPolyLineProperty.getStrokeProperty().setValue(colorPicker.getValue());
        });
        //ScaleY binding
        doubleField.valueProperty().bindBidirectional(myPolyLineProperty.getAmplitude());
    }
}
