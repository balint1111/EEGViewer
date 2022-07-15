package custom.component;

import custom.dialogs.ColorPickerDialog;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import lombok.Data;
import org.springframework.stereotype.Component;
import root.main.DataController;
import root.main.UpdateHandler;

import java.util.*;

@Data
@Component
public class MyPolyline extends HBox {

    private Polyline polyline = new Polyline();
    private Button button = new Button("hali");
    private final DataController dataController;
    private Line line = new Line();
    private Label label = new Label();

    private VBox vbox = new VBox();
    private VBox vbox2 = new VBox();

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

    public int getHorizontalResolution() {
        return horizontalResolution.get();
    }

    public IntegerProperty horizontalResolutionProperty() {
        return horizontalResolution;
    }

    private IntegerProperty horizontalResolution;
    private LineProperty lineProperty = new LineProperty();


    private ObservableList<Double> polyLineList;

    public MyPolyline(DataController dataController, int number, VBox parent) {
        this.dataController = dataController;
        this.number = number;



        horizontalResolution = new PolylineHorizontalResolutionProperty(300l, dataController);

        button.setOnAction(this::buttonClick);
        line.setStartX(0);
        line.setStartY(0);
        line.endXProperty().bind(vbox2.prefWidthProperty());
        line.setEndY(0);
        polyLineList = polyline.getPoints();


        //bindings
        lineProperty.setStrokeProperty(polyline.strokeProperty());
        //amplitudeChange -> update()
        lineProperty.getAmplitude().addListener((observable, oldValue, newValue) -> update());
        //label text bind to amplitude
        //label.textProperty().bind(lineProperty.getAmplitude().asString());
        lineProperty.getAmplitude().addListener((observable, oldValue, newValue) -> {
            label.setText(newValue.toString());
            try {
                setYPosition(UpdateHandler.get().getLineSpacing() * number);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        line.strokeProperty().bindBidirectional(lineProperty.getStrokeProperty());

        //add elements to anchorPane
        vbox2.getChildren().addAll(new Group(polyline, line));
        vbox2.setAlignment(Pos.CENTER_LEFT);
        vbox.prefWidthProperty().bind(this.prefWidthProperty().multiply(0.1d));
        vbox2.prefWidthProperty().bind(this.prefWidthProperty().subtract(vbox.prefWidthProperty()));
        horizontalResolution.bind(vbox2.prefWidthProperty());

        vbox2.setStyle("-fx-background-color: green;");
        this.setStyle("-fx-background-color: yellow;");
        //add elements to VBox
        vbox.getChildren().addAll(button, label);
        vbox.setAlignment(Pos.TOP_LEFT);
        //add elements to HBox (this)
        this.getChildren().addAll(vbox, vbox2);
        //Set Style Classes
        this.getStyleClass().addAll(styleClasses);
        polyline.getStyleClass().addAll(polylineStyleClasses);
        button.getStyleClass().addAll(buttonStyleClasses);
        this.getStylesheets().add("/myPolyline.css");
        prefWidthProperty().bind(parent.prefWidthProperty());
        parent.getChildren().add(this);
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
        polyLineList.setAll(updateList);
    }

    public void setYVector(List<Double> yVector) {
        this.yVector = yVector;
        xVector = new ArrayList<>();
        for (int i = 0; i < yVector.size(); i++) {
            xVector.add((double) i);
        }
        //horizontalResolution.setValue(xVector.size());
        try {
            setYPosition(UpdateHandler.get().getLineSpacing() * number);
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
        try {
            setYPosition(UpdateHandler.get().getLineSpacing() * number);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
