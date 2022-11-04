package root;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class SpringJavaFxApplication  {


    public SpringJavaFxApplication() {}

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringJavaFxApplication.class);
        EventQueue.invokeLater(() -> {
            SpringJavaFxApplication ex = applicationContext.getBean(SpringJavaFxApplication.class);
        });
    }


}
