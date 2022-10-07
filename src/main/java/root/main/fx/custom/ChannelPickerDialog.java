package root.main.fx.custom;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class ChannelPickerDialog {

    public static ObservableList<Integer> display(List<String> channels) {

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

        ListView<String> listView = new ListView<String>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ObservableList<String> list = listView.getItems();

        list.addAll(channels);

        //layout addChildren
        layout.getChildren().addAll(listView);

        //bindings


        stage.showAndWait();
        return listView.getSelectionModel().getSelectedIndices();
    }

//    private static void bindProperties(LineProperty lineProperty, ColorPicker colorPicker, DoubleField doubleField) {
//        //colorBinding
//        colorPicker.setValue((Color) lineProperty.getStrokeProperty().getValue());
//        colorPicker.setOnAction(e  -> {
//            lineProperty.getStrokeProperty().setValue(colorPicker.getValue());
//        });
//        //ScaleY binding
//        doubleField.valueProperty().bindBidirectional(lineProperty.getAmplitude());
//    }
}
