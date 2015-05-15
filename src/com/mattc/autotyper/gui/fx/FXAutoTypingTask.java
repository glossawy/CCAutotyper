package com.mattc.autotyper.gui.fx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.concurrent.Task;
import com.mattc.autotyper.robot.Keyboard;
import com.mattc.autotyper.util.IOUtils;

import java.nio.file.Path;

/**
 * Needs Documentation
 *
 * @author Matthew
 *         Created 3/29/2015 at 11:34 PM
 */
public class FXAutoTypingTask extends Task<Boolean> {

    private final Keyboard keys;
    private final ObjectProperty<Path> file;
    private final IntegerProperty wait, inputDelay;
    private final Runnable prestart;

    public FXAutoTypingTask(Keyboard keys, ObjectProperty<Path> fileProperty, IntegerProperty wait, IntegerProperty inputDelay, Runnable prestart) {
        this.keys = keys;
        this.file = fileProperty;
        this.wait = wait;
        this.inputDelay = inputDelay;
        this.prestart = prestart;
    }

    @Override
    protected Boolean call() throws Exception {
        prestart.run();
        keys.setInputDelay(inputDelay.get());
        IOUtils.sleep(wait.get());
        keys.typeFile(file.get().toFile());
        return true;
    }
}
