package com.mattc.autotyper.robot;

import static java.awt.event.KeyEvent.VK_BACK_SPACE;

import com.google.common.collect.Queues;
import com.mattc.autotyper.util.Console;
import com.mattc.autotyper.util.IOUtils;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.File;
import java.io.IOException;
import java.util.Queue;

/**
 * Base Implementation for {@link Methodology} objects, which define how a {@link Keyboard} should
 * handle input. <br />
 * <br />
 * The benefit of extending BaseMethodology include a working implementation of {@link NativeKeyListener}
 * which can also pause input and handle key combinations based on alt, particularly (alt-S and alt-p for stop and play
 * respectively though this will be expanded in the future to allow arbitrary alt-combination or even just
 * arbitrary combinations), and a schedule system that handles communication between the NativeKey Thread
 * and the Main Thread.
 *
 * @author Glossawy
 *         Created 4/3/2015 at 4:53 PM
 */
public abstract class BaseMethodology implements Methodology {

    // This queue is maintained for key presses that must be communicated to the Native Key Thread
    private final Queue<Integer> schedule = Queues.newConcurrentLinkedQueue();

    private volatile Keyboard.KeyboardMode mode = Keyboard.KeyboardMode.INACTIVE;
    private volatile boolean alt = false;
    private volatile boolean bspace = false;
    private volatile boolean keypressed = false;

    @Override
    public abstract void type(char c);

    @Override
    public abstract void typeLine(String line);

    @Override
    public abstract void typeFile(File file) throws IOException;

    @Override
    public Keyboard.KeyboardMode mode() {
        return mode;
    }

    /**
     * @return If Current KeyboardMode == KeyboardMode.ACTIVE
     */
    public boolean isActive() {
        return mode == Keyboard.KeyboardMode.ACTIVE;
    }

    /**
     * @return If Current KeyboardMode == KeyboardMode.PAUSED
     */
    public boolean isPaused() {
        return mode == Keyboard.KeyboardMode.PAUSED;
    }

    /**
     * @return If Current KeyboardMode == KeyboardMode.INACTIVE
     */
    public boolean isInactive() {
        return mode == Keyboard.KeyboardMode.INACTIVE;
    }

    /**
     * Will return true if it is determined that the <em>Left</em> Alt Key is down.
     *
     * @return true if Left alt is down
     */
    public boolean isAltDown() {
        return alt;
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // Ignore if alt is not pressed, it's all we care about.
        if (!this.alt) return;

        String log = this.mode.name() + "...";
        if (this.alt && (e.getKeyChar() == 'p')) {
            // Toggle for Alt + P
            switch (this.mode) {
                case ACTIVE:
                    this.mode = Keyboard.KeyboardMode.PAUSED;
                    break;
                case PAUSED:
                    this.mode = Keyboard.KeyboardMode.ACTIVE;
                    break;
                default:
                    break;
            }
        } else if (this.alt && (e.getKeyChar() == 's')) {
            if (this.mode == Keyboard.KeyboardMode.PAUSED)
                schedule.add(VK_BACK_SPACE);

            // Terminate Current Session for Alt + S
            this.mode = Keyboard.KeyboardMode.INACTIVE;
            schedule.add(VK_BACK_SPACE);
            this.alt = false;
        }

        log = "Keyboard set to " + this.mode.name() + " from " + log;
        Console.debug(log);
        IOUtils.sleep(20);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // If Keyboard is Active and Left Alt is released
        if ((e.getKeyCode() == NativeKeyEvent.VC_ALT_L) && (this.mode == Keyboard.KeyboardMode.ACTIVE)) {
            this.alt = false;

            // Delete the 1 or 2 stray characters
            // Alt + P will print P in computer craft, this deletes the P
            // if the user did not.
            if (!this.bspace && this.keypressed) {
                schedule.add(VK_BACK_SPACE);
            } else {
                this.bspace = false;
            }

            if (this.keypressed)
                schedule.add(VK_BACK_SPACE);
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        // If Left Alt is Pressed, set the Alt Flag to true.
        if (e.getKeyCode() == NativeKeyEvent.VC_ALT_L) {
            this.alt = true;
        } else if (this.alt && (e.getKeyCode() == NativeKeyEvent.VC_BACKSPACE)) {
            this.bspace = true;
        } else if (this.alt && (e.getKeyCode() == NativeKeyEvent.VC_P || e.getKeyCode() == NativeKeyEvent.VC_S)) {
            this.keypressed = true;
        }
    }

    /*
        Returns true if the scheduling queue is empty, false otherwise (essentially a call to queue.isEmpty)
     */
    protected boolean isScheduleEmpty() {
        return schedule.isEmpty();
    }

    /*
        Returns the next key in the schedule, if any.
     */
    protected int nextScheduleKey() {
        return schedule.remove();
    }

    /*
        Clears the current scheduling queue.
     */
    protected void clearSchedule() {
        schedule.clear();
    }

    protected void start() {
        setMode(Keyboard.KeyboardMode.ACTIVE);
    }

    protected void pause() {
        setMode(Keyboard.KeyboardMode.PAUSED);
    }

    protected void end() {
        setMode(Keyboard.KeyboardMode.INACTIVE);
    }

    protected void setMode(Keyboard.KeyboardMode mode) {
        this.mode = mode;
    }
}
