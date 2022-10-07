package root.main;

import root.main.fx.custom.UpdateHandler;
import root.main.fx.UpdateHandlerController;
import root.main.fx.custom.MinMaxIntegerProperty;
import root.main.fx.custom.ScrollProperty;
import root.main.fx.custom.ChannelPickerDialog;
import edffilereader.file.EEG_File;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import root.main.fx.MainController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
@Getter
public class General {
    private MainController mainController;

    private final DataController dataController;
    private final DataModel dataModel;
    private final ConfigurableApplicationContext applicationContext;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;


    private final IntegerProperty pageSizeProperty = new SimpleIntegerProperty();
    private final IntegerProperty numberOfDataRecordsProperty = new SimpleIntegerProperty();
    private final IntegerProperty numberOfSamplesProperty = new SimpleIntegerProperty();
    @Setter
    private ScrollProperty scrollBarValue;
    public UpdateHandler updateHandler;

    private final UpdateHandlerController updateHandlerController;

    private double amplitude = 1;

    private Integer DEFAULT_PAGE_SIZE = 100;

    private final List<Color> colors = new ArrayList<>(Arrays.asList(
            Color.gray(0.05)
//            Color.valueOf("yellow"),
//            Color.valueOf("lightgreen"),
//            Color.valueOf("red"),
//            Color.valueOf("lightblue"),
//            Color.valueOf("violet")
    ));

    public General(DataController dataController, DataModel dataModel,
                   ConfigurableApplicationContext applicationContext,
                   AutowireCapableBeanFactory autowireCapableBeanFactory,
                   @Lazy UpdateHandlerController updateHandlerController
    ) {
        this.dataController = dataController;
        this.dataModel = dataModel;
        this.applicationContext = applicationContext;
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.updateHandlerController = updateHandlerController;
    }

    @Autowired
    private void init(MainController mainController) {
        this.mainController = mainController;
    }


    @PostConstruct
    public void postConstruct() {
        System.out.println(getClass().getSimpleName() + " has been initialized");
        pageSizeProperty.addListener((observable, oldValue, newValue) -> {
            try {
                dataController.showDataRecord();
            } catch (NullPointerException e) {
                log.error("Something is not initialized");
            }
        });
    }

    private void openNewFile(File file) {
        try {
            dataModel.setEeg_file(EEG_File.build(file));

            numberOfDataRecordsProperty.setValue(dataModel.getEeg_file().getHeader().getNumberOfDataRecords());
            numberOfSamplesProperty.setValue(dataModel.getEeg_file().getHeader().getNumberOfSamples().stream().max(Integer::compare).get());

            pickChannel();

            updateHandlerController.getLineSpacingProperty().setValue(70);
            updateHandlerController.getAmplitudeProperty().set(0.1);

            pageSizeProperty.setValue(DEFAULT_PAGE_SIZE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/previous")
    public void previousPage(ActionEvent actionEvent) {
        scrollBarValue.getRecordProperty().setValue(scrollBarValue.getRecordProperty().get() - pageSizeProperty.get());
    }

    @PostMapping("/next")
    public void nextPage(ActionEvent actionEvent) {
//        dataController.getSelectedChannels().addAll(4);
//        updateHandler.setAmplitudes(amplitude);
//        updateHandler.setColors(colors);
        scrollBarValue.getRecordProperty().setValue(scrollBarValue.getRecordProperty().get() + pageSizeProperty.get());
    }


    public void open() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        openNewFile(fileChooser.showOpenDialog(new Stage()));
    }

    public void pickChannel() {
        updateHandlerController.getSelectedChannels().setAll(ChannelPickerDialog.display(dataModel.getEeg_file().getHeader().getLabelsOfTheChannels()));
        updateHandlerController.setColors(colors);
        dataController.showDataRecord();
    }
}
