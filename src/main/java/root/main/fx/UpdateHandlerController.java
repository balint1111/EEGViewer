package root.main.fx;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.control.ScrollPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import root.main.DataController;
import root.main.General;
import root.main.common.enums.Modes;
import root.main.fx.custom.*;
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
import root.main.common.Properties;
import root.main.fx.custom.builders.PositionPropertyBuilder;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


@Getter
@Controller
public class UpdateHandlerController implements Initializable {

    private ConfigurableApplicationContext applicationContext;

    @FXML
    private Pane group;

    @FXML
    private Pane timeLine;

    @FXML
    private Pane labels;

    @FXML
    private StackPane stack;

    @FXML
    private UpdateHandler updateHandler;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    public MyScrollBar myScrollBar;

    @FXML
    private Pane backgroundLayer;

    private final Properties properties;

    private final SimpleDoubleProperty horizontalResolution = new SimpleDoubleProperty(0d);
    private final DoubleProperty pixelPerMilliSecond = new SimpleDoubleProperty();


    private final ObservableList<Integer> selectedChannels = FXCollections.observableArrayList();
    private final DoubleProperty viewportHeightProperty = new SimpleDoubleProperty(0d);
    private final DoubleProperty viewportWidthProperty = new SimpleDoubleProperty(0d);
    private final DoubleProperty lineSpacingProperty = new SimpleDoubleProperty(0);
    private final DoubleProperty baseOffsetProperty = new SimpleDoubleProperty(0);
    private final DoubleProperty amplitudeProperty = new SimpleDoubleProperty(1);

    private final General general;

    private ObjectProperty<Modes> modeProperty = new SimpleObjectProperty<>(Modes.NORMAL);

    private ObservableList<MyPolyline> myPolylineList = FXCollections.observableArrayList();
    private final ObservableList<MyLine> myLines = FXCollections.observableArrayList();

    private Double lineSpacing = 0d;

    private final DataController dataController;
    private final PositionPropertyBuilder positionPropertyBuilder;

    public UpdateHandlerController(Properties properties, DataController dataController, General general, PositionPropertyBuilder positionPropertyBuilder) {
        this.properties = properties;
        this.general = general;
        this.dataController = dataController;
        this.positionPropertyBuilder = positionPropertyBuilder;
        System.out.println("UpdateHandlerController");
    }

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
            labels.getChildren().clear();
            for (int i = 0; i < selectedChannels.size(); i++) {
                MyPolyline myPolyline = new MyPolyline(dataController, c.getList().get(i), group, updateHandler);
                Label label = new Label(updateHandler.getController().getDataController().getDataModel().getEeg_file().getHeader().getLabelsOfTheChannels(c.getList().get(i)));
                label.layoutYProperty().bind(updateHandler.getBaseOffsetProperty().add(updateHandler.getLineSpacingProperty().multiply(c.getList().get(i) + 1)).subtract(label.heightProperty().divide(2)));
                labels.getChildren().add(label);
            }
        }
    }

    Position relative;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateHandler.init();
        pixelPerMilliSecond.bind(horizontalResolution.divide(general.getDurationOfDataRecordProperty().multiply(general.getNumberOfDataRecordsProperty())));
        updateHandler.addEventFilter(ScrollEvent.SCROLL, this::shiftDownFilter);
        stack.prefWidthProperty().bind(viewportWidthProperty.subtract(labels.prefWidthProperty()));
        labels.minHeightProperty().bind(viewportHeightProperty);
        horizontalResolution.addListener((observableValue, oldValue, newValue) -> {
            dataController.showDataRecord();
        });
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        autowireCapableBeanFactory.autowireBean(myScrollBar);
        applicationContext.getBeanFactory().registerSingleton(myScrollBar.getClass().getCanonicalName(), myScrollBar);

        general.getPageEndPosition().getRecordProperty().addListener((observable, oldValue, newValue) -> {
            for (int i = general.getScrollBarValue().get().getRecordProperty().get(); i <= general.getPageEndPosition().getRecordProperty().get(); i++) {
                new MyLine(positionPropertyBuilder.build(new Position(i, 0)), updateHandler.viewportHeightProperty(), labels.prefWidthProperty(), backgroundLayer, timeLine,
                        horizontalResolution.divide(general.getPageSizeProperty().multiply(general.getNumberOfSamplesProperty())), general.getScrollBarValue().getPosition(), general.getPageEndPosition(), general.getNumberOfSamplesProperty(), i * 1000l);
            }
        });
    }

    @Autowired
    private void autowire(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;

    }

    private void shiftDownFilter(ScrollEvent event) {
        if (event.isShiftDown()) {
            general.getScrollBarValue().getPosition().getRecordProperty().setValue(general.getScrollBarValue().getPosition().getRecordProperty().get() - event.getDeltaY());
//            scroll(event);
            event.consume();
        }
    }
}
