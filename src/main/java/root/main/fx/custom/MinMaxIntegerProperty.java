package root.main.fx.custom;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Getter;

@Getter
public class MinMaxIntegerProperty extends SimpleIntegerProperty {

    private final IntegerProperty max;
    private final IntegerProperty min;

    public MinMaxIntegerProperty(IntegerProperty max, IntegerProperty min) {
        super();
        this.max = max;
        this.min = min;
    }

    @Override
    public void set(int newValue) {
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
        set(v.intValue());
    }
}
