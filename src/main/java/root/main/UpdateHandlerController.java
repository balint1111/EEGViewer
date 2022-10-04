package root.main;

import com.sun.javafx.fxml.expression.ExpressionValue;
import custom.component.MyPolyline;
import custom.component.MyScrollBar;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

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
    @Autowired
    private MyScrollBar myScrollBar;



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
        synchronized (myPolylineList) {
            for (int i = 0; i < myPolylineList.size(); i++) {
                myPolylineList.get(i).layoutYProperty().set((i + 1) * spacing);
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
                myPolyline.getLineProperty().getAmplitude().set(amplitude);
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
            setLineSpacings(updateHandler.getLineSpacingProperty().get());
        }
    }

    int i=0;
    int j=0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateHandler.addEventFilter(ScrollEvent.SCROLL, this::shiftDownFilter);
        updateHandler.getLineSpacingProperty().addListener(this::lineSpacingChange);
    }

    private void scroll(ScrollEvent event) {
        double newValue = myScrollBar.valueProperty().get() - event.getDeltaY();
        double minValue = myScrollBar.minProperty().get();
        double maxValue = myScrollBar.maxProperty().get();
        if (minValue < newValue && newValue < maxValue) {
            myScrollBar.valueProperty().setValue(newValue);
        } else if (minValue > newValue) {
            myScrollBar.valueProperty().setValue(minValue);
        } else {
            myScrollBar.valueProperty().setValue(maxValue);
        }
    }

    private void setLineSpacings(Double newValue) {
        synchronized (myPolylineList) {
            for (int i = 0; i < myPolylineList.size(); i++) {
                myPolylineList.get(i).layoutYProperty().set((i + 1) * newValue.doubleValue());
            }
        }
    }

    private void lineSpacingChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        setLineSpacings(newValue.doubleValue());
    }

    private void shiftDownFilter(ScrollEvent event) {
        if (event.isShiftDown()) {
            scroll(event);
            event.consume();
        }
    }
}
