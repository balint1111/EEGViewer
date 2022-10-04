package root.main;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.IOException;


@Getter
@Setter
public class UpdateHandler extends ScrollPane {
    private UpdateHandlerController controller;

    private final DoubleProperty viewportHeightProperty = new SimpleDoubleProperty(0d);
    private final DoubleProperty viewportWidthProperty = new SimpleDoubleProperty(0d);
    private final DoubleProperty lineSpacingProperty = new SimpleDoubleProperty(0);

    @SneakyThrows
    public UpdateHandler() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/UpdateHandler.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setControllerFactory(param -> controller = new UpdateHandlerController());
        try {
           fxmlLoader.load();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        init();
    }

    public void init() {
        initViewPortProperties();
        viewportWidthProperty.setValue(getViewportBounds().getMaxX() - getViewportBounds().getMinX());
    }

    private void initViewPortProperties() {

        viewportBoundsProperty().addListener((observableValue, bounds, t1) -> {
            int newHeight = (int) (t1.getMaxY() - t1.getMinY());
            if (newHeight != viewportHeightProperty.get()) {
                viewportHeightProperty.setValue(newHeight);
            }
        });
        viewportHeightProperty.setValue(getViewportBounds().getMaxY() - getViewportBounds().getMinY());

        viewportBoundsProperty().addListener((observableValue, bounds, t1) -> {
            int newWidth = (int) (t1.getMaxX() - t1.getMinX());
            if (newWidth != viewportWidthProperty.get()) {
                viewportWidthProperty.setValue(newWidth);
            }
        });
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
