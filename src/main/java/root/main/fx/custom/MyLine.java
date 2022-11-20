package root.main.fx.custom;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import lombok.Getter;
import org.apache.commons.lang3.time.DurationFormatUtils;

@Getter
public class MyLine extends Line{
    private final PositionProperty positionProperty;
    private final DoubleProperty xPosition;
    private final BooleanProperty shouldBeVisible;
    private final Pane parentPane;
    private final Pane parentLabelPane;
    private final Label label;

    private ChangeListener<Boolean> shouldBeVisibleListener = this::shouldBeVisibleListener;

    public MyLine(PositionProperty positionProperty, DoubleProperty endYProperty, DoubleProperty baseOffset, Pane parentPane, Pane parentLabelPane, DoubleBinding pixelPerSample, Position firstVisible, Position lastVisible, IntegerProperty numberOfSamples, NumberBinding msProperty, ObjectProperty<Color> strokeProperty, DoubleProperty strokeWidthProperty) {
        super();
        this.positionProperty = positionProperty;
        this.parentPane = parentPane;
        this.parentLabelPane = parentLabelPane;
        this.label = new Label();

        msPropertyChange(null, null, msProperty.getValue());
        msProperty.addListener(this::msPropertyChange);


        shouldBeVisible = new SimpleBooleanProperty(false);
        xPosition = new SimpleDoubleProperty();
        NumberBinding offsetFromStart = (positionProperty.getPosition().getRecordProperty().subtract(firstVisible.getRecordProperty())).multiply(numberOfSamples)
                .add(positionProperty.getPosition().getOffsetProperty()).subtract(firstVisible.getOffsetProperty());
        xPosition.bind(baseOffset.add(offsetFromStart.multiply(pixelPerSample)));


        label.layoutXProperty().bind(xPosition.subtract(label.widthProperty().divide(2)));
        label.layoutYProperty().set(0);

        strokeProperty().bind(strokeProperty);
        strokeWidthProperty().bind(strokeWidthProperty);
        startXProperty().bind(xPosition);
        endXProperty().bind(xPosition);
        startYProperty().set(0);
        endYProperty().bind(endYProperty);

        shouldBeVisible.addListener(shouldBeVisibleListener);
        shouldBeVisible.bind(positionProperty.greaterThanOrEqual(firstVisible).and(positionProperty.lessThanOrEqual(lastVisible)));

    }

    private void shouldBeVisibleListener(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        Platform.runLater(() -> {
            try {
                ObservableList<Node> children = parentPane.getChildren();
                    if (!oldValue && newValue && children.stream().noneMatch(node -> node.getClass().equals(MyLine.class) && ((MyLine)node).positionProperty.getPosition().equals(positionProperty.getPosition()))) {
                        children.add(this);
                        parentLabelPane.getChildren().add(label);
                    } else if (oldValue && !newValue) {
                        children.remove(this);
                        parentLabelPane.getChildren().remove(label);
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    private void msPropertyChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        String formatStr;
        long labelMs = newValue.longValue();
        if (labelMs < 60000) {
            formatStr = "s";
        } else if (labelMs < 3600000) {
            formatStr = "m:s";
        } else {
            formatStr = "H:m:s";
        }
        if (labelMs % 1000 != 0) {
            formatStr += ":SSS";
        }
        label.setText(DurationFormatUtils.formatDuration(labelMs, formatStr));
    }

    public void remove() {
        parentPane.getChildren().remove(this);
        parentLabelPane.getChildren().remove(label);
        shouldBeVisible.removeListener(shouldBeVisibleListener);
    }
}
