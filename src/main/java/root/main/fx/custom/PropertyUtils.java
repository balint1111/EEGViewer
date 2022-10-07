package root.main.fx.custom;

import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class PropertyUtils {

    public static IntegerProperty toIntegerProperty( NumberBinding binding) {
        SimpleIntegerProperty prop = new SimpleIntegerProperty();
        prop.bind(binding);
        return prop;
    }
}
