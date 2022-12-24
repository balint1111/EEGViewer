package root.main;

import edffilereader.file.Annotation;
import edffilereader.file.EDF_File;
import edffilereader.file.EEG_File;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import root.main.common.Properties;
import root.main.swing.MainWindow;
import root.main.fx.MainController;
import root.main.fx.UpdateHandlerController;
import root.main.fx.custom.*;
import root.main.fx.custom.builders.PositionPropertyBuilder;
import root.main.swing.ChannelPicker;
import root.main.swing.CurrentValueWatcher;
import root.main.swing.SettingsWindow;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
@Getter
@RequestMapping("/control")
public class General {
    private MainWindow mainSwingFrame;
    private MainController mainController;
    private PositionPropertyBuilder positionPropertyBuilder;

    private final DataController dataController;
    private final DataModel dataModel;
    private final ConfigurableApplicationContext applicationContext;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;


    private final IntegerProperty pageSizeProperty = new SimpleIntegerProperty();
    private final IntegerProperty numberOfDataRecordsProperty = new SimpleIntegerProperty();
    private final IntegerProperty numberOfSamplesProperty = new SimpleIntegerProperty();
    private final DoubleProperty durationOfDataRecordProperty = new SimpleDoubleProperty();

    private ScrollProperty scrollBarValue;
    private final IntegerProperty fpsProperty = new SimpleIntegerProperty(30);
    private final DoubleProperty slowDownProperty = new SimpleDoubleProperty(1);

    private final ObjectProperty<List<Float>> currentValuesProperty = new SimpleObjectProperty<>();

    public void setScrollBarValue(ScrollProperty scrollBarValue) {
        this.scrollBarValue = scrollBarValue;
        pageEndPosition = positionPropertyBuilder.relative(scrollBarValue.getPosition(), new Position(pageSizeProperty, new ReadOnlyIntegerWrapper(0)));
    }


    private Position pageEndPosition;

    public UpdateHandler updateHandler;

    private final UpdateHandlerController updateHandlerController;
    private Properties properties;

    private final Integer DEFAULT_PAGE_SIZE = 10;

    private final List<Color> colors = new ArrayList<>(Arrays.asList(
            Color.gray(0.05)
//            Color.valueOf("yellow"),
//            Color.valueOf("lightgreen"),
//            Color.valueOf("red"),
//            Color.valueOf("lightblue"),
//            Color.valueOf("violet")
    ));

    public General(@Lazy PositionPropertyBuilder positionPropertyBuilder,
                   DataController dataController,
                   DataModel dataModel,
                   ConfigurableApplicationContext applicationContext,
                   AutowireCapableBeanFactory autowireCapableBeanFactory,
                   @Lazy UpdateHandlerController updateHandlerController,
                   Properties properties) {
        this.positionPropertyBuilder = positionPropertyBuilder;
        this.dataController = dataController;
        this.dataModel = dataModel;
        this.applicationContext = applicationContext;
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.updateHandlerController = updateHandlerController;
        this.properties = properties;
    }

    @Autowired
    private void init(MainController mainController) {
        this.mainController = mainController;
    }

    @Autowired
    private void setMainSwingFrame(MainWindow mainSwingFrame) {
        this.mainSwingFrame = mainSwingFrame;
    }


    @PostConstruct
    public void postConstruct() {
        log.info(getClass().getSimpleName() + " has been initialized");
        pageSizeProperty.addListener((observable, oldValue, newValue) -> {
            try {
                dataController.showDataRecord();
            } catch (NullPointerException e) {
                log.error("Something is not initialized");
            }
        });
        properties.getMaxQueueSizeProperty().addListener((observable, oldValue, newValue) -> dataModel.setMaxQueueSize(newValue.intValue()));
    }

    private void openNewFile(File file) {
        try {
            updateHandlerController.getSelectedChannels().clear();
            dataModel.setEeg_file(EEG_File.build(file));
            for (Annotation annotation : ((EDF_File) dataModel.getEeg_file()).getAnnotations()) {
                System.out.println("onSet: " + annotation.getOnset() + "duration: " + annotation.getDuration() + " description: " + annotation.getDescription());
            }
            durationOfDataRecordProperty.set((Double) dataModel.getEeg_file().getHeader().getExtraParameters().get("durationOfDataRecord") * 1000);
            numberOfDataRecordsProperty.setValue(dataModel.getEeg_file().getHeader().getNumberOfDataRecords());
            numberOfSamplesProperty.setValue(dataModel.getEeg_file().getHeader().getNumberOfSamples().stream().max(Integer::compare).get());

            pickChannel();

            pageSizeProperty.setValue(DEFAULT_PAGE_SIZE);
            scrollBarValue.set(new Position(0, 1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/previous")
    public void previousPage(ActionEvent actionEvent) {
        scrollBarValue.getPosition().getRecordProperty().setValue(scrollBarValue.getPosition().getRecordProperty().get() - pageSizeProperty.get());
    }

    @PostMapping("/next")
    public void nextPage(ActionEvent actionEvent) {
//        dataController.getSelectedChannels().addAll(4);
//        updateHandler.setAmplitudes(amplitude);
//        updateHandler.setColors(colors);
        scrollBarValue.getPosition().getRecordProperty().setValue(scrollBarValue.getPosition().getRecordProperty().get() + pageSizeProperty.get());
    }


    public void open() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        openNewFile(fileChooser.showOpenDialog(new Stage()));
    }

    public void pickChannel() {
//        updateHandlerController.getSelectedChannels().setAll(ChannelPickerDialog.display(dataModel.getEeg_file().getHeader().getLabelsOfTheChannels()));
        ChannelPicker colorPicker = new ChannelPicker(mainSwingFrame, true);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(colorPicker);
        colorPicker.setVisible(true);
//        updateHandlerController.setColors(colors);
    }

    public void openSettings() {
//        updateHandlerController.getSelectedChannels().setAll(ChannelPickerDialog.display(dataModel.getEeg_file().getHeader().getLabelsOfTheChannels()));
        SettingsWindow settingsWindow = new SettingsWindow(mainSwingFrame, false);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(settingsWindow);
        settingsWindow.setVisible(true);
    }

    public void openValueWatcher() {
        CurrentValueWatcher valueWatcher = new CurrentValueWatcher(mainSwingFrame, false);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(valueWatcher);
        valueWatcher.setVisible(true);
    }
}
