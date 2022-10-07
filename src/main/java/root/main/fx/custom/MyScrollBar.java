package root.main.fx.custom;

import javafx.fxml.Initializable;
import javafx.scene.control.ScrollBar;
import org.springframework.beans.factory.annotation.Autowired;
import root.main.General;

import java.net.URL;
import java.util.ResourceBundle;

public class MyScrollBar extends ScrollBar implements Initializable {

    private General general;
    private UpdateHandler updateHandler;
    private ScrollProperty scrollProperty;

    public MyScrollBar() {
        System.out.println("scrollbar");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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
