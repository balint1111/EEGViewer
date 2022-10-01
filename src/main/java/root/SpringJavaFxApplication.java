package root;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import root.main.DataController;
import root.main.DataModel;
import root.main.swing.MainSwing;
import root.main.swing.SwingController;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.*;

@SpringBootApplication
public class SpringJavaFxApplication extends JFrame {

    @Autowired
    private DataController dataController;
    @Autowired
    private MainSwing mainSwing;

    private final Integer WIDTH = 1200;
    private final Integer HEIGHT = 1200;
    private final String TITLE = "EEG";


    public SpringJavaFxApplication() {
    }

    public void initUI() {
        try {
            setTitle(TITLE);
            setSize(new Dimension(WIDTH, HEIGHT));

            setLocationRelativeTo(null);
            setVisible(true);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringJavaFxApplication.class);
        EventQueue.invokeLater(() -> {
            SpringJavaFxApplication ex = applicationContext.getBean(SpringJavaFxApplication.class);
            ex.initUI();
            ex.setVisible(true);
        });
    }

    @Bean
    DataModel dataModel() {
        return new DataModel(100000, dataController);
    }

    @Bean
    ThreadPoolExecutor backgroundExecutor() {
        return new ThreadPoolExecutor( 1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>() );
    }
}
