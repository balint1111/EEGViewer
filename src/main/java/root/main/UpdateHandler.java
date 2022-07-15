package root.main;

import custom.component.MyPolyline;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;


public class UpdateHandler {

    private VBox vBox;

    private static UpdateHandler instance;

    private List<MyPolyline> myPolylineList = new ArrayList<>();

    private Double lineSpacing = 0d;

    private int numberOfChannels;

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


    private UpdateHandler(VBox group) {
        this.vBox = group;
    }

    public List<MyPolyline> getMyPolylineList() {
        return myPolylineList;
    }

    public static UpdateHandler get(VBox group) throws Exception {
        if (instance == null) {
            instance = new UpdateHandler(group);
        }
        return instance;
    }

    public static UpdateHandler get() throws Exception {
        if (instance == null)
            throw new Exception("NO_INSTANCE_OF_UPDATE_HANDLER");
        return instance;
    }

    public void setNumberOfChannels(int numberOfChannels) {
        myPolylineList = new ArrayList<>();
        this.numberOfChannels = numberOfChannels;
        for (int i = 0; i < numberOfChannels; i++) {
            MyPolyline myPolyline = new MyPolyline(dataController, i + 1 , vBox);
            myPolylineList.add(myPolyline);
        }
        vBox.getChildren().setAll(myPolylineList);
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
