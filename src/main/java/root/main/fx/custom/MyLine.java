package root.main.fx.custom;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class MyLine extends Line {
    private final PositionProperty position;
    private final DoubleProperty xPosition;
    private final BooleanProperty shouldBeVisible;
    private final Pane parent;

    public MyLine(PositionProperty position, DoubleProperty endYProperty, DoubleProperty baseOffset, Pane parent, DoubleBinding pixelPerSample, Position firstVisible, Position lastVisible, IntegerProperty numberOfSamples) {
        super();
        this.position = position;
        this.parent = parent;
        shouldBeVisible = new SimpleBooleanProperty(false);
        shouldBeVisible.addListener(this::shouldBeVisibleListener);
        shouldBeVisible.bind(position.greaterThanOrEqual(firstVisible).and(position.lessThanOrEqual(lastVisible)));

        xPosition = new SimpleDoubleProperty();
        NumberBinding offsetFromStart = (position.getPosition().getRecordProperty().subtract(firstVisible.getRecordProperty())).multiply(numberOfSamples)
                .add(position.getPosition().getOffsetProperty()).subtract(firstVisible.getOffsetProperty());
        xPosition.bind(baseOffset.add(offsetFromStart.multiply(pixelPerSample)));
        setStroke(Color.BLUE);
        startXProperty().bind(xPosition);
        endXProperty().bind(xPosition);
        startYProperty().set(0);
        endYProperty().bind(endYProperty);
    }

    private void shouldBeVisibleListener(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (!oldValue && newValue) {
            Platform.runLater(() -> {
                synchronized (parent) {
                    parent.getChildren().add(this);
                }
            });
        } else if (oldValue && !newValue) {
            Platform.runLater(() -> {
                synchronized (parent) {
                    parent.getChildren().remove(this);
                }
            });
        }
    }
}
