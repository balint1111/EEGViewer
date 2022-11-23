package root.main.fx.custom;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import lombok.Getter;
import root.main.DataController;

import java.io.IOException;
import java.util.*;

@Getter
public class MyPolyline extends HBox {

    @FXML
    private Polyline polyline;

    @FXML
    private Line line;

//    @FXML
//    private VBox controlVbox;

//    @FXML
//    private Label amplitudeLabel;

//    @FXML
//    private Label nameLabel;

//    @FXML
//    private Button button;

    private Integer channelNumber;

    private final List<Double> xVector = new ArrayList<>();

    private final List<Double> yVector = new ArrayList<>();

    private MyPolyLineProperty myPolyLineProperty;

    private ObservableList<Double> polyLineList;

    private UpdateHandler updateHandler;

    public MyPolyline(Integer selectionNumber,
                      Pane parent,
                      UpdateHandler updateHandler
    ) {
        this.channelNumber = updateHandler.getSelectedChannels().get(selectionNumber);
        DataController dataController = updateHandler.getController().getDataController();
        load(parent);
        polyLineList = polyline.getPoints();
        polyline.setStrokeWidth(0.8d);

        layoutYProperty().bind(updateHandler.getBaseOffsetProperty().add(updateHandler.getLineSpacingProperty().multiply(updateHandler.getAmplitudeProperty()).multiply(selectionNumber + 1)));

//        DoubleProperty controlVboxWidthProperty = getControlVboxWidthProperty();
        DoubleProperty prefWidthProperty = updateHandler.getController().getGroup().prefWidthProperty();
        this.prefWidthProperty().bind(prefWidthProperty);
        Tooltip t = new Tooltip(dataController.getDataModel().getEeg_file().getHeader().getLabelsOfTheChannels().get(channelNumber));
        Tooltip.install(polyline, t);
        initLineProperty(prefWidthProperty);
        lineInit();
//        nameLabelInit(dataController, channelNumber);
//        setStyleClasses();
    }

    private void lineInit() {
        line.visibleProperty().bind(updateHandler.getController().getProperties().getZeroLineVisibleProperty());
        line.strokeProperty().bindBidirectional(myPolyLineProperty.getStrokeProperty());
        line.endXProperty().bind(myPolyLineProperty.getHorizontalResolution());
    }

//    private void nameLabelInit(DataController dataController, int channelNumber) {
//        if (nameLabel != null) nameLabel.textFillProperty().bind(myPolyLineProperty.getStrokeProperty());
//        if (nameLabel != null) {
//            nameLabel.setText(dataController.getDataModel().getEeg_file().getHeader().getLabelsOfTheChannels().get(channelNumber));
//        }
//
//    }

    private void initLineProperty(DoubleProperty prefWidth) {
        myPolyLineProperty = new MyPolyLineProperty(polyline.strokeProperty(), new SimpleDoubleProperty(0), null, new SimpleIntegerProperty());
        myPolyLineProperty.getHorizontalResolution().bind(prefWidth.subtract(2));
        myPolyLineProperty.getAmplitude().bind(updateHandler.getAmplitudeProperty());
        myPolyLineProperty.getAmplitude().addListener(this::amplitudeListener);
    }

    private void load(Pane parent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/MyPolyline.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        parent.getChildren().add(this);
        updateHandler.getMyPolylineList().add(this);
    }

//    private SimpleDoubleProperty getControlVboxWidthProperty() {
//        SimpleDoubleProperty controlVboxWidthProperty = new SimpleDoubleProperty();
//        controlVbox.prefWidthProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue.doubleValue() > controlVbox.getMaxWidth()) {
//                controlVboxWidthProperty.setValue(controlVbox.getMaxWidth());
//            } else if (newValue.doubleValue() < controlVbox.getMinWidth()) {
//                controlVboxWidthProperty.setValue(controlVbox.getMinWidth());
//            } else {
//                controlVboxWidthProperty.setValue(newValue);
//            }
//        });
//        return controlVboxWidthProperty;
//    }

//    public void buttonClick(ActionEvent actionEvent) {
//        myPolyLineProperty = ColorPickerDialog.display(this.myPolyLineProperty);
//        update();
//    }

    public void update() {
        List<Double> updateList = new ArrayList<>();
        //System.out.println("xvectorSize: " + xVector.size());
        for (int i = 0; i < xVector.size(); i++) {
            updateList.add(xVector.get(i));
            updateList.add(-yVector.get(i) * myPolyLineProperty.getAmplitude().getValue());
        }
        Platform.runLater(() -> {
            polyLineList.setAll(updateList);
        });
    }

    public void setYVector(List<Double> yVector) {
        if (yVector == null) return;
        this.yVector.clear();
        this.yVector.addAll(yVector);
        xVector.clear();
        for (int i = 0; i < yVector.size(); i++) {
            xVector.add((double) i);
        }
    }


    private void amplitudeListener(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        update();
//        if (amplitudeLabel != null) amplitudeLabel.setText(newValue.toString());
    }
}
