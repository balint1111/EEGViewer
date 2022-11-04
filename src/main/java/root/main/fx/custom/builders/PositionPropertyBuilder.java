package root.main.fx.custom.builders;

import javafx.beans.binding.NumberBinding;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import root.main.General;
import root.main.fx.custom.Position;
import root.main.fx.custom.PositionProperty;
import root.main.fx.custom.PropertyUtils;

@Component
@Slf4j
public class PositionPropertyBuilder {
    private final General general;

    public PositionPropertyBuilder(General general) {
        this.general = general;
    }

    public PositionProperty build(Position position) {
        return new PositionProperty(position);
    }

    public Position relative(Position other, Position relativePosition) {
        NumberBinding relativeRecordNumberBinding = other.getRecordProperty().add(relativePosition.getRecordProperty());
        NumberBinding relativeOffsetNumberBinding = other.getOffsetProperty().add(relativePosition.getOffsetProperty());
        Position newProperty = new Position(PropertyUtils.toIntegerProperty(relativeRecordNumberBinding), PropertyUtils.toIntegerProperty(relativeOffsetNumberBinding));
        return newProperty;
    }
}
