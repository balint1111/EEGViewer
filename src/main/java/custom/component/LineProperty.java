package custom.component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Paint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineProperty {
    private ObjectProperty<Paint> strokeProperty;
    private DoubleProperty amplitude = new SimpleDoubleProperty(0);
    private BooleanProperty buttonDisable;
}
