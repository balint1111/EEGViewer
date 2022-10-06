package root.main;

import com.sun.javafx.scene.control.DoubleField;
import com.sun.javafx.scene.control.IntegerField;
import custom.component.MyScrollBar;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Controller
public class MainController implements Initializable {

    @FXML
    public IntegerField pageSizeField;

    @FXML
    public DoubleField amplitudeField;
    @FXML
    public DoubleField lineSpacingField;

    @FXML
    public MyScrollBar myScrollBar;


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
                          ConfigurableApplicationContext applicationContext, General general) {
        this.dataController = dataController;
        this.dataModel = dataModel;
        this.applicationContext = applicationContext;
        this.autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        this.general = general;
        System.out.println("MainController");
    }

    @SneakyThrows
    public Scene createScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainWindow.fxml"));
        fxmlLoader.setController(this);

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 1200, 700);
        return scene;
        //scene.getStylesheets().add("/style.css");
//        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
    }

    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        autowireCapableBeanFactory.autowireBean(updateHandler);
        applicationContext.getBeanFactory().registerSingleton(updateHandler.getClass().getCanonicalName(), updateHandler);
        updateHandlerController = updateHandler.getController();
        autowireCapableBeanFactory.autowireBean(myScrollBar);
        applicationContext.getBeanFactory().registerSingleton(myScrollBar.getClass().getCanonicalName(), myScrollBar);
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
