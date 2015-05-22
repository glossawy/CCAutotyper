package com.mattc.autotyper.gui.fx;

import static com.mattc.autotyper.gui.fx.FXGuiUtils.makeAlwaysOnTop;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import com.mattc.autotyper.Strings;
import com.mattc.autotyper.meta.FXCompatible;
import com.mattc.autotyper.meta.ModeParser;
import com.mattc.autotyper.meta.ModeParser.Mode;
import com.mattc.autotyper.util.Console;
import com.mattc.autotyper.util.IOUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.prefs.Preferences;

@FXCompatible
public class FXConfirmDialog extends Stage {

    private static final String[] THEMES = {"neat", "blackboard"};
    private static final String DEFAULT_SYNTAX_LUA = "Lua";
    private static final Preferences prefs = Preferences.userNodeForPackage(FXAutotyperWindow.class);
    private static final int BUTTON_WIDTH = 200;

    private final WebView webView = new WebView();
    private volatile boolean approved = false;

    /**
     * a template for editing code - this can be changed to any template derived of
     * the supported modes at http://codemirror.net to allow syntax highlighted
     * editing of a wide variety of languages.
     */
    private static final String EDITING_TEMPLATE = "<!doctype html>" + "<html>" + "<head>" + " <link rel=\"stylesheet\" href=\"http://codemirror.net/lib/codemirror.css\">" + " <link rel=\"stylesheet\" href=\"http://codemirror.net/theme/neat.css\">" + " <link rel=\"stylesheet\" href=\"http://codemirror.net/theme/blackboard.css\">" + " <script src=\"http://codemirror.net/lib/codemirror.js\"></script>" + " <script src=\"http://codemirror.net/mode/clike/clike.js\"></script>" + " <script src=\"http://codemirror.net/mode/%1$s/%1$s.js\"></script>" + "</head>" + "<body>" + "<form><textarea id=\"code\" name=\"code\">\n" + "%2$s" + "</textarea></form>" + "<script>" + " var editor = CodeMirror.fromTextArea(document.getElementById(\"code\"), {" + " lineNumbers: true," + " matchBrackets: true," + " theme: \"%3$s\"," + " mode: \"%4$s\"," + " readOnly: true" + " });" + "</script>" + "</body>" + "</html>";

    private String codeSnapshot;
    private String theme = "neat";
    private Mode mode = ModeParser.getModeFor(DEFAULT_SYNTAX_LUA);

    private FXConfirmDialog(Window owner, String code) {
        super(StageStyle.UTILITY);

        loadPrefs();
        this.codeSnapshot = code;
        setTitle("Code Confirmation");
        setResizable(false);
        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);

        this.webView.setPrefSize(650, 325);
        this.webView.getEngine().loadContent(getFormattedTemplate());

        final Scene scene = new Scene(assemble());
        scene.getStylesheets().addAll(owner.getScene().getStylesheets());
        setScene(scene);
        toFront();
        centerOnScreen();
        makeAlwaysOnTop(this);
        showAndWait();
        savePrefs();
    }

    public static boolean confirm(Window owner, Path code) throws IOException {
        return confirm(owner, IOUtils.fileToString(code.toFile()));
    }

    public static boolean confirm(Window owner, String code) {
        final FXConfirmDialog dialog = new FXConfirmDialog(owner, code);

        return dialog.isApproved();
    }

    public static boolean isAvailable() {
        try {
            return IOUtils.checkConnectionSuccess("http://codemirror.net");
        } catch (final Exception e) {
            Console.exception(e);
            return false;
        }
    }

    private void savePrefs() {
        prefs.put(Strings.PREFS_CONFIRM_THEME, this.theme);
        prefs.put(Strings.PREFS_CONFIRM_MODE, this.mode.displayName);
    }

    private void loadPrefs() {
        this.theme = prefs.get(Strings.PREFS_CONFIRM_THEME, this.theme);
        this.mode = ModeParser.getModeFor(prefs.get(Strings.PREFS_CONFIRM_MODE, DEFAULT_SYNTAX_LUA));
    }

    private Parent assemble() {

        final BorderPane root = new BorderPane();

        final HBox btnBox = new HBox(50);
        final Button approve = new Button("Approve");
        final Button reject = new Button("Reject");

        approve.setPrefWidth(BUTTON_WIDTH);
        reject.setPrefWidth(BUTTON_WIDTH);

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(approve, reject);
        ButtonBar.setButtonData(approve, ButtonBar.ButtonData.YES);
        ButtonBar.setButtonData(reject, ButtonBar.ButtonData.NO);

        approve.setDefaultButton(true);

        btnBox.setId("button-box");
        btnBox.setAlignment(Pos.BASELINE_CENTER);
        btnBox.setPadding(new Insets(10, 0, 10, 0));
        btnBox.getChildren().addAll(buttonBar);

        final VBox webBox = new VBox();
        webBox.getChildren().add(this.webView);

        final HBox optBox = new HBox(20);
        final Button cpyBtn = new Button("Copy Code");
        final ComboBox<String> themeColor = new ComboBox<>(FXCollections.observableArrayList(THEMES));
        final ComboBox<String> syntaxes = new ComboBox<>(FXCollections.observableList(ModeParser.getPossibleModes()));

        syntaxes.getSelectionModel().select(this.mode.displayName);
        themeColor.getSelectionModel().select(this.theme);
        cpyBtn.setPrefWidth(BUTTON_WIDTH);

        syntaxes.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null)
                return;

            FXConfirmDialog.this.mode = ModeParser.getModeFor(newValue);
            FXConfirmDialog.this.webView.getEngine().loadContent(getFormattedTemplate());
        });

        themeColor.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) return;

            FXConfirmDialog.this.theme = newValue.toLowerCase();
            FXConfirmDialog.this.webView.getEngine().loadContent(getFormattedTemplate());
        });

        optBox.setId("menu-bar");
        optBox.setAlignment(Pos.CENTER_LEFT);
        optBox.setPadding(new Insets(10, 0, 10, 10));
        optBox.getChildren().addAll(cpyBtn, syntaxes, themeColor);

        root.setTop(optBox);
        root.setCenter(webBox);
        root.setBottom(btnBox);
        VBox.setVgrow(this.webView, Priority.ALWAYS);

        syntaxes.setEditable(false);
        approve.setOnAction((e) -> {
            FXConfirmDialog.this.approved = true;
            hide();
        });

        reject.setOnAction((e) -> {
            FXConfirmDialog.this.approved = false;
            hide();
        });

        cpyBtn.setOnAction((e) -> {
            final String code = getCodeAndSnapshot();

            final Clipboard clip = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();

            content.putString(code);
            Platform.runLater(() -> clip.setContent(content));
        });

        return root;
    }

    public void setCode(String code) {
        this.codeSnapshot = code;
        refresh();
    }

    public String getCodeAndSnapshot() {
        this.codeSnapshot = (String) this.webView.getEngine().executeScript("editor.getValue();");

        return this.codeSnapshot;
    }

    public boolean isApproved() {
        return this.approved;
    }

    private String getFormattedTemplate() {
        return String.format(EDITING_TEMPLATE, this.mode.name, this.codeSnapshot, this.theme, this.mode.mimeType);
    }

    private void refresh() {
        this.webView.getEngine().loadContent(getFormattedTemplate());
    }

}
