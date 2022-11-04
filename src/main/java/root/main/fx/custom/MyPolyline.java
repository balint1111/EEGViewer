package root.main.fx.custom;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import root.main.common.enums.Modes;

import java.io.IOException;
import java.util.*;

@Getter
public class MyPolyline extends HBox {

    @FXML
    private Polyline polyline;

    @FXML
    private Line line;

    @FXML
    private VBox controlVbox;

    @FXML
    private Label amplitudeLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Button button;

    private DataController dataController;

    private Integer channelNumber;

    private List<Double> xVector = new ArrayList<>();

    private List<Double> yVector = new ArrayList<>();

    private LineProperty lineProperty;


    private ObservableList<Double> polyLineList;

    private UpdateHandler updateHandler;

    public MyPolyline(DataController dataController,
                      Integer channelNumber,
                      Pane parent,
                      UpdateHandler updateHandler
    ) {
        this.updateHandler = updateHandler;
        this.dataController = dataController;
        this.channelNumber = channelNumber;
        load(parent);
        polyLineList = polyline.getPoints();
        polyline.setStrokeWidth(0.8d);

        layoutYProperty().bind(updateHandler.getBaseOffsetProperty().add(updateHandler.getLineSpacingProperty().multiply(updateHandler.getAmplitudeProperty()).multiply(channelNumber + 1)));

        DoubleProperty controlVboxWidthProperty = getControlVboxWidthProperty();
        DoubleProperty prefWidthProperty = updateHandler.getController().getGroup().prefWidthProperty();
        this.prefWidthProperty().bind(prefWidthProperty);
        Tooltip t = new Tooltip(dataController.getDataModel().getEeg_file().getHeader().getLabelsOfTheChannels().get(channelNumber));
        Tooltip.install(polyline, t);
        initLineProperty(controlVboxWidthProperty, prefWidthProperty);
        //lineInit();
        nameLabelInit(dataController, channelNumber);
//        setStyleClasses();
    }

    private void lineInit() {
        line.strokeProperty().bindBidirectional(lineProperty.getStrokeProperty());
        line.endXProperty().bind(lineProperty.getHorizontalResolution());
    }

    private void nameLabelInit(DataController dataController, int channelNumber) {
        if (nameLabel != null) nameLabel.textFillProperty().bind(lineProperty.getStrokeProperty());
        if (nameLabel != null) {
            nameLabel.setText(dataController.getDataModel().getEeg_file().getHeader().getLabelsOfTheChannels().get(channelNumber));
        }

    }

    private void initLineProperty(DoubleProperty controlVboxWidthProperty, DoubleProperty prefWidth) {
        lineProperty = new LineProperty(polyline.strokeProperty(), new SimpleDoubleProperty(0), null, new PolylineHorizontalResolutionProperty(dataController));
        lineProperty.getHorizontalResolution().bind(prefWidth.subtract(2));
        lineProperty.getAmplitude().bind(updateHandler.getAmplitudeProperty());
        lineProperty.getAmplitude().addListener(this::amplitudeListener);
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

    private SimpleDoubleProperty getControlVboxWidthProperty() {
        SimpleDoubleProperty controlVboxWidthProperty = new SimpleDoubleProperty();
        controlVbox.prefWidthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > controlVbox.getMaxWidth()) {
                controlVboxWidthProperty.setValue(controlVbox.getMaxWidth());
            } else if (newValue.doubleValue() < controlVbox.getMinWidth()) {
                controlVboxWidthProperty.setValue(controlVbox.getMinWidth());
            } else {
                controlVboxWidthProperty.setValue(newValue);
            }
        });
        return controlVboxWidthProperty;
    }

    public void buttonClick(ActionEvent actionEvent) {
        lineProperty = ColorPickerDialog.display(this.lineProperty);
        update();
    }

    private final List<String> styleClasses = new ArrayList<>(Arrays.asList(
            "group"
    ));
    private final List<String> polylineStyleClasses = new ArrayList<>(Arrays.asList());
    private final List<String> buttonStyleClasses = new ArrayList<>(Arrays.asList());

    private void setStyleClasses() {
        this.getStyleClass().addAll(styleClasses);
        polyline.getStyleClass().addAll(polylineStyleClasses);
        button.getStyleClass().addAll(buttonStyleClasses);
        //this.getStylesheets().add("/myPolyline.css");
    }

    public void update() {
        List<Double> updateList = new ArrayList<>();
        //System.out.println("xvectorSize: " + xVector.size());
        for (int i = 0; i < xVector.size(); i++) {
            updateList.add(xVector.get(i));
            updateList.add(-yVector.get(i) * lineProperty.getAmplitude().getValue());
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
        if (amplitudeLabel != null) amplitudeLabel.setText(newValue.toString());
        try {
//            setYPosition(UpdateHandler.get().getLineSpacing() * channelNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
