package root.main;

import com.sun.javafx.scene.control.IntegerField;
import edffilereader.file.EDF_File;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class MainController implements Initializable {
    @FXML
    public HBox root;
    @FXML
    public Button previousBtn;
    @FXML
    public IntegerField pageSizeField;

    private final List<Color> colors = new ArrayList<>(Arrays.asList(
            Color.valueOf("yellow"),
            Color.valueOf("lightgreen"),
            Color.valueOf("red"),
            Color.valueOf("lightblue"),
            Color.valueOf("violet")
    ));


    private final DataController dataController;
    private final DataModel dataModel;


    private final IntegerProperty pageSizeProperty = new SimpleIntegerProperty();
    @FXML
    public UpdateHandler updateHandler;


    public MainController(DataController dataController, DataModel dataModel) {
        this.dataController = dataController;
        this.dataModel = dataModel;
    }

    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        open(null);
    }

    private void openNewFile(File file) {
        int numberOfChannels = 5;
        double amplitude = 0.75;
        int pageSize = 100;
        try {

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

    public void previousPage(ActionEvent actionEvent) {
        dataController.showPreviousPage();
    }

    public void nextPage(ActionEvent actionEvent) {
        dataController.showNextPage();
    }


    public void open(ActionEvent actionEvent) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        openNewFile(fileChooser.showOpenDialog(new Stage()));
    }
}
