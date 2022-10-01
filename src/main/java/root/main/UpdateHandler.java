package root.main;

import custom.component.MyPolyline;
import custom.component.MyScrollBar;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Getter
public class UpdateHandler extends ScrollPane {

    @FXML
    public Pane group;


//    @FXML
//    public Pane background;

    private List<MyPolyline> myPolylineList = new ArrayList<>();

    public List<MyPolyline> getMyPolylineList() {
        return myPolylineList;
    }

    private final SimpleDoubleProperty viewportHeightProperty = new SimpleDoubleProperty(0);
    private final SimpleDoubleProperty viewportWidthProperty = new SimpleDoubleProperty(0);

    private Double lineSpacing = 0d;

    @Autowired
    private DataController dataController;

    private AutowireCapableBeanFactory autowireCapableBeanFactory;

    private ConfigurableApplicationContext applicationContext;

    @Autowired
    @Lazy
    private void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        load();
        initViewPortProperties();
        viewportWidthProperty.setValue(this.getViewportBounds().getMaxX() - this.getViewportBounds().getMinX());

    }

    @Autowired
    private MainController mainController;


    @SneakyThrows
    public UpdateHandler() {

    }

    private void load() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/UpdateHandler.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();


//            autowireCapableBeanFactory.autowireBean(myScrollBar);
//            background.prefWidthProperty().bind(viewportWidthProperty);
//            background.prefHeightProperty().bind(viewportHeightProperty);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void initViewPortProperties() {

        this.viewportBoundsProperty().addListener((observableValue, bounds, t1) -> {
            int newHeight = (int) (t1.getMaxY() - t1.getMinY());
            if (newHeight != viewportHeightProperty.get()) {
                viewportHeightProperty.setValue(newHeight);
            }
        });
        viewportHeightProperty.setValue(this.getViewportBounds().getMaxY() - this.getViewportBounds().getMinY());

        this.viewportBoundsProperty().addListener((observableValue, bounds, t1) -> {
            int newWidth = (int) (t1.getMaxX() - t1.getMinX());
            if (newWidth != viewportWidthProperty.get()) {
                viewportWidthProperty.setValue(newWidth);
            }
        });
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
        this.lineSpacing = spacing;
        synchronized (myPolylineList) {
            for (int i = 0; i < myPolylineList.size(); i++) {
                myPolylineList.get(i).setYPosition((i + 1) * spacing);
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
                myPolyline.setAmplitude(amplitude);
            }
        }
    }

    public Double getLineSpacing() {
        return lineSpacing;
    }

    public void onChangeSelectedChannels(ListChangeListener.Change<? extends Integer> c) {
        group.getChildren().removeIf(node -> myPolylineList.stream().anyMatch(myPolyline -> myPolyline.equals(node)));
        synchronized (myPolylineList) {
            myPolylineList.clear();
            myPolylineList.clear();
            for (int i = 0; i < dataController.getSelectedChannels().size(); i++) {
                MyPolyline myPolyline = new MyPolyline(dataController, c.getList().get(i), group, this);
                myPolylineList.add(myPolyline);
            }
            setLineSpacing(lineSpacing);
        }
    }

    public final SimpleDoubleProperty getViewportWidthProperty() {
        return viewportWidthProperty;
    }

    public final SimpleDoubleProperty getViewportHeightProperty() {
        return viewportHeightProperty;
    }

    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }
}
