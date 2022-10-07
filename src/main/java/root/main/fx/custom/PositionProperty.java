package root.main.fx.custom;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Getter;

@Getter
public class PositionProperty {
    protected MinMaxIntegerProperty recordProperty;
    protected MinMaxIntegerProperty offsetProperty;

    public PositionProperty(IntegerProperty recordMax, IntegerProperty offsetMax) {
        SimpleIntegerProperty zeroProp = new SimpleIntegerProperty(0);
        recordProperty = new MinMaxIntegerProperty(recordMax, zeroProp);
        offsetProperty = new MinMaxIntegerProperty(offsetMax, zeroProp);
    }
}
