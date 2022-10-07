package root.main.fx.custom;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import lombok.Getter;

@Getter
public class MinMaxDoubleProperty extends SimpleDoubleProperty {

    private final DoubleProperty max;
    private final DoubleProperty min;

    public MinMaxDoubleProperty(DoubleProperty max, DoubleProperty min) {
        super();
        this.max = max;
        this.min = min;
    }

    @Override
    public void set(double newValue) {
        if (min.get() <= newValue && newValue <= max.get()) {
            super.set(newValue);
        } else if (min.get() > newValue) {
            super.set(min.get());
        } else if (max.get() < newValue) {
            super.set(max.get());
        }
    }

    @Override
    public void setValue(Number v) {
        set(v.doubleValue());
    }
}
