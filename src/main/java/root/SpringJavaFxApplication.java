package root;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import root.main.DataController;
import root.main.DataModel;

import java.util.concurrent.*;

@SpringBootApplication
public class SpringJavaFxApplication extends Application {


    private static ConfigurableApplicationContext applicationContext;

    private Parent root;

    @Autowired
    private DataController dataController;

    @Override
    public void init() throws Exception {
        applicationContext = SpringApplication.run(SpringJavaFxApplication.class);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainWindow.fxml"));
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        root = fxmlLoader.load();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Objektum orientált adatbázis demó");
        Scene scene = new Scene(root, 1200, 700);
        //scene.getStylesheets().add("/style.css");
//        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        applicationContext.close();
        Platform.exit();
    }

    @Bean
    Group group() {
        return new Group();
    }

    @Bean
    ColorPicker colorPicker() {
        return new ColorPicker();
    }

    @Bean
    DataModel dataModel() {
        return new DataModel(1000, dataController);
    }

    @Bean
    ThreadPoolExecutor backgroundExecutor() {
        return new ThreadPoolExecutor( 1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>() );
    }

    public static ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void main(String[] args) {
        Application.launch(SpringJavaFxApplication.class, args);
    }
}
