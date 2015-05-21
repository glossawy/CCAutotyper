package com.mattc.autotyper.gui.fx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Hyperlink;
import com.mattc.autotyper.gui.GuiAccessor;
import com.mattc.autotyper.util.Console;

/**
 * A Convenience class for more concise creation of Hyperlinks. This quickly creates the
 * Display Name, underlying URL link ("href") and sets the OnAction Event for the given {@link GuiAccessor}
 * to use {@link GuiAccessor#openSite(String)}. <br />
 * <br />
 * The name is changeable using the standard {@link Hyperlink#setText(String)} method. The URL and
 * GuiAccessor associated with this WebLink can be modified using the {@link #urlProperty()} and
 * {@link #guiProperty()} methods. Both of which return SimpleProperty's which are mutable.
 */
public class WebLink extends Hyperlink {

    private final StringProperty url;
    private final ObjectProperty<GuiAccessor> access;

    public WebLink(String name, String url, GuiAccessor accessor) {
        super(name);

        this.url = new SimpleStringProperty(url);
        this.access = new SimpleObjectProperty<>(accessor);

        setOnAction((e) -> {
            try {
                this.access.get().openSite(this.url.get());
            } catch (Exception ex) {
                Console.exception(ex);
            }
        });
    }

    public WebLink(String url, GuiAccessor accessor) {
        this(url, url, accessor);
    }

    public final StringProperty urlProperty() {
        return url;
    }

    public final ObjectProperty<GuiAccessor> guiProperty() {
        return access;
    }

}
