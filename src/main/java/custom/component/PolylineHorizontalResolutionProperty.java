package custom.component;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import root.main.DataController;

public class PolylineHorizontalResolutionProperty extends SimpleIntegerProperty {
    private SimpleLongProperty lastUpdate;
    
    PolylineHorizontalResolutionProperty(Long minDelayBetweenUpdates, DataController dataController){
        this.lastUpdate = new SimpleLongProperty(System.currentTimeMillis());
        this.addListener((observableValue, oldValue, newValue) -> {

                Long updateTime = System.currentTimeMillis();
                if (Long.valueOf(updateTime-lastUpdate.getValue()).compareTo(minDelayBetweenUpdates) > 0) {
                    System.out.println("update");
                    dataController.showDataRecord(dataController.getFrom(), dataController.getTo(), lastUpdate);
                }


        });
    }
}
