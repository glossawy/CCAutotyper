package com.mattc.autotyper.gui.fx;

import com.mattc.autotyper.meta.FXCompatible;
import com.mattc.autotyper.meta.SwingCompatible;

/**
 * An interface for properly comparing objects used in Auto Completion data. Strings
 * are wrapped in {@link FXGuiUtils} as a separate String-like AutoCompleteObject.
 *
 * @param <T>
 * @author Matthew
 */
@FXCompatible
@SwingCompatible
public interface AutoCompleteObject<T extends AutoCompleteObject<T>> extends Comparable<T> {

    boolean isValidCandidate(T base);

    @Override
    int compareTo(T other);
}
