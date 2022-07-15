package root.main;

import com.sun.javafx.scene.control.IntegerField;
import edffilereader.file.EDF_File;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

@Component
public class MainController implements Initializable {

    @FXML
    public VBox vBox;
    @FXML
    public HBox hBox;
    @FXML
    public HBox root;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public Button previousBtn;
    @FXML
    public AnchorPane pane;
    @FXML
    public IntegerField pageSizeField;

    private final ExecutorService backgroundExecutor;

    private final List<Color> colors = new ArrayList<>(Arrays.asList(
            Color.valueOf("yellow"),
            Color.valueOf("lightgreen"),
            Color.valueOf("red"),
            Color.valueOf("lightblue"),
            Color.valueOf("violet")
    ));


    private final DataController dataController;


    private final IntegerProperty pageSizeProperty = new SimpleIntegerProperty();


    public MainController(ExecutorService backgroundExecutor, DataController dataController) {
        this.backgroundExecutor = backgroundExecutor;
        this.dataController = dataController;
    }

    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init(new File("/home/balint/Dokumentumok/EEGViewer/src/main/resources/ma0844az_1-1+.edf"),
                1, 0.75, 300, 1);
    }

    private void init(File file, int numberOfChannels, double amplitude, int pageSize, int maxQueueSize) {
        try {
            DataModel dataModel = DataModel.get(maxQueueSize);

            VBox group = new VBox();
            group.prefWidthProperty().bind(pane.prefWidthProperty());
            group.setStyle("-fx-background-color: pink;");

            pane.getChildren().add(group);

            UpdateHandler updateHandler = UpdateHandler.get(group);


            dataModel.setDataController(dataController);
            dataModel.setEeg_file(new EDF_File(file));

            updateHandler.setDataController(dataController);
            updateHandler.setLineSpacing(100d);

            dataController.setUpdateHandler(updateHandler);
            dataController.setDataModel(dataModel);

            dataController.setNumberOfChannels(numberOfChannels);

            pageSizeProperty.bindBidirectional(pageSizeField.valueProperty());
            pageSizeProperty.addListener((observable, oldValue, newValue) -> {
                if (((Integer) newValue).compareTo(1) >= 0)
                    try {
                        dataController.rangeChange(newValue.intValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            });
            pageSizeProperty.setValue(pageSize);


            updateHandler.setAmplitudes(amplitude);
            updateHandler.setColors(colors);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void previousPage(ActionEvent actionEvent){
        dataController.showPreviousPage();
    }

    public void nextPage(ActionEvent actionEvent){
        dataController.showNextPage();
    }


}
