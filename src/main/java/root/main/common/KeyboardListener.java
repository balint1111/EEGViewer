package root.main.common;

import org.springframework.stereotype.Component;
import root.main.General;
import root.main.fx.UpdateHandlerController;
import root.main.common.enums.Modes;

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
                if ((general.getScrollBarValue().getOffsetProperty().get() + 1) >= general.getScrollBarValue().getOffsetProperty().getMax().get()) {
                    general.getScrollBarValue().getOffsetProperty().set(0);
                    general.getScrollBarValue().getRecordProperty().set(general.getScrollBarValue().getRecordProperty().get() + 1);
                } else {
                    general.getScrollBarValue().getOffsetProperty().set(general.getScrollBarValue().getOffsetProperty().get() + 1);
                }
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                if ((general.getScrollBarValue().getOffsetProperty().get() - 1) < 0) {
                    general.getScrollBarValue().getOffsetProperty().set(general.getScrollBarValue().getOffsetProperty().getMax().get() - 1);
                    general.getScrollBarValue().getRecordProperty().set(general.getScrollBarValue().getRecordProperty().get() - 1);
                } else {
                    general.getScrollBarValue().getOffsetProperty().set(general.getScrollBarValue().getOffsetProperty().get() - 1);
                }
            }
        }

        return false;
    }
}
