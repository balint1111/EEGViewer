package root.main;

import javafx.beans.property.SimpleDoubleProperty;
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

    private final SimpleDoubleProperty viewportHeightProperty = new SimpleDoubleProperty(0);
    private final SimpleDoubleProperty viewportWidthProperty = new SimpleDoubleProperty(0);

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


    public SimpleDoubleProperty viewportHeightPropertyProperty() {
        return viewportHeightProperty;
    }

    public void setViewportHeightProperty(double viewportHeightProperty) {
        this.viewportHeightProperty.set(viewportHeightProperty);
    }

    public SimpleDoubleProperty viewportWidthPropertyProperty() {
        return viewportWidthProperty;
    }

    public void setViewportWidthProperty(double viewportWidthProperty) {
        this.viewportWidthProperty.set(viewportWidthProperty);
    }
}
