package custom.component;

import custom.dialogs.ColorPickerDialog;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import lombok.Data;
import org.springframework.stereotype.Component;
import root.main.DataController;
import root.main.UpdateHandler;

import java.io.IOException;
import java.net.URL;
import java.util.*;

@Data
@Component
public class MyPolyline extends HBox implements Initializable {

    @FXML
    private Polyline polyline;

    @FXML
    private Line line;

    @FXML
    private VBox controlVbox;

    @FXML
    private VBox lineVbox;

    @FXML
    private Label amplitudeLabel;

    @FXML
    private Button button;

    private final DataController dataController;

    private final List<String> styleClasses = new ArrayList<>(Arrays.asList(
            "group"
    ));
    private final List<String> polylineStyleClasses = new ArrayList<>(Arrays.asList(
    ));
    private final List<String> buttonStyleClasses = new ArrayList<>(Arrays.asList(
    ));

    private int number;

    private List<Double> xVector = new ArrayList<>();

    private List<Double> yVector = new ArrayList<>();

    private IntegerProperty horizontalResolution;
    private LineProperty lineProperty = new LineProperty();


    private ObservableList<Double> polyLineList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    public MyPolyline(DataController dataController, int number, VBox parent, ScrollPane sc) {
        this.dataController = dataController;
        this.number = number;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/MyPolyline.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.prefWidthProperty().bind(parent.prefWidthProperty());
        this.setId("myPolyline" + number);
        parent.getChildren().add(this);


        horizontalResolution = new PolylineHorizontalResolutionProperty(750l, dataController);
        SimpleIntegerProperty viewportWidth = new SimpleIntegerProperty();
        sc.viewportBoundsProperty().addListener((observableValue, bounds, t1) -> {
            int newWidth = (int)(t1.getMaxX() - t1.getMinX());
            if (newWidth != viewportWidth.get()) {
                viewportWidth.setValue(newWidth);
                System.out.println("viewportWidth: " + viewportWidth.get());
            }
        });
        viewportWidth.setValue(sc.getViewportBounds().getMaxX() - sc.getViewportBounds().getMinX());
        horizontalResolution.bind(viewportWidth.subtract(controlVbox.widthProperty()).subtract(2));

        polyLineList = polyline.getPoints();


        lineProperty.setStrokeProperty(polyline.strokeProperty());
        lineProperty.getAmplitude().addListener((observable, oldValue, newValue) -> {
            update();
            amplitudeLabel.setText(newValue.toString());
            try {
                //setYPosition(UpdateHandler.get().getLineSpacing() * number);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        line.strokeProperty().bindBidirectional(lineProperty.getStrokeProperty());


        //amplitudeChange -> update()
        //label text bind to amplitude
        //label.textProperty().bind(lineProperty.getAmplitude().asString());

//
//        this.getStyleClass().addAll(styleClasses);
//        polyline.getStyleClass().addAll(polylineStyleClasses);
        //button.getStyleClass().addAll(buttonStyleClasses);
        //this.getStylesheets().add("/myPolyline.css");

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
        this.yVector = yVector;
        xVector = new ArrayList<>();
        for (int i = 0; i < yVector.size(); i++) {
            xVector.add((double) i);
        }
        //horizontalResolution.setValue(xVector.size());
        try {
            //setYPosition(UpdateHandler.get().getLineSpacing() * number);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setYPosition(Double yPosition) {
        Optional<Double> yMax = yVector.stream().map(aDouble -> -aDouble * lineProperty.getAmplitude().getValue()).max(Double::compareTo);
        this.setLayoutY(yPosition - ((yMax.orElse(0d) / 2d)));
    }

    public void setColor(Color color) {
        lineProperty.getStrokeProperty().setValue(color);
    }

    public void setAmplitude(Double amplitude) {
        lineProperty.getAmplitude().setValue(amplitude);
        //setYPosition(UpdateHandler.get().getLineSpacing() * number);

    }


}
