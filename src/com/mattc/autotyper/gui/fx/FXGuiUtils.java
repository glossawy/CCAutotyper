package com.mattc.autotyper.gui.fx;

import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;

public class FXGuiUtils {

    public static void setToggleTextSwitch(final ToggleButton btn, final String onText, final String offText) {

        btn.addEventHandler(ActionEvent.ACTION, (event) -> {
            if (btn.isSelected()) {
                btn.setText(onText);
            } else {
                btn.setText(offText);
            }
        });

        btn.fireEvent(new ActionEvent());
    }

    public static void setMaxCharCount(final TextInputControl control, final int count) {
        control.addEventFilter(KeyEvent.KEY_TYPED, (event) -> {
            final String text = control.getText();
            if (text.length() == count) {
                event.consume();
            } else if (text.length() > count) {
                control.setText(text.substring(0, count));
                event.consume();
            }
        });
    }

    public static boolean addTogglesToGroup(ToggleGroup group, Toggle... toggles) {
        return group.getToggles().addAll(toggles);
    }

    public static boolean addTogglesToGroup(MetaToggleGroup group, Toggle... toggles) {
        for (final Toggle t : toggles) {
            group.add(t, "");
        }

        return true;
    }

    public static Point2D getScreenCoordinates(Node node) {

        final double x = node.getScene().getWindow().getX();
        final double y = node.getScene().getWindow().getY();

        final Bounds localBounds = node.localToScene(node.getBoundsInLocal());

        return new Point2D(x + localBounds.getMinX(), y + localBounds.getMaxY());
    }

    public static boolean canUseJavaFX() {
        try {
            return Class.forName("javafx.application.Application") != null;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Please use {@link Stage#setAlwaysOnTop(boolean)} instead.
     */
    @Deprecated
    public static void makeAlwaysOnTop(final Stage stage) {
        stage.setAlwaysOnTop(true);
    }

    public static Alert buildLongAlert(String context, String longMessage, Node... additional) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(context);

        Label label = new Label("Details: ");

        TextArea textArea = new TextArea(longMessage);
        textArea.setEditable(false);
        textArea.setWrapText(false);

        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setMaxWidth(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expanded = new GridPane();
        expanded.setMaxWidth(Double.MAX_VALUE);
        expanded.add(label, 0, 0);
        expanded.add(textArea, 0, 1);

        for (int i = 0; i < additional.length; i++)
            expanded.add(additional[i], 0, 2 + i);

        alert.getDialogPane().setContent(expanded);
        alert.getDialogPane().setPrefWidth(700);
        alert.getDialogPane().setPrefHeight(400);

        return alert;
    }

    public static Alert buildException(Exception e) {
        return buildException(e.getClass().getSimpleName() + " : " + e.getMessage(), e);
    }

    public static Alert buildException(String context, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, context);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        e.printStackTrace(pw);

        String text = sw.toString();
        Label label = new Label("Exception Stack Trace: ");

        TextArea textArea = new TextArea(text);
        textArea.setEditable(false);
        textArea.setWrapText(false);

        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setMaxWidth(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expanded = new GridPane();
        expanded.setMaxWidth(Double.MAX_VALUE);
        expanded.add(label, 0, 0);
        expanded.add(textArea, 0, 1);

        alert.getDialogPane().setContent(expanded);
        alert.getDialogPane().setPrefWidth(800);
        alert.getDialogPane().setPrefHeight(600);

        return alert;
    }

}
