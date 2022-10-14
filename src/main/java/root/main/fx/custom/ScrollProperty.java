package root.main.fx.custom;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ObservableValue;
import lombok.Getter;

@Getter
public class ScrollProperty extends PositionProperty {
    private DoubleProperty doubleProperty;

    private Boolean recursive = false;
    private Runnable onChange;

    public ScrollProperty(IntegerProperty recordMax, IntegerProperty offsetMax, Runnable onChange) {
        super(recordMax, offsetMax);
        this.onChange = onChange;

        position.getRecordProperty().addListener(this::positionChange);
        position.getOffsetProperty().addListener(this::positionChange);
    }

    public void setDoubleProperty(DoubleProperty property) {
        doubleProperty = property;
        doubleProperty.addListener(this::doublePropertyChange);
    }

    private void positionChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (!recursive) {
            recursive = true;
            doubleProperty.setValue(position.getRecordProperty().get() + position.getOffsetProperty().get() * (1 / ((MinMaxIntegerProperty) position.getOffsetProperty()).getMax().get()));
        } else {
            recursive = false;
        }
        Platform.runLater(onChange);
    }


    private void doublePropertyChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (!recursive) {
            recursive = true;
            position.getRecordProperty().set(newValue.intValue());
            position.getOffsetProperty().set((int) (((newValue.doubleValue() - newValue.intValue())
                    / (1d / (double) ((MinMaxIntegerProperty) position.getOffsetProperty()).getMax().get())))
            );
        } else {
            recursive = false;
        }
    }
}
