package root.main.fx.custom;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Getter;

@Getter
public class ScrollProperty {
    private MinMaxIntegerProperty recordProperty;
    private MinMaxIntegerProperty offsetProperty;
    private DoubleProperty doubleProperty;

    private Boolean recursive = false;

    public ScrollProperty(IntegerProperty recordMax, IntegerProperty offsetMax, Runnable onChange) {
        super();
        SimpleIntegerProperty zeroProp = new SimpleIntegerProperty(0);
        recordProperty = new MinMaxIntegerProperty(recordMax, zeroProp);
        offsetProperty = new MinMaxIntegerProperty(offsetMax, zeroProp);

        recordProperty.addListener((observable, oldValue, newValue) -> {
            synchronized (recursive) {
                if (!recursive) {
                    recursive = true;
                    doubleProperty.setValue(recordProperty.get() + offsetProperty.get() * (1 / offsetProperty.getMax().get()));
                } else {
                    recursive = false;
                }
                Platform.runLater(onChange::run);
            }
        });
        offsetProperty.addListener((observable, oldValue, newValue) -> {
            synchronized (recursive) {
                if (!recursive) {
                    recursive = true;
                    doubleProperty.setValue(recordProperty.get() + offsetProperty.get() * (1 / offsetProperty.getMax().get()));
                } else {
                    recursive = false;
                }
                Platform.runLater(onChange);
            }
        });

    }

    public void setDoubleProperty(DoubleProperty property) {
        doubleProperty = property;
        doubleProperty.addListener((observable, oldValue, newValue) -> {
            synchronized (recursive) {
                if (!recursive) {
                    recursive = true;
                    recordProperty.set(newValue.intValue());
                    offsetProperty.set((int) (((newValue.doubleValue() - newValue.intValue())
                            / (1d / (double) offsetProperty.getMax().get())))
                    );
                } else {
                    recursive = false;
                }
            }
        });
    }


}
