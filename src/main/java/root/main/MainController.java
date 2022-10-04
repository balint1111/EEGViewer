package root.main;

import com.sun.javafx.scene.control.DoubleField;
import com.sun.javafx.scene.control.IntegerField;
import custom.component.MyScrollBar;
import custom.dialogs.ChannelPickerDialog;
import edffilereader.file.EEG_File;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.SpringJavaFxApplication;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

@RestController
@Getter
@RequestMapping("/control")
public class MainController implements Initializable {
    @FXML
    public HBox root;
    @FXML
    public Button previousBtn;
    @FXML
    public IntegerField pageSizeField;

    @FXML
    public DoubleField amplitudeField;
    @FXML
    public DoubleField lineSpacingField;

    @FXML
    public MyScrollBar myScrollBar;

    private double amplitude = 0.3;

    private Integer DEFAULT_PAGE_SIZE = 100;

    private final List<Color> colors = new ArrayList<>(Arrays.asList(
            Color.valueOf("yellow"),
            Color.valueOf("lightgreen"),
            Color.valueOf("red"),
            Color.valueOf("lightblue"),
            Color.valueOf("violet")
    ));


    private final DataController dataController;
    private final DataModel dataModel;
    private final ConfigurableApplicationContext applicationContext;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;


    private final IntegerProperty pageSizeProperty = new SimpleIntegerProperty();
    private final IntegerProperty numberOfDataRecordsProperty = new SimpleIntegerProperty();
    @FXML
    public UpdateHandler updateHandler;

    private UpdateHandlerController updateHandlerController;


    public MainController(DataController dataController,
                          DataModel dataModel,
                          ConfigurableApplicationContext applicationContext) {
        this.dataController = dataController;
        this.dataModel = dataModel;
        this.applicationContext = applicationContext;
        this.autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        applicationContext.getBeanFactory().registerSingleton(dataController.getClass().getCanonicalName(), dataController);

    }

    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        open(null);
        updateHandlerController = updateHandler.getController();
        autowireCapableBeanFactory.autowireBean(updateHandler);
        autowireCapableBeanFactory.autowireBean(dataController);

        applicationContext.getBeanFactory().registerSingleton(myScrollBar.getClass().getCanonicalName(), myScrollBar);
        applicationContext.getBeanFactory().registerSingleton(updateHandlerController.getClass().getCanonicalName(), updateHandlerController);
        autowireCapableBeanFactory.autowireBean(myScrollBar);
        autowireCapableBeanFactory.autowireBean(updateHandlerController);
        autowireCapableBeanFactory.autowireBean(dataController);
        myScrollBar.setUpdateHandlerController(updateHandler);
        init();
    }

    private void init(){
        pageSizeField.maxValueProperty().bind(numberOfDataRecordsProperty);
        pageSizeProperty.bindBidirectional(pageSizeField.valueProperty());
        pageSizeProperty.addListener((observable, oldValue, newValue) -> {
            dataController.showDataRecord();
        });
        amplitudeField.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateHandlerController.setAmplitudes(newValue.doubleValue());
        });
        lineSpacingField.valueProperty().bindBidirectional(updateHandlerController.getUpdateHandler().getLineSpacingProperty());

        updateHandlerController.getUpdateHandler().getLineSpacingProperty().setValue(50);
    }

    private void openNewFile(File file) {
        try {
            dataModel.setEeg_file(EEG_File.build(file));

            numberOfDataRecordsProperty.setValue(dataModel.getEeg_file().getHeader().getNumberOfDataRecords());

            pickChannel();

            pageSizeProperty.setValue(DEFAULT_PAGE_SIZE);

            updateHandlerController.setAmplitudes(amplitude);
            updateHandlerController.setColors(colors);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/previous")
    public void previousPage(ActionEvent actionEvent) {
        myScrollBar.setValue(myScrollBar.valueProperty().get() - pageSizeProperty.get());
    }

    @PostMapping("/next")
    public void nextPage(ActionEvent actionEvent) {
//        dataController.getSelectedChannels().addAll(4);
//        updateHandler.setAmplitudes(amplitude);
//        updateHandler.setColors(colors);
        myScrollBar.setValue(myScrollBar.valueProperty().get() + pageSizeProperty.get());
    }


    public void open() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        openNewFile(fileChooser.showOpenDialog(new Stage()));
    }

    public void pickChannel() {
        dataController.getSelectedChannels().setAll(ChannelPickerDialog.display(dataModel.getEeg_file().getHeader().getLabelsOfTheChannels()));
        updateHandlerController.setAmplitudes(amplitude);
        updateHandlerController.setColors(colors);
    }
}
