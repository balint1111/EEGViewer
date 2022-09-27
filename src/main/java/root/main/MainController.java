package root.main;

import com.sun.javafx.scene.control.IntegerField;
import edffilereader.file.EEG_File;
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
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

@Service
@RestController
@RequestMapping("/control")
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
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;


    private final IntegerProperty pageSizeProperty = new SimpleIntegerProperty();
    @FXML
    public UpdateHandler updateHandler;

    private EEG_File eegFile;


    public MainController(DataController dataController, DataModel dataModel, AutowireCapableBeanFactory autowireCapableBeanFactory) {
        this.dataController = dataController;
        this.dataModel = dataModel;
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
    }

    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        open(null);
    }

    private void openNewFile(File file) {
        int numberOfChannels = 10;
        double amplitude = 0.05;
        int pageSize = 100;
        try {
            eegFile = EEG_File.build(file);
            dataModel.setEeg_file(eegFile);

            autowireCapableBeanFactory.autowireBean(updateHandler);
            updateHandler.setLineSpacing(100d);

            dataController.setUpdateHandler(updateHandler);
            dataController.setDataModel(dataModel);

            dataController.setNumberOfChannels(numberOfChannels);

            pageSizeProperty.bindBidirectional(pageSizeField.valueProperty());
            pageSizeProperty.addListener((observable, oldValue, newValue) -> {
                try {
                    Integer maxValue = eegFile.getHeader().getNumberOfDataRecords();
                    if ((Integer) newValue > maxValue) {
                        pageSizeField.setValue(maxValue);
                        dataController.rangeChange(maxValue);
                    } else if (((Integer) newValue).compareTo(1) >= 0) {
                        dataController.rangeChange(newValue.intValue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            pageSizeProperty.setValue(pageSize);
            dataController.showFirstPage(pageSize);


            updateHandler.setAmplitudes(amplitude);
            updateHandler.setColors(colors);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/previous")
    public void previousPage(ActionEvent actionEvent) {
        dataController.showPreviousPage();
    }

    @PostMapping("/next")
    public void nextPage(ActionEvent actionEvent) {
        dataController.showNextPage();
    }


    public void open(ActionEvent actionEvent) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        openNewFile(fileChooser.showOpenDialog(new Stage()));
    }
}
