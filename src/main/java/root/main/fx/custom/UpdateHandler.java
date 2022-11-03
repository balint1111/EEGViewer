package root.main.fx.custom;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import root.main.common.enums.Modes;
import root.main.fx.UpdateHandlerController;
import root.main.fx.custom.MyPolyline;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import root.main.common.Properties;

import java.io.IOException;


@Getter
@Setter
public class UpdateHandler extends VBox {
    @FXML
    public Pane timeline;
    private UpdateHandlerController controller;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Pane backgroundLayer;

    private final SimpleDoubleProperty horizontalResolution = new SimpleDoubleProperty(0d);

    private final DoubleProperty viewportHeightProperty = new SimpleDoubleProperty(0d);
    private final DoubleProperty viewportWidthProperty = new SimpleDoubleProperty(0d);
    private final DoubleProperty lineSpacingProperty = new SimpleDoubleProperty(70);
    private final DoubleProperty baseOffsetProperty = new SimpleDoubleProperty(0);
    private final DoubleProperty amplitudeProperty = new SimpleDoubleProperty(1);
    private final BooleanProperty playProperty = new SimpleBooleanProperty(false);
    private final ObjectProperty<Modes> modeProperty = new SimpleObjectProperty<>(Modes.NORMAL);
    private ObservableList<Integer> selectedChannels;

    private ObservableList<MyPolyline> myPolylineList;

    private Properties props;

    public UpdateHandler() {
        super();
        System.out.println("update Handler");
    }

    @Autowired
    private void setProps(Properties props){
        this.props = props;
        initProps();
    }

    @Autowired
    private void asd(UpdateHandlerController updateHandlerController){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/UpdateHandler.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(updateHandlerController);
        controller = updateHandlerController;
        try {
            fxmlLoader.load();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void init() {
        myPolylineList = controller.getMyPolylineList();
        selectedChannels = controller.getSelectedChannels();
        scrollPane = controller.getScrollPane();
        initViewPortProperties();

        controller.getHorizontalResolution().bindBidirectional(horizontalResolution);
        horizontalResolution.bind(controller.getGroup().prefWidthProperty().subtract(2));
        controller.getLineSpacingProperty().bindBidirectional(lineSpacingProperty);
        controller.getViewportWidthProperty().bindBidirectional(viewportWidthProperty);
        controller.getViewportHeightProperty().bindBidirectional(viewportHeightProperty);
        controller.getModeProperty().bindBidirectional(modeProperty);
        controller.getAmplitudeProperty().bindBidirectional(amplitudeProperty);
        controller.getPlayProperty().bindBidirectional(playProperty);
        controller.getBaseOffsetProperty().bindBidirectional(baseOffsetProperty);

        selectedChannels.addListener(controller::onChangeSelectedChannels);

        modeProperty.addListener(this::changeMode);

    }


    private void initProps() {
        props.getBaseOffsetProperty().addListener((observable, oldValue, newValue) -> baseOffsetProperty.set(newValue.doubleValue()));
        props.getLineSpacingProperty().addListener((observable, oldValue, newValue) -> lineSpacingProperty.set(newValue.doubleValue()));
    }


    private void changeMode(ObservableValue<? extends Modes> observable, Modes oldValue, Modes newValue) {
        if (newValue.equals(Modes.BUTTERFLY)) {
            lineSpacingProperty.set(0);
            baseOffsetProperty.set(viewportHeightProperty.divide(2).get());
        } else if (newValue.equals(Modes.NORMAL)) {
            lineSpacingProperty.set(props.getLineSpacingProperty().get());
            baseOffsetProperty.set(props.getBaseOffsetProperty().get());
        }
    }

    public void initViewPortProperties() {

        scrollPane.viewportBoundsProperty().addListener((observableValue, bounds, t1) -> {
            int newHeight = (int) (t1.getMaxY() - t1.getMinY());
            if (newHeight != viewportHeightProperty.get()) {
                viewportHeightProperty.setValue(newHeight);
            }
        });
        viewportHeightProperty.setValue(scrollPane.getViewportBounds().getMaxY() - scrollPane.getViewportBounds().getMinY());

        scrollPane.viewportBoundsProperty().addListener((observableValue, bounds, t1) -> {
            int newWidth = (int) (t1.getMaxX() - t1.getMinX());
            if (newWidth != viewportWidthProperty.get()) {
                viewportWidthProperty.setValue(newWidth);
            }
        });
        viewportWidthProperty.setValue(scrollPane.getViewportBounds().getMaxX() - scrollPane.getViewportBounds().getMinX());
    }


    public DoubleProperty viewportHeightProperty() {
        return viewportHeightProperty;
    }

    public void setViewportHeightProperty(double viewportHeightProperty) {
        this.viewportHeightProperty.set(viewportHeightProperty);
    }

    public DoubleProperty viewportWidthProperty() {
        return viewportWidthProperty;
    }

    public final double getViewportWidthPropertyValue() {
        return viewportWidthProperty.get();
    }

    public void setViewportWidthProperty(double viewportWidthProperty) {
        this.viewportWidthProperty.set(viewportWidthProperty);
    }
}
