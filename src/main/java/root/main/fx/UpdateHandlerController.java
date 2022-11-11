package root.main.fx;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import root.main.DataController;
import root.main.General;
import root.main.common.Properties;
import root.main.common.enums.Modes;
import root.main.fx.custom.*;
import root.main.fx.custom.builders.PositionPropertyBuilder;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Supplier;


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
    private final DoubleProperty lineSpacingProperty = new SimpleDoubleProperty(70);
    private final DoubleProperty baseOffsetProperty = new SimpleDoubleProperty(0);
    private final DoubleProperty amplitudeProperty = new SimpleDoubleProperty(1);
    private final BooleanProperty playProperty = new SimpleBooleanProperty(false);
    private ObjectProperty<MyLine> cursorProperty = new SimpleObjectProperty<>(null);

    private final General general;

    private ObjectProperty<Modes> modeProperty = new SimpleObjectProperty<>(Modes.NORMAL);

    private ObservableList<MyPolyline> myPolylineList = FXCollections.observableArrayList();
    private final ObservableList<MyLine> myLines = FXCollections.observableArrayList();

    private final DataController dataController;
    private final PositionPropertyBuilder positionPropertyBuilder;

    public UpdateHandlerController(Properties properties, DataController dataController, General general, PositionPropertyBuilder positionPropertyBuilder) {
        this.properties = properties;
        this.general = general;
        this.dataController = dataController;
        this.positionPropertyBuilder = positionPropertyBuilder;
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

    public void setColors(List<Color> colors) {
        synchronized (myPolylineList) {
            for (int i = 0; i < myPolylineList.size(); i++) {
                Color color = colors.get(i % colors.size());
                if (color != null)
                    myPolylineList.get(i).getMyPolyLineProperty().getStrokeProperty().setValue(color);
            }
        }
    }

    public void onChangeSelectedChannels(ListChangeListener.Change<? extends Integer> c) {
        group.getChildren().clear();
        synchronized (myPolylineList) {
            myPolylineList.clear();
            labels.getChildren().clear();
            for (int i = 0; i < selectedChannels.size(); i++) {
                MyPolyline myPolyline = new MyPolyline(i , group, updateHandler);
                Label label = new Label(updateHandler.getController().getDataController().getDataModel().getEeg_file().getHeader().getLabelsOfTheChannels(c.getList().get(i)));
                label.layoutYProperty().bind(myPolyline.layoutYProperty().subtract(label.heightProperty().divide(2)));
                label.visibleProperty().bind(modeProperty.isNotEqualTo(Modes.BUTTERFLY));
                labels.getChildren().add(label);
            }
        }
        cursorProperty.get().getLabel().setVisible(true);
        positionChange.changed(null, null, null);
    }

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
                        horizontalResolution.divide(general.getPageSizeProperty().multiply(general.getNumberOfSamplesProperty())), general.getScrollBarValue().getPosition(),
                        general.getPageEndPosition(), general.getNumberOfSamplesProperty(), new SimpleLongProperty(i).multiply(general.getDurationOfDataRecordProperty()),
                        new ReadOnlyObjectWrapper<Paint>(Color.BLUE));
            }
        });
        playProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(true)) {
                loop((targetFps) -> {
                    int samplePerFrame = general.getNumberOfSamplesProperty().get() / targetFps / (int) general.getSlowDownProperty().get();
                    int overShoot = (general.getScrollBarValue().getPosition().getOffsetProperty().get() + samplePerFrame) - ((MinMaxIntegerProperty) general.getScrollBarValue().getPosition().getOffsetProperty()).getMax().get();
                    if (overShoot >= 0) {
                        general.getScrollBarValue().getPosition().getOffsetProperty().set(overShoot);
                        general.getScrollBarValue().getPosition().getRecordProperty().set(general.getScrollBarValue().getPosition().getRecordProperty().get() + 1);
                    } else {
                        general.getScrollBarValue().getPosition().getOffsetProperty().set(general.getScrollBarValue().getPosition().getOffsetProperty().get() + samplePerFrame);
                    }
                }, playProperty::get, () -> general.getFpsProperty().get());
            }
        });

        Position position = general.getScrollBarValue().getPosition();
        cursorMsBinding = position.getRecordProperty().multiply(general.getDurationOfDataRecordProperty()).add(position.getOffsetProperty().multiply(general.getDurationOfDataRecordProperty().divide(general.getNumberOfSamplesProperty())));
        cursorProperty.set(new MyLine(general.getScrollBarValue(), updateHandler.viewportHeightProperty(), labels.prefWidthProperty(), backgroundLayer, timeLine,
                horizontalResolution.divide(general.getPageSizeProperty().multiply(general.getNumberOfSamplesProperty())), general.getScrollBarValue().getPosition(), general.getPageEndPosition(), general.getNumberOfSamplesProperty(),
                cursorMsBinding,
                new ReadOnlyObjectWrapper<Paint>(Color.ORANGE)));
        cursorProperty.get().getLabel().setVisible(false);
        cursorProperty.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.getPositionProperty().getPosition().getRecordProperty().removeListener(positionChange);
                oldValue.getPositionProperty().getPosition().getOffsetProperty().removeListener(positionChange);
            }
            newValue.getPositionProperty().getPosition().getRecordProperty().addListener(positionChange);
            newValue.getPositionProperty().getPosition().getOffsetProperty().addListener(positionChange);
            positionChange.changed(null, null, null);
        });
        cursorProperty.get().getPositionProperty().getPosition().getRecordProperty().addListener(positionChange);
        cursorProperty.get().getPositionProperty().getPosition().getOffsetProperty().addListener(positionChange);
    }

    public void playToggle() {
        if (playProperty.get())
            Platform.runLater(() -> playProperty.set(false));
        else
            Platform.runLater(() -> playProperty.set(true));
    }

    public void butterflyToggle() {
        if (modeProperty.get().equals(Modes.BUTTERFLY))
            Platform.runLater(() -> modeProperty.set(Modes.NORMAL));
        else if (modeProperty.get().equals(Modes.NORMAL))
            Platform.runLater(() -> modeProperty.set(Modes.BUTTERFLY));
    }

    private NumberBinding cursorMsBinding;

    public void onClick(MouseEvent event) {
        double xPosition = event.getSceneX() - labels.getWidth();
        DoubleBinding pixelPerSample = horizontalResolution.divide(general.getPageSizeProperty().multiply(general.getNumberOfSamplesProperty()));
        int sampleOffset = (int) (xPosition / pixelPerSample.get());
        if (cursorProperty.get() != null) cursorProperty.get().remove();
        Position position = positionPropertyBuilder.relative(general.getScrollBarValue().getPosition(), new Position(new ReadOnlyIntegerWrapper(sampleOffset / general.getNumberOfSamplesProperty().get()), new ReadOnlyIntegerWrapper(sampleOffset % general.getNumberOfSamplesProperty().get())));
        cursorMsBinding = position.getRecordProperty().multiply(general.getDurationOfDataRecordProperty()).add(position.getOffsetProperty().multiply(general.getDurationOfDataRecordProperty().divide(general.getNumberOfSamplesProperty())));
        cursorProperty.set(new MyLine(new PositionProperty(position), updateHandler.viewportHeightProperty(), labels.prefWidthProperty(), backgroundLayer, timeLine,
                horizontalResolution.divide(general.getPageSizeProperty().multiply(general.getNumberOfSamplesProperty())), general.getScrollBarValue().getPosition(), general.getPageEndPosition(), general.getNumberOfSamplesProperty(),
                cursorMsBinding,
                new ReadOnlyObjectWrapper<>(Color.ORANGE)));
    }


    @Autowired
    private void autowire(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;

    }

    private void shiftDownFilter(ScrollEvent event) {
        if (event.isShiftDown()) {
            general.getScrollBarValue().getPosition().getRecordProperty().setValue(general.getScrollBarValue().getPosition().getRecordProperty().get() - event.getDeltaY());
            event.consume();
        }
    }

    public void loop(Consumer<Integer> consumer, Supplier<Boolean> supplier, Supplier<Integer> targetFps) {
        Thread loopThread = new Thread(() -> {
            long now;
            long updateTime;
            long wait;

            while (supplier.get()) {
                final int TARGET_FPS = targetFps.get();
                final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

                now = System.nanoTime();

                Platform.runLater(() -> consumer.accept(TARGET_FPS));

                updateTime = System.nanoTime() - now;
                wait = (OPTIMAL_TIME - updateTime) / 1000000;

                try {
                    Thread.sleep(wait);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        loopThread.start();
    }

    private ChangeListener<Number> positionChange = this::positionChange;

    private void positionChange(ObservableValue<? extends Number> observable1, Number oldValue1, Number newValue1) {
        general.getCurrentValuesProperty().set(general.getDataController().getDataModel().getDataAtPosition(cursorProperty.get().getPositionProperty().get().getNormalizedPosition(general.getNumberOfSamplesProperty().get())));
    }
}
