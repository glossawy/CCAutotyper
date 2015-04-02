package com.mattc.autotyper.gui.fx;

import com.google.common.collect.Lists;
import com.mattc.autotyper.meta.FXCompatible;
import com.mattc.autotyper.meta.FXParseable;
import com.mattc.autotyper.util.Console;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

// TODO Consider transferring to ControlsFX for AutoCompletion or re-working this framework to be more useful
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
		this(new ArrayList<String>());
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
                    if(!AutoCompleteTextField.this.isPopupShowing()) {
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

        textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                final String text = newValue;

                if (!text.trim().isEmpty() && (getScene() != null)) {
                    organizeData(text);
                    if (!isPopupShowing() && AutoCompleteTextField.this.listView.getItems().size() > 0) {
                        showPopup();
                    }
                } else if (AutoCompleteTextField.this.popup.isShowing() && text.trim().isEmpty()) {
					AutoCompleteTextField.this.hidePopup();
				}
			}

		});

		focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				final boolean isFocused = newValue.booleanValue();

				if (!isFocused && AutoCompleteTextField.this.popup.isShowing()) {
					hidePopup();
				}
			}

		});

		widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AutoCompleteTextField.this.listView.setPrefWidth(newValue.doubleValue());
            }
        });

		this.listView.setPrefHeight(200);
		this.listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Mouse Click Select Item
        this.listView.setOnMouseClicked((e) -> {
            if(e.getButton() == MouseButton.PRIMARY)
                setTextToSelection();
        });

		this.listView.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
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
            }

        });

	}

	@SafeVarargs
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

        if(count == 0)
            return;

		this.listView.getSelectionModel().clearSelection();
		final Point2D origin = FXGuiUtils.getScreenCoordinates(AutoCompleteTextField.this);
		this.listView.setItems(FXGuiUtils.selectCompletionCandidates(AutoCompleteTextField.this.data, getText(), true));
		this.popup.show(AutoCompleteTextField.this, origin.getX(), origin.getY() + getHeight());
	}

	public void installXYChangeListeners(Stage stage) {
		stage.xProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				final double dX = newValue.doubleValue() - oldValue.doubleValue();
				AutoCompleteTextField.this.popup.setX(AutoCompleteTextField.this.popup.getX() + dX);
			}

		});

		stage.yProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				final double dY = newValue.doubleValue() - oldValue.doubleValue();
				AutoCompleteTextField.this.popup.setY(AutoCompleteTextField.this.popup.getY() + dY);
			}

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
        ObservableList<String> selected = FXGuiUtils.selectCompletionCandidates(AutoCompleteTextField.this.data, text, true);
        FXCollections.sort(selected);
        AutoCompleteTextField.this.listView.setItems(selected);
    }
}
