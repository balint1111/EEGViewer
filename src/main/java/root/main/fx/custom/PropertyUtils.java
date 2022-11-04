package root.main.fx.custom;

import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;

public class PropertyUtils {

    public static IntegerProperty toIntegerProperty( NumberBinding binding) {
        SimpleIntegerProperty prop = new SimpleIntegerProperty();
        prop.bind(binding);
        return prop;
    }

    public static LongProperty toLongProperty(NumberBinding binding) {
        LongProperty prop = new SimpleLongProperty();
        prop.bind(binding);
        return prop;
    }
}
