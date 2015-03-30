package com.mattc.autotyper.gui.fx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.stage.Stage;

import com.google.common.collect.Lists;
import com.mattc.autotyper.meta.FXCompatible;
import com.mattc.autotyper.meta.FXParseable;
import com.mattc.autotyper.meta.InDev;

/**
 * A TextField that handles AutoCompletion
 * 
 * @author Matthew
 */
@FXCompatible
@FXParseable("%at")
@InDev(sinceVersion = 2.0, lastUpdate = 2.0, author = "Matthew Crocco")
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

		this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (AutoCompleteTextField.this.popup.isShowing()) {
					AutoCompleteTextField.this.popup.hide();
				}
			}

		});

		this.addEventHandler(EventType.ROOT, new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				if (event instanceof KeyEvent) {
					final KeyEvent evt = (KeyEvent) event;
					if ((evt.getCode() == KeyCode.SPACE) && evt.isControlDown()) {
						showPopup();
					} else if ((evt.getCode() == KeyCode.ESCAPE) && AutoCompleteTextField.this.popup.isShowing()) {
						AutoCompleteTextField.this.popup.hide();
					}
				}
			}

		});

		textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				final String text = newValue;

				if (!text.trim().isEmpty() && (getScene() != null)) {
					AutoCompleteTextField.this.listView.setItems(FXGuiUtils.selectCompletionCandidates(AutoCompleteTextField.this.data, text, true));
					if (!isPopupShowing() && AutoCompleteTextField.this.listView.getItems().size() > 0) {
						showPopup();
					}
				} else if (AutoCompleteTextField.this.popup.isShowing() && text.trim().isEmpty()) {
					AutoCompleteTextField.this.popup.hide();
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
		this.listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue == null) return;

				if (AutoCompleteTextField.this.caused.get()) {
					AutoCompleteTextField.this.caused.set(false);
					return;
				}

				setText(newValue);
				hidePopup();
				positionCaret(getText().length());
			}

		});

		this.listView.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (!AutoCompleteTextField.this.popup.isShowing()) return;

				final int index = AutoCompleteTextField.this.listView.getSelectionModel().getSelectedIndex();
				if (event.getCode() == KeyCode.UP) {
					AutoCompleteTextField.this.caused.set(true);
					if (index > 0) {
						AutoCompleteTextField.this.listView.getSelectionModel().select(index - 1);
					}
				} else if (event.getCode() == KeyCode.DOWN) {
					AutoCompleteTextField.this.caused.set(true);
					if (index < AutoCompleteTextField.this.listView.getItems().size()) {
						AutoCompleteTextField.this.listView.getSelectionModel().select(index + 1);
					}
				}

				if (event.getCode() == KeyCode.ENTER) {
					final String text = AutoCompleteTextField.this.listView.getSelectionModel().getSelectedItem();
					if ((text == null) || text.trim().isEmpty()) return;
					setText(text);
					hidePopup();
					positionCaret(getText().length());
				}
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
	public void setMaxLength(int max) {
		this.limit = max;
	}

	@Override
	public int getMaxLength() {
		return this.limit;
	}

	public boolean isPopupShowing() {
		return this.popup.isShowing();
	}

	public void hidePopup() {
		if (this.popup.isShowing()) {
			this.popup.hide();
		}
	}

	public void showPopup() {
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
}
