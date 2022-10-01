package custom.component;

import custom.dialogs.ColorPickerDialog;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import root.main.DataController;
import root.main.MainController;
import root.main.UpdateHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MyScrollBar extends ScrollBar implements Initializable {

    private MainController mainController;
    private UpdateHandler updateHandler;

    public MyScrollBar() {}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Autowired
    public void init(MainController mainController) {
        this.mainController = mainController;
        visibleAmountProperty().bind(mainController.getPageSizeProperty());
        maxProperty().bind(mainController.getNumberOfDateRecordsProperty().subtract(visibleAmountProperty()));
    }
//    @Autowired
    public void setUpdateHandler(UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
//        layoutYProperty().bind(updateHandler.getViewportHeightProperty().subtract(heightProperty()));
        prefWidthProperty().bind(updateHandler.getViewportWidthProperty());
//        System.out.println("2");
        valueProperty().addListener((observable, oldValue, newValue) -> {
            mainController.getDataController().jumpToPosition(newValue.intValue());
        });
    }


}
