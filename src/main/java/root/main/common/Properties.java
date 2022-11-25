package root.main.common;

import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Properties {

    private final DoubleProperty lineSpacingProperty = new SimpleDoubleProperty(0);
    private final DoubleProperty baseOffsetProperty = new SimpleDoubleProperty(0);
    private final BooleanProperty zeroLineVisibleProperty = new SimpleBooleanProperty(false);
    private DoubleProperty polylineStrokeWidthProperty = new SimpleDoubleProperty(0.8);
    private ObjectProperty<Color> polyLineColorProperty = new SimpleObjectProperty<>(Color.BLACK);
    private ObjectProperty<Color> timeVerticalLineColorProperty = new SimpleObjectProperty<>(Color.BLUE);
    private ObjectProperty<Background> backgroundProperty = new SimpleObjectProperty<>(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
    private DoubleProperty timeStrokeWidthProperty = new SimpleDoubleProperty(1);
    private IntegerProperty timeResolutionProperty = new SimpleIntegerProperty(1);
    private IntegerProperty maxQueueSizeProperty = new SimpleIntegerProperty(1000);
}
