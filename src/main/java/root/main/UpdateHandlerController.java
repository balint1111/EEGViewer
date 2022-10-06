package root.main;

import custom.component.Modes;
import custom.component.MyPolyline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


@Getter
@Controller
public class UpdateHandlerController implements Initializable {

    @FXML
    private Pane group;

    @FXML
    private Pane labels;

    @FXML
    private StackPane stack;

    @FXML
    private UpdateHandler updateHandler;

    private final Properties properties;


    private final ObservableList<Integer> selectedChannels = FXCollections.observableArrayList();
    private final DoubleProperty viewportHeightProperty = new SimpleDoubleProperty(0d);
    private final DoubleProperty viewportWidthProperty = new SimpleDoubleProperty(0d);
    private final DoubleProperty lineSpacingProperty = new SimpleDoubleProperty(0);
    private final DoubleProperty baseOffsetProperty = new SimpleDoubleProperty(0);
    private final DoubleProperty amplitudeProperty = new SimpleDoubleProperty(1);

    private final General general;

    private ObjectProperty<Modes> modeProperty = new SimpleObjectProperty<>(Modes.NORMAL);

    public UpdateHandlerController(Properties properties, DataController dataController, General general){
        this.properties = properties;
        this.general = general;
        System.out.println("UpdateHandlerController");
        this.dataController = dataController;
    }

    private ObservableList<MyPolyline> myPolylineList = FXCollections.observableArrayList();

    private Double lineSpacing = 0d;

    private final DataController dataController;



    public void setYVectors(List<Double>[] yVectors) {
        synchronized (myPolylineList) {
            for (int i = 0; i < myPolylineList.size(); i++) {
                MyPolyline myPolyline = myPolylineList.get(i);
                myPolyline.setYVector(yVectors[myPolyline.getChannelNumber()]);
            }
        }
    }

    public void update() {
        synchronized (myPolylineList) {
            for (MyPolyline myPolyline : myPolylineList) {
                myPolyline.update();
            }
        }
    }

    public void setLineSpacing(Double spacing) {
        synchronized (myPolylineList) {
            for (int i = 0; i < myPolylineList.size(); i++) {
                myPolylineList.get(i).layoutYProperty().set((i + 1) * spacing);
            }
        }
    }

    public void setColors(List<Color> colors) {
        synchronized (myPolylineList) {
            for (int i = 0; i < myPolylineList.size(); i++) {
                Color color = colors.get(i % colors.size());
                if (color != null)
                    myPolylineList.get(i).getLineProperty().getStrokeProperty().setValue(color);
            }
        }
    }

    public void setAmplitudes(Double amplitude) {
        synchronized (myPolylineList) {
            for (MyPolyline myPolyline : myPolylineList) {
                myPolyline.getLineProperty().getAmplitude().set(amplitude);
            }
        }
    }

    public void onChangeSelectedChannels(ListChangeListener.Change<? extends Integer> c) {
        group.getChildren().clear();
        synchronized (myPolylineList) {
            myPolylineList.clear();
            for (int i = 0; i < selectedChannels.size(); i++) {
                MyPolyline myPolyline = new MyPolyline(dataController, c.getList().get(i), group, updateHandler);
                Label label = new Label(updateHandler.getController().getDataController().getDataModel().getEeg_file().getHeader().getLabelsOfTheChannels(c.getList().get(i)));
                label.layoutYProperty().bind(updateHandler.getBaseOffsetProperty().add(updateHandler.getLineSpacingProperty().multiply(c.getList().get(i) + 1)).subtract(label.heightProperty().divide(2)));
                labels.getChildren().add(label);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateHandler.addEventFilter(ScrollEvent.SCROLL, this::shiftDownFilter);
        stack.prefWidthProperty().bind(viewportWidthProperty.subtract(labels.prefWidthProperty()));
        labels.minHeightProperty().bind(viewportHeightProperty);
    }

//    private void scroll(ScrollEvent event) {
//        double newValue = general.getScrollBarValue().get() - event.getDeltaY();
//        double minValue = general.getScrollBarValue().minProperty().get();
//        double maxValue = general.getScrollBarValue().maxProperty().get();
//        if (minValue < newValue && newValue < maxValue) {
//
//            general.getScrollBarValue().setValue(newValue);
//            System.out.println("set: "+ myScrollBar.valueProperty().get());
//        } else if (minValue > newValue) {
//            myScrollBar.valueProperty().setValue(minValue);
//        } else {
//            myScrollBar.valueProperty().setValue(maxValue);
//        }
//    }


    private void shiftDownFilter(ScrollEvent event) {
        if (event.isShiftDown()) {
            general.getScrollBarValue().setValue(general.getScrollBarValue().get() - event.getDeltaY());
//            scroll(event);
            event.consume();
        }
    }
}
