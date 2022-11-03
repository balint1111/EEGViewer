package root;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class SpringJavaFxApplication extends JFrame {

    private final Integer WIDTH = 1200;
    private final Integer HEIGHT = 1200;
    private final String TITLE = "EEG";


    public SpringJavaFxApplication() {
    }

    public void initUI() {
        setTitle(TITLE);
        setSize(new Dimension(WIDTH, HEIGHT));

        setLocationRelativeTo(null);
        setVisible(true);
        setFocusable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringJavaFxApplication.class);
        EventQueue.invokeLater(() -> {
            SpringJavaFxApplication ex = applicationContext.getBean(SpringJavaFxApplication.class);
            ex.initUI();
            ex.setVisible(true);
        });
    }


}
