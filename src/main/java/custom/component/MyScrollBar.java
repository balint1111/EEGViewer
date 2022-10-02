package custom.component;

import javafx.fxml.Initializable;
import javafx.scene.control.ScrollBar;
import org.springframework.beans.factory.annotation.Autowired;
import root.main.MainController;
import root.main.UpdateHandler;
import root.main.UpdateHandlerController;

import java.net.URL;
import java.util.ResourceBundle;

public class MyScrollBar extends ScrollBar implements Initializable {

    private MainController mainController;
    private UpdateHandler updateHandler;

    public MyScrollBar() {}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Autowired
    public void init(MainController mainController) {
        this.mainController = mainController;
        visibleAmountProperty().bind(mainController.getPageSizeProperty());
        maxProperty().bind(mainController.getNumberOfDateRecordsProperty().subtract(visibleAmountProperty()));
    }
//    @Autowired
    public void setUpdateHandlerController(UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
//        layoutYProperty().bind(updateHandler.getViewportHeightProperty().subtract(heightProperty()));
        prefWidthProperty().bind(updateHandler.getViewportWidthProperty());
//        System.out.println("2");
        valueProperty().addListener((observable, oldValue, newValue) -> {
            mainController.getDataController().jumpToPosition(newValue.intValue());
        });
    }


}
