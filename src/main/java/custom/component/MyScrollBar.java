package custom.component;

import javafx.fxml.Initializable;
import javafx.scene.control.ScrollBar;
import org.springframework.beans.factory.annotation.Autowired;
import root.main.General;
import root.main.UpdateHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class MyScrollBar extends ScrollBar implements Initializable {

    private General general;
    private UpdateHandler updateHandler;
    private MinMaxProperty minMaxProperty;

    public MyScrollBar() {
        System.out.println("scrollbar");
        minMaxProperty = new MinMaxProperty(maxProperty(), minProperty());
        valueProperty().bindBidirectional(minMaxProperty);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Autowired
    public void init(General mainController) {
        this.general = mainController;
        mainController.setScrollBarValue(minMaxProperty);
        visibleAmountProperty().bind(mainController.getPageSizeProperty());
        maxProperty().bind(mainController.getNumberOfDataRecordsProperty().subtract(visibleAmountProperty()));
        maxProperty().addListener((observable, oldValue, newMax) -> {
            if (newMax.doubleValue() < valueProperty().get()) {
                valueProperty().setValue(newMax);
            }
        });
        minMaxProperty.addListener((observable, oldValue, newValue) -> {
            general.getDataController().jumpToPosition();
        });
    }



}
