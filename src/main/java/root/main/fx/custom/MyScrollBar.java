package root.main.fx.custom;

import javafx.scene.control.ScrollBar;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import root.main.General;

@Getter
public class MyScrollBar extends ScrollBar {

    private General general;
    private ScrollProperty scrollProperty;

    public MyScrollBar() {
        System.out.println("scrollbar");
    }

    @Autowired
    public void init(General general) {
        this.general = general;
        scrollProperty = new ScrollProperty(
                PropertyUtils.toIntegerProperty(general.getNumberOfDataRecordsProperty().subtract(general.getPageSizeProperty())),
                general.getNumberOfSamplesProperty(),
                () -> this.general.getDataController().jumpToPosition());
        scrollProperty.setDoubleProperty(valueProperty());
        general.setScrollBarValue(scrollProperty);

        maxProperty().bind(scrollProperty.getRecordProperty().getMax());
        visibleAmountProperty().bind(general.getPageSizeProperty().divide(general.getNumberOfDataRecordsProperty().divide(maxProperty())));
        maxProperty().addListener((observable, oldValue, newMax) -> {
            if (newMax.doubleValue() < valueProperty().get()) {
                valueProperty().setValue(newMax);
            }
        });
    }



}
