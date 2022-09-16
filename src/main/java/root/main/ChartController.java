package root.main;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;

@Component
public class ChartController extends ScrollPane{

    @FXML
    public AnchorPane pane;

    private final DataController dataController;


    public ChartController(DataController dataController) {
        this.dataController = dataController;
    }
}
