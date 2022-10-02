package root.main;

import custom.component.MyPolyline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


@Getter
public class UpdateHandlerController implements Initializable {

    @FXML
    private Pane group;

    @FXML
    private UpdateHandler updateHandler;

    private List<MyPolyline> myPolylineList = new ArrayList<>();

    private Double lineSpacing = 0d;

    @Autowired
    private DataController dataController;



    public void setYVectors(List<Double>[] yVectors) {
        synchronized (myPolylineList) {
            for (int i = 0; i < myPolylineList.size(); i++) {
                MyPolyline myPolyline = myPolylineList.get(i);
                myPolyline.setYVector(yVectors[myPolyline.getChannelNumber()]);
            }
        }
    }

    public void update() {
        synchronized (myPolylineList) {
            for (MyPolyline myPolyline : myPolylineList) {
                myPolyline.update();
            }
        }
    }

    public void setLineSpacing(Double spacing) {
        this.lineSpacing = spacing;
        synchronized (myPolylineList) {
            for (int i = 0; i < myPolylineList.size(); i++) {
                myPolylineList.get(i).setYPosition((i + 1) * spacing);
            }
        }
    }

    public void setColors(List<Color> colors) {
        synchronized (myPolylineList) {
            for (int i = 0; i < myPolylineList.size(); i++) {
                Color color = colors.get(i % colors.size());
                if (color != null)
                    myPolylineList.get(i).getLineProperty().getStrokeProperty().setValue(color);
            }
        }
    }

    public void setAmplitudes(Double amplitude) {
        synchronized (myPolylineList) {
            for (MyPolyline myPolyline : myPolylineList) {
                myPolyline.setAmplitude(amplitude);
            }
        }
    }

    public void onChangeSelectedChannels(ListChangeListener.Change<? extends Integer> c) {
        group.getChildren().removeIf(node -> myPolylineList.stream().anyMatch(myPolyline -> myPolyline.equals(node)));
        synchronized (myPolylineList) {
            myPolylineList.clear();
            for (int i = 0; i < dataController.getSelectedChannels().size(); i++) {
                MyPolyline myPolyline = new MyPolyline(dataController, c.getList().get(i), group, updateHandler);
                myPolylineList.add(myPolyline);
            }
            setLineSpacing(lineSpacing);
        }
    }

    int i=0;
    int j=0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        EventHandler<? super ScrollEvent> defaultScrollHandler = group.onScrollProperty().get();
        updateHandler.addEventFilter(ScrollEvent.SCROLL,new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.isShiftDown()) {
                    event.consume();
                }
            }
        });
        updateHandler.onScrollProperty().set(event -> {
            System.out.println("updateH:" + j++);
        });
        group.onScrollProperty().set(event -> {
            System.out.println("scroll" + i++);
        });
    }
}
