package custom.component;

import javafx.fxml.Initializable;
import javafx.scene.control.ScrollBar;
import org.springframework.beans.factory.annotation.Autowired;
import root.main.MainController;
import root.main.UpdateHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class MyScrollBar extends ScrollBar implements Initializable {

    private MainController mainController;
    private UpdateHandler updateHandler;

    public MyScrollBar() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Autowired
    public void init(MainController mainController) {
        this.mainController = mainController;
        mainController.getPageSizeProperty().addListener((observable, oldValue, newValue) -> {

        });

        visibleAmountProperty().bind(mainController.getPageSizeProperty());
        maxProperty().bind(mainController.getNumberOfDataRecordsProperty().subtract(visibleAmountProperty()));
        maxProperty().addListener((observable, oldValue, newMax) -> {
            if (newMax.doubleValue() < valueProperty().get()) {
                valueProperty().setValue(newMax);
            }
        });
    }


    public void setUpdateHandlerController(UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
        prefWidthProperty().bind(updateHandler.getViewportWidthProperty());
        valueProperty().addListener((observable, oldValue, newValue) -> {
            if (minProperty().get() <= newValue.doubleValue() && newValue.doubleValue() <= maxProperty().get()) {
                mainController.getDataController().jumpToPosition();
            } else if (minProperty().get() > newValue.doubleValue()) {
                valueProperty().setValue(minProperty().get());
            } else if (maxProperty().get() < newValue.doubleValue()) {
                valueProperty().setValue(maxProperty().get());
            }
        });
    }


}
