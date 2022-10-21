package root.main.common;

import javafx.application.Platform;
import org.springframework.stereotype.Component;
import root.main.General;
import root.main.fx.UpdateHandlerController;
import root.main.common.enums.Modes;
import root.main.fx.custom.MinMaxIntegerProperty;

import java.awt.*;
import java.awt.event.KeyEvent;

@Component
public class KeyboardListener implements KeyEventDispatcher {
    private final UpdateHandlerController updateHandlerController;
    private final General general;

    public KeyboardListener(UpdateHandlerController updateHandlerController, General general) {
        this.updateHandlerController = updateHandlerController;
        this.general = general;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        Platform.runLater(() -> {
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                if (e.getKeyCode() == KeyEvent.VK_B) {
                    if (updateHandlerController.getModeProperty().get().equals(Modes.BUTTERFLY))
                        updateHandlerController.getModeProperty().set(Modes.NORMAL);
                    else if (updateHandlerController.getModeProperty().get().equals(Modes.NORMAL))
                        updateHandlerController.getModeProperty().set(Modes.BUTTERFLY);
                }
            } else if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    updateHandlerController.getAmplitudeProperty().set(updateHandlerController.getAmplitudeProperty().get() + 0.01);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    updateHandlerController.getAmplitudeProperty().set(updateHandlerController.getAmplitudeProperty().get() - 0.01);
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if ((general.getScrollBarValue().getPosition().getOffsetProperty().get() + 1) >= ((MinMaxIntegerProperty) general.getScrollBarValue().getPosition().getOffsetProperty()).getMax().get()) {
                        general.getScrollBarValue().getPosition().getOffsetProperty().set(0);
                        general.getScrollBarValue().getPosition().getRecordProperty().set(general.getScrollBarValue().getPosition().getRecordProperty().get() + 1);
                    } else {
                        general.getScrollBarValue().getPosition().getOffsetProperty().set(general.getScrollBarValue().getPosition().getOffsetProperty().get() + 1);
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if ((general.getScrollBarValue().getPosition().getOffsetProperty().get() - 1) < 0) {
                        general.getScrollBarValue().getPosition().getOffsetProperty().set(((MinMaxIntegerProperty) general.getScrollBarValue().getPosition().getOffsetProperty()).getMax().get() - 1);
                        general.getScrollBarValue().getPosition().getRecordProperty().set(general.getScrollBarValue().getPosition().getRecordProperty().get() - 1);
                    } else {
                        general.getScrollBarValue().getPosition().getOffsetProperty().set(general.getScrollBarValue().getPosition().getOffsetProperty().get() - 1);
                    }
                }
            }
        });


        return false;
    }
}
