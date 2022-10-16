package root.main.fx.custom;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Position {
    private IntegerProperty recordProperty;
    private IntegerProperty offsetProperty;

    public Position(Integer recordProperty, Integer offsetProperty) {
        this.recordProperty = new ReadOnlyIntegerWrapper(recordProperty);
        this.offsetProperty = new ReadOnlyIntegerWrapper(offsetProperty);
    }
}