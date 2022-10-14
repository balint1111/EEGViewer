package root.main.fx.custom;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Getter;

@Getter
public class PositionProperty {
    protected Position position;

    public PositionProperty(IntegerProperty recordMax, IntegerProperty offsetMax) {
        SimpleIntegerProperty zeroProp = new SimpleIntegerProperty(0);
        this.position = new Position(new MinMaxIntegerProperty(recordMax, zeroProp), new MinMaxIntegerProperty(offsetMax, zeroProp));
    }

    public PositionProperty(Position position) {
        this.position = position;
    }

    public void set(Position position) {
        position.setRecordProperty(position.getRecordProperty());
        position.setOffsetProperty(position.getOffsetProperty());
    }

    public Position get() {
        return new Position(position.getRecordProperty().get(), position.getOffsetProperty().get());
    }

    public BooleanBinding lessThan(Position other) {
        return position.getRecordProperty().lessThan(other.getRecordProperty())
                .or(position.getOffsetProperty().lessThan(other.getOffsetProperty()).and(position.getRecordProperty().isEqualTo(other.getRecordProperty())));
    }

    public BooleanBinding greaterThan(Position other) {
        return position.getRecordProperty().greaterThan(other.getRecordProperty())
                .or(position.getOffsetProperty().greaterThan(other.getOffsetProperty()).and(position.getRecordProperty().isEqualTo(other.getRecordProperty())));
    }

    public BooleanBinding greaterThanOrEqual(Position other) {
        return lessThan(other).not();
    }

    public BooleanBinding lessThanOrEqual(Position other) {
        return greaterThan(other).not();
    }

}
