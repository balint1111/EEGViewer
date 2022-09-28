package root.main;

import custom.component.MyPolyline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class UpdateHandler extends ScrollPane {

//    @FXML
//    public VBox vBox;

    @FXML
    public Pane group;

    private List<MyPolyline> myPolylineList = new ArrayList<>();

    public List<MyPolyline> getMyPolylineList() {
        return myPolylineList;
    }

    private Double lineSpacing = 0d;

    @Autowired
    private DataController dataController;


    @SneakyThrows
    public UpdateHandler() {
        load();
    }

    private void load() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Chart.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
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
        load();
        synchronized (myPolylineList) {
            myPolylineList.clear();
            for (int i = 0; i < dataController.getSelectedChannels().size(); i++) {
                MyPolyline myPolyline = new MyPolyline(dataController, c.getList().get(i), group, this);
                myPolylineList.add(myPolyline);
            }
            setLineSpacing(lineSpacing);
        }
    }

    public final SimpleDoubleProperty getViewportWidthProperty() {
        SimpleDoubleProperty viewportWidth = new SimpleDoubleProperty();
        this.viewportBoundsProperty().addListener((observableValue, bounds, t1) -> {
            int newWidth = (int)(t1.getMaxX() - t1.getMinX());
            if (newWidth != viewportWidth.get()) {
                viewportWidth.setValue(newWidth);
            }
        });
        viewportWidth.setValue(this.getViewportBounds().getMaxX() - this.getViewportBounds().getMinX());
        return viewportWidth;
    }
    public final SimpleDoubleProperty getViewportHeightProperty() {
        SimpleDoubleProperty viewportHeight = new SimpleDoubleProperty();
        this.viewportBoundsProperty().addListener((observableValue, bounds, t1) -> {
            int newHeiht = (int)(t1.getMaxY() - t1.getMinY());
            if (newHeiht != viewportHeight.get()) {
                viewportHeight.setValue(newHeiht);
            }
        });
        viewportHeight.setValue(this.getViewportBounds().getMaxY() - this.getViewportBounds().getMinY());
        return viewportHeight;
    }
}
