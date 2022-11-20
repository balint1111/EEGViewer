package root.main.common;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Properties {

    private final DoubleProperty lineSpacingProperty = new SimpleDoubleProperty(0);
    private final DoubleProperty baseOffsetProperty = new SimpleDoubleProperty(0);
    private final BooleanProperty zeroLineVisibleProperty = new SimpleBooleanProperty(false);
    private ObjectProperty<Color> timeVerticalLineColorProperty = new SimpleObjectProperty<>(Color.BLUE);
    private DoubleProperty timeVerticalLineWidthProperty = new SimpleDoubleProperty(1);
    private IntegerProperty timeResolutionProperty = new SimpleIntegerProperty(1);
    private IntegerProperty maxQueueSizeProperty = new SimpleIntegerProperty(1000);
}
