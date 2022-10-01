package custom.component;

import custom.dialogs.ColorPickerDialog;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;
import root.main.DataController;
import root.main.UpdateHandler;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Getter
public class MyPolyline extends HBox implements Initializable {

    @FXML
    private Polyline polyline;

    @FXML
    private Line line;

    @FXML
    private VBox controlVbox;

//    @FXML
//    private VBox lineVbox;

    @FXML
    private Label amplitudeLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Button button;

    private DataController dataController;

    private final List<String> styleClasses = new ArrayList<>(Arrays.asList(
            "group"
    ));
    private final List<String> polylineStyleClasses = new ArrayList<>(Arrays.asList(
    ));
    private final List<String> buttonStyleClasses = new ArrayList<>(Arrays.asList(
    ));

    private Integer channelNumber;

    private List<Double> xVector = new ArrayList<>();

    private List<Double> yVector = new ArrayList<>();

    private LineProperty lineProperty;


    private ObservableList<Double> polyLineList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    public MyPolyline(DataController dataController, int channelNumber, Pane parent, UpdateHandler sc) {
        this.dataController = dataController;
        this.channelNumber = channelNumber;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/MyPolyline.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.setId("myPolyline" + channelNumber);
        parent.getChildren().add(this);

        polyline.setStrokeWidth(3);

        SimpleDoubleProperty controlVboxWidthProperty = getControlVboxWidthProperty();
        SimpleDoubleProperty viewportWidthProperty = sc.getViewportWidthProperty();
        this.prefWidthProperty().bind(viewportWidthProperty);
        lineProperty = new LineProperty(polyline.strokeProperty(), new SimpleDoubleProperty(0), null, new PolylineHorizontalResolutionProperty(dataController));
        lineProperty.getHorizontalResolution().bind(viewportWidthProperty.subtract(controlVboxWidthProperty).subtract(2));
        lineProperty.getAmplitude().addListener((observable, oldValue, newValue) -> {
            update();
            if (amplitudeLabel != null) amplitudeLabel.setText(newValue.toString());
            try {
                //setYPosition(UpdateHandler.get().getLineSpacing() * channelNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        line.strokeProperty().bindBidirectional(lineProperty.getStrokeProperty());

        line.endXProperty().bind(lineProperty.getHorizontalResolution());

        polyLineList = polyline.getPoints();

        //nameLabel
        if (nameLabel != null) nameLabel.textFillProperty().bind(lineProperty.getStrokeProperty());
        if (nameLabel != null) nameLabel.setText(dataController.getDataModel().getEeg_file().getHeader().getLabelsOfTheChannels().get(channelNumber));


        Tooltip t = new Tooltip(dataController.getDataModel().getEeg_file().getHeader().getLabelsOfTheChannels().get(channelNumber));
        Tooltip.install(polyline, t);


        //amplitudeChange -> update()
        //label text bind to amplitude
        //label.textProperty().bind(lineProperty.getAmplitude().asString());

//
//        this.getStyleClass().addAll(styleClasses);
//        polyline.getStyleClass().addAll(polylineStyleClasses);
        //button.getStyleClass().addAll(buttonStyleClasses);
        //this.getStylesheets().add("/myPolyline.css");

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
        this.yVector = yVector;
        xVector = new ArrayList<>();
        for (int i = 0; i < yVector.size(); i++) {
            xVector.add((double) i);
        }
    }

    public void setYPosition(Double yPosition) {
//        Optional<Double> yMax = yVector.stream().map(aDouble -> -aDouble * lineProperty.getAmplitude().getValue()).max(Double::compareTo);
        this.setLayoutY(600 + yPosition);
    }

    public void setColor(Color color) {
        lineProperty.getStrokeProperty().setValue(color);
    }

    public void setAmplitude(Double amplitude) {
        lineProperty.getAmplitude().setValue(amplitude);
        //setYPosition(UpdateHandler.get().getLineSpacing() * number);

    }


}
