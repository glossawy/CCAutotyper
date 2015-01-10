package com.mattc.autotyper.gui.fx;

import java.util.Comparator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

public class FXGuiUtils {

	public static final void setToggleTextSwitch(final ToggleButton btn, final String onText, final String offText) {

		btn.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (btn.isSelected()) {
					btn.setText(onText);
				} else {
					btn.setText(offText);
				}
			}

		});

		btn.fireEvent(new ActionEvent());
	}

	public static final void setMaxCharCount(final TextInputControl control, final int count) {
		control.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				final String text = control.getText();
				if (text.length() == count) {
					event.consume();
				} else if (text.length() > count) {
					control.setText(text.substring(0, count));
					event.consume();
				}
			}

		});
	}

	public static final boolean addTogglesToGroup(ToggleGroup group, Toggle... toggles) {
		return group.getToggles().addAll(toggles);
	}

	public static final boolean addTogglesToGroup(MetaToggleGroup group, Toggle... toggles) {
		for (final Toggle t : toggles) {
			group.add(t, "");
		}

		return true;
	}

	public static final int getPointer(Object[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == null) return i;
		}

		return arr.length;
	}

	public static ObservableList<String> selectCompletionCandidates(final ObservableList<String> data, String base, boolean sort) {
		ObservableList<StringWrapper> wrappers = FXCollections.observableArrayList();
		final ObservableList<String> candidates = FXCollections.observableArrayList();

		for (final String s : data) {
			wrappers.add(new StringWrapper(s));
		}

		wrappers = selectCompletionCandidates(wrappers, new StringWrapper(base), sort);

		for (final StringWrapper wrapper : wrappers) {
			candidates.add(wrapper.toString());
		}

		return candidates;
	}

	public static <T extends AutoCompleteObject<T>> ObservableList<T> selectCompletionCandidates(final ObservableList<T> data, final T base, final boolean sort) {
		final ObservableList<T> candidates = FXCollections.observableArrayList();

		for (final T obj : data)
			if ((obj != null) && base.isValidCandidate(obj)) {
				candidates.add(obj);
			}

		if (sort) {
			FXCollections.sort(candidates, new Comparator<T>() {
				@Override
				public int compare(T first, T second) {
					return first.compareTo(second);
				}
			});
		}

		return candidates;
	}

	public static final Point2D getScreenCoordinates(Node node) {

		final double x = node.getScene().getWindow().getX();
		final double y = node.getScene().getWindow().getY();

		final Bounds localBounds = node.localToScene(node.getBoundsInLocal());

		return new Point2D(x + localBounds.getMinX(), y + localBounds.getMaxY());
	}

	private static class StringWrapper implements AutoCompleteObject<StringWrapper> {

		private final String str;

		private StringWrapper(String str) {
			this.str = str;
		}

		@Override
		public boolean isValidCandidate(StringWrapper base) {
			return base.str.startsWith(this.str);
		}

		@Override
		public int compareTo(StringWrapper other) {
			return this.str.compareTo(other.str);
		}

		@Override
		public String toString() {
			return this.str;
		}

	}

	public static boolean canUseJavaFX() {
		try {
			return Class.forName("javafx.application.Application") != null;
		} catch (final ClassNotFoundException e) {
			return false;
		}
	}

	public static boolean isFXRobotAvailable() {
		try {
			return Class.forName("com.sun.glass.ui.Robot") != null;
		} catch (final ClassNotFoundException e) {
			return false;
		}
	}

	public static void makeAlwaysOnTop(final Window window) {
		window.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!newValue.booleanValue()) {
					window.requestFocus();
				}
			}

		});
	}

	public static void makeAlwaysOnTop(final Stage stage) {
		stage.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!newValue.booleanValue()) {
					stage.requestFocus();
					stage.toFront();
				}
			}

		});
	}

}
