package com.mattc.autotyper.gui.fx;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Tooltip;

public class TimedTooltip extends Tooltip {

    private final DoubleProperty delayProperty;

    public TimedTooltip(String text, double delayMillis) {
        super(text);

        delayProperty = new SimpleDoubleProperty();
        delayProperty.addListener((obs, old, nv) -> FXGuiUtils.setTooltipDelay(TimedTooltip.this, nv.doubleValue(), nv.doubleValue(), 5000));

        delayProperty.set(delayMillis);
    }

    public DoubleProperty delayProperty() {
        return delayProperty;
    }

}
