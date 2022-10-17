package root.main.fx.custom;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import lombok.Getter;

@Getter
public class MyLine extends Line {
    private final PositionProperty position;
    private final DoubleProperty xPosition;
    private final BooleanProperty shouldBeVisible;
    private final Pane parentPane;

    public MyLine(PositionProperty position, DoubleProperty endYProperty, DoubleProperty baseOffset, Pane parentPane, DoubleBinding pixelPerSample, Position firstVisible, Position lastVisible, IntegerProperty numberOfSamples) {
        super();
        this.position = position;
        this.parentPane = parentPane;
        shouldBeVisible = new SimpleBooleanProperty(false);
        xPosition = new SimpleDoubleProperty();
        NumberBinding offsetFromStart = (position.getPosition().getRecordProperty().subtract(firstVisible.getRecordProperty())).multiply(numberOfSamples)
                .add(position.getPosition().getOffsetProperty()).subtract(firstVisible.getOffsetProperty());
        xPosition.bind(baseOffset.add(offsetFromStart.multiply(pixelPerSample)));
        setStroke(Color.BLUE);
        startXProperty().bind(xPosition);
        endXProperty().bind(xPosition);
        startYProperty().set(0);
        endYProperty().bind(endYProperty);

        shouldBeVisible.addListener(this::shouldBeVisibleListener);
        shouldBeVisible.bind(position.greaterThanOrEqual(firstVisible).and(position.lessThanOrEqual(lastVisible)));

    }

    private void shouldBeVisibleListener(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        Platform.runLater(() -> {
            try {
                ObservableList<Node> children = parentPane.getChildren();
                    if (!oldValue && newValue && !children.contains(this)) {
                        children.add(this);
                    } else if (oldValue && !newValue) {
                        children.remove(this);
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
}
