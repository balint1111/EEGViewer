package root.main.fx;

import com.sun.javafx.scene.control.DoubleField;
import com.sun.javafx.scene.control.IntegerField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import root.main.fx.custom.MyScrollBar;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import root.main.*;
import root.main.fx.custom.UpdateHandler;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Controller
@Slf4j
public class MainController implements Initializable {

    @FXML
    public IntegerField pageSizeField;

    @FXML
    public DoubleField amplitudeField;
    @FXML
    public DoubleField lineSpacingField;


    private final DataController dataController;
    private final DataModel dataModel;
    private final ConfigurableApplicationContext applicationContext;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;
    private final General general;

    @FXML
    public UpdateHandler updateHandler;

    private UpdateHandlerController updateHandlerController;


    public MainController(DataController dataController,
                          DataModel dataModel,
                          ConfigurableApplicationContext applicationContext,
                          @Lazy General general) {
        this.dataController = dataController;
        this.dataModel = dataModel;
        this.applicationContext = applicationContext;
        this.autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        this.general = general;
        log.info(getClass().getSimpleName() + " has been initialized");
    }

    @SneakyThrows
    public Scene createScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainWindow.fxml"));
        fxmlLoader.setController(this);

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 1200, 700);
        return scene;
    }

    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        autowireCapableBeanFactory.autowireBean(updateHandler);
        applicationContext.getBeanFactory().registerSingleton(updateHandler.getClass().getCanonicalName(), updateHandler);
        updateHandlerController = updateHandler.getController();
        init();
    }

    private void init(){
        if (pageSizeField != null) {
            pageSizeField.maxValueProperty().bind(general.getNumberOfDataRecordsProperty());
            general.getPageSizeProperty().bindBidirectional(pageSizeField.valueProperty());
        }
        if (amplitudeField != null) {
//            general.getAmplitudeProperty().bindBidirectional(amplitudeField.valueProperty());
        }
        if (lineSpacingField != null) {
            lineSpacingField.valueProperty().bindBidirectional(updateHandler.getLineSpacingProperty());
        }
    }


}
