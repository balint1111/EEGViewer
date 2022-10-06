package root.main;

import custom.component.Modes;
import org.springframework.stereotype.Component;
import root.main.UpdateHandlerController;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@Component
public class KeyboardListener implements KeyEventDispatcher {
    private final UpdateHandlerController updateHandlerController;

    public KeyboardListener(UpdateHandlerController updateHandlerController) {
        this.updateHandlerController = updateHandlerController;
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
            }
        }

        return false;
    }
}
