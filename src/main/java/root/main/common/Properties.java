package root.main.common;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Properties {

    private final DoubleProperty lineSpacingProperty = new SimpleDoubleProperty(0);
    private final DoubleProperty baseOffsetProperty = new SimpleDoubleProperty(0);
    private final BooleanProperty zeroLineVisibleProperty = new SimpleBooleanProperty(false);
}
