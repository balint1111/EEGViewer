package custom.component;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import root.main.DataController;

public class PolylineHorizontalResolutionProperty extends SimpleIntegerProperty {

    private DataController dataController;

    PolylineHorizontalResolutionProperty(DataController dataController) {
        this.addListener((observableValue, oldValue, newValue) -> {
            dataController.showDataRecord();
        });
    }
}
