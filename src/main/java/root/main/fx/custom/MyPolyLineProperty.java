package root.main.fx.custom;

import javafx.beans.property.*;
import javafx.scene.paint.Paint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyPolyLineProperty {
    private ObjectProperty<Paint> strokeProperty;
    private DoubleProperty amplitude;
    private BooleanProperty buttonDisable;
    private IntegerProperty horizontalResolution;
}
