package root;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.*;

@SpringBootApplication
public class SpringJavaFxApplication extends Application {


    private ConfigurableApplicationContext applicationContext;
    private Parent root;

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
    ThreadPoolExecutor backgroundExecutor() {
        return new ThreadPoolExecutor( 1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>() );
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void main(String[] args) {
        Application.launch(SpringJavaFxApplication.class, args);
    }
}
