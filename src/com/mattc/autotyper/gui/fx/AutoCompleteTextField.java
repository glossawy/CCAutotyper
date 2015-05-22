package com.mattc.autotyper.gui.fx;

import static com.mattc.autotyper.gui.fx.AutoCompleteUtils.selectCompletionCandidates;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.stage.Stage;
import com.google.common.collect.Lists;
import com.mattc.autotyper.meta.FXCompatible;
import com.mattc.autotyper.meta.FXParseable;
import com.mattc.autotyper.util.Console;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A TextField that handles AutoCompletion
 *
 * @author Matthew
 */
@FXCompatible
@FXParseable("%at")
public class AutoCompleteTextField extends TextField implements AutoCompleteControl<String> {

    private final AtomicBoolean caused = new AtomicBoolean(false);
    private final ObservableList<String> data;
    private final ListView<String> listView;
    private final Popup popup;
    private int limit = 5;

    @SuppressWarnings("unused")
    private AutoCompleteTextField() {
        this(Lists.newArrayList());
    }

    public AutoCompleteTextField(List<String> data) {
        super();

        this.data = FXCollections.observableList(data);
        this.listView = new ListView<>(this.data);
        this.popup = new Popup();
        this.popup.getContent().add(this.listView);

        this.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            if (AutoCompleteTextField.this.popup.isShowing()) {
                AutoCompleteTextField.this.hidePopup();
            }
        });

        this.addEventFilter(EventType.ROOT, (event) -> {
            if (event instanceof KeyEvent) {
                final KeyEvent evt = (KeyEvent) event;
                if ((evt.getCode() == KeyCode.SPACE) && evt.isControlDown()) {
                    if (!AutoCompleteTextField.this.isPopupShowing()) {
                        organizeData(getText().trim());
                        showPopup();
                    }

                    setSelection(0);
                } else if ((evt.getCode() == KeyCode.ESCAPE) && AutoCompleteTextField.this.popup.isShowing()) {
                    AutoCompleteTextField.this.hidePopup();
                    evt.consume();
                }
            }
        });

        textProperty().addListener((obs, oldTest, newText) -> {
            if (!newText.trim().isEmpty() && (getScene() != null)) {
                organizeData(newText);
                if (!isPopupShowing() && AutoCompleteTextField.this.listView.getItems().size() > 0) {
                    showPopup();
                }
            } else if (AutoCompleteTextField.this.popup.isShowing() && newText.trim().isEmpty()) {
                AutoCompleteTextField.this.hidePopup();
            }
        });

        focusedProperty().addListener((obs, oldValue, newValue) -> {
            final boolean isFocused = newValue;

            if (!isFocused && AutoCompleteTextField.this.popup.isShowing()) {
                hidePopup();
            }
        });

        widthProperty().addListener((obs, oldValue, newValue) -> AutoCompleteTextField.this.listView.setPrefWidth(newValue.doubleValue()));

        this.listView.setPrefHeight(200);
        this.listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Mouse Click Select Item
        this.listView.setOnMouseClicked((e) -> {
            if (e.getButton() == MouseButton.PRIMARY)
                setTextToSelection();
        });

        this.listView.setOnKeyPressed((event) -> {
            if (!AutoCompleteTextField.this.popup.isShowing()) return;

            final int index = AutoCompleteTextField.this.listView.getSelectionModel().getSelectedIndex();
            if (event.getCode() == KeyCode.UP) {
                setSelection(index - 1);
                event.consume();
            } else if (event.getCode() == KeyCode.DOWN) {
                setSelection(index + 1);
                event.consume();
            }

            if (event.getCode() == KeyCode.ENTER)
                setTextToSelection();
        });

    }

    public AutoCompleteTextField(String... data) {
        this(Lists.newArrayList(data));
    }

    @Override
    public void setData(List<String> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    public void addData(String data) {
        this.data.add(data);
    }

    @Override
    public ObservableList<String> getData() {
        return this.data;
    }

    @Override
    public ListView<String> getListView() {
        return this.listView;
    }

    @Override
    public void setMaxResults(int max) {
        this.limit = max;
    }

    @Override
    public int getMaxResults() {
        return this.limit;
    }

    public boolean isPopupShowing() {
        return this.popup.isShowing();
    }

    public void hidePopup() {
        if (this.popup.isShowing()) {
            this.popup.hide();
            this.listView.getItems().clear();
        }
    }

    public void showPopup() {
        int count = this.listView.getItems().size();

        Console.debug("Popup Request: AutoComplete Items = " + count + ", Will Show Popup? = " + (count != 0));

        if (count == 0)
            return;

        this.listView.getSelectionModel().clearSelection();
        final Point2D origin = FXGuiUtils.getScreenCoordinates(AutoCompleteTextField.this);
        this.listView.setItems(selectCompletionCandidates(AutoCompleteTextField.this.data, getText(), true));
        this.popup.show(AutoCompleteTextField.this, origin.getX(), origin.getY() + getHeight());
    }

    /**
     * Installs listeners that ensure the Popup will follow the Stage as it is moved.
     *
     * @param stage Stage
     */
    public void installXYChangeListeners(Stage stage) {
        stage.xProperty().addListener((obs, oldValue, newValue) -> {
            final double dX = newValue.doubleValue() - oldValue.doubleValue();
            AutoCompleteTextField.this.popup.setX(AutoCompleteTextField.this.popup.getX() + dX);
        });

        stage.yProperty().addListener((obs, oldValue, newValue) -> {
            final double dY = newValue.doubleValue() - oldValue.doubleValue();
            AutoCompleteTextField.this.popup.setY(AutoCompleteTextField.this.popup.getY() + dY);
        });
    }

    private void setSelection(int index) {
        AutoCompleteTextField.this.caused.set(true);
        if (index >= 0 && index < AutoCompleteTextField.this.listView.getItems().size()) {
            AutoCompleteTextField.this.listView.getSelectionModel().select(index);
        }
    }

    private void setTextToSelection() {
        final String text = AutoCompleteTextField.this.listView.getSelectionModel().getSelectedItem();
        if ((text == null) || text.trim().isEmpty()) return;
        setText(text);
        hidePopup();
        positionCaret(getText().length());
    }

    private void organizeData(String text) {
        ObservableList<String> selected = selectCompletionCandidates(AutoCompleteTextField.this.data, text, true);
        FXCollections.sort(selected);
        AutoCompleteTextField.this.listView.setItems(selected);
    }
}
