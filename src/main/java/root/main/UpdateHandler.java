package root.main;

import custom.component.MyPolyline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component
public class UpdateHandler extends ScrollPane {

    @FXML
    public AnchorPane pane;

    @FXML
    public VBox vBox;

    private List<MyPolyline> myPolylineList = new ArrayList<>();

    private Double lineSpacing = 0d;

    private int numberOfChannels;

    @SneakyThrows
    public UpdateHandler() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Chart.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }

    public void setYVectors(List<Double>[] yVectors) {
        for (int i = 0; i < yVectors.length; i++) {
            myPolylineList.get(i).setYVector(yVectors[i]);
        }
    }

    public void update() {
        for (MyPolyline myPolyline : myPolylineList) {
            myPolyline.update();
        }
    }

    public void setLineSpacing(Double spacing) {
        this.lineSpacing = spacing;
        for (int i = 0; i < myPolylineList.size(); i++) {
            myPolylineList.get(i).setYPosition((i + 1) * spacing);
        }
    }


    private DataController dataController;

    public void pushNewData() {

    }

    public List<MyPolyline> getMyPolylineList() {
        return myPolylineList;
    }



    public void setNumberOfChannels(int numberOfChannels) {
        myPolylineList = new ArrayList<>();
        this.numberOfChannels = numberOfChannels;
        for (int i = 0; i < numberOfChannels; i++) {
            MyPolyline myPolyline = new MyPolyline(dataController, i + 1, vBox, this);
            myPolylineList.add(myPolyline);
        }
        setLineSpacing(lineSpacing);
    }


    public void setColors(List<Color> colors) {
        for (int i = 0; i < myPolylineList.size(); i++) {
            Color color = colors.get(i % colors.size());
            if (color != null)
                myPolylineList.get(i).getLineProperty().getStrokeProperty().setValue(color);
        }
    }

    public void setAmplitudes(Double amplitude) {
        for (MyPolyline myPolyline : myPolylineList) {
            myPolyline.setAmplitude(amplitude);
        }
    }

    public void setDataController(DataController dataController) {
        this.dataController = dataController;
    }

    public Double getLineSpacing() {
        return lineSpacing;
    }
}
