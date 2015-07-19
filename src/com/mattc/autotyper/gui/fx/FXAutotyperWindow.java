package com.mattc.autotyper.gui.fx;

import static javafx.scene.input.KeyCombination.ModifierValue.DOWN;
import static javafx.scene.input.KeyCombination.ModifierValue.UP;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.mattc.autotyper.AppVersion;
import com.mattc.autotyper.Autotyper;
import com.mattc.autotyper.Ref;
import com.mattc.autotyper.Strings;
import com.mattc.autotyper.Strings.Resources;
import com.mattc.autotyper.gui.ConfirmFileDialog;
import com.mattc.autotyper.gui.GuiAccessor;
import com.mattc.autotyper.gui.LocationHandler;
import com.mattc.autotyper.meta.Outcome;
import com.mattc.autotyper.minify.Minifier;
import com.mattc.autotyper.robot.Keyboard;
import com.mattc.autotyper.robot.KeyboardMethodology;
import com.mattc.autotyper.util.Console;
import com.mattc.autotyper.util.OS;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class FXAutotyperWindow extends Application {

    /**
     * Meta Key for Rank in MetaToggleGroup
     */
    private static final String META_RANK = "RANK";
    private static FXAutotyperWindow INSTANCE;

    private final Preferences prefs = Preferences.userNodeForPackage(FXAutotyperWindow.class);
    private final EvictingQueue<String> locations = EvictingQueue.create(50);

    private Keyboard keys;
    private Stage primaryStage;

    private final ObjectProperty<Path> fileProperty = new SimpleObjectProperty<>();
    private final IntegerProperty inputDelayProperty = new SimpleIntegerProperty(40);
    private final IntegerProperty waitTimeProperty = new SimpleIntegerProperty(5000);
    private final BooleanProperty minifyProperty = new SimpleBooleanProperty(false);

    private boolean doConfirm;
    private volatile boolean doSave = false;
    private int curRank;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        try {
            if (INSTANCE != null) throw new IllegalStateException("Multiple Instances of FXAutotyperWindow!");

            Thread.currentThread().setName("FX_GUI");
            FXAutotyperWindow.INSTANCE = this;
            this.primaryStage = primaryStage;

            // Load Preferences and obtain proper Keyboard
            loadPrefs();
            obtainKeyboard();
            addIcons(primaryStage);

            // Initialize Parent Nodes
            final BorderPane root = new BorderPane();
            final StackPane gridStack = new StackPane();
            final GridPane grid = new GridPane();
            grid.setGridLinesVisible(false);
            grid.setHgap(5);
            grid.setVgap(5);

            gridStack.getChildren().add(grid);

            // Initialize Button Bar
            final StackPane buttonStack = new StackPane();
            final HBox buttonBox = new HBox(10);
            final MetaToggleGroup btnGroup = new MetaToggleGroup();
            final Button startBtn = new Button("Start");
            final RadioButton fileBtn = new RadioButton("File");
            final RadioButton urlBtn = new RadioButton("URL");
            final RadioButton pasteBtn = new RadioButton("Paste");
            final RadioButton autoBtn = new RadioButton("Auto");
            final CheckBox minifyBtn = new CheckBox("Minify Lua Code?");

            minifyBtn.setTooltip(new TimedTooltip("Speed up autotyping by reducing character count.\nMay not be effective on *all* files.\n\nWill fail on non-Lua code.", 200));
            minifyBtn.selectedProperty().bindBidirectional(minifyProperty);
            MetaToggleGroup.addTogglesToGroup(btnGroup, fileBtn, Strings.GHOST_TEXT_FSELECT, urlBtn, Strings.GHOST_TEXT_USELECT, pasteBtn, Strings.GHOST_TEXT_PSELECT, autoBtn, Strings.GHOST_TEXT_ASELECT);
            btnGroup.putProperty(fileBtn, META_RANK, 1);
            btnGroup.putProperty(urlBtn, META_RANK, 2);
            btnGroup.putProperty(pasteBtn, META_RANK, 3);
            btnGroup.putProperty(autoBtn, META_RANK, 4);
            btnGroup.setSelectedForProperty(META_RANK, this.curRank);

            buttonStack.setAlignment(Pos.BASELINE_LEFT);
            StackPane.setAlignment(startBtn, Pos.BASELINE_RIGHT);

            startBtn.setPrefSize(50, 20);
            buttonBox.getChildren().addAll(fileBtn, urlBtn, pasteBtn, autoBtn, minifyBtn);
            buttonStack.setId("button-box");
            buttonStack.setPadding(new Insets(15, 25, 15, 25));
            buttonStack.getChildren().addAll(buttonBox, startBtn);
            startBtn.setDefaultButton(true);

            // Initialize the Grid

            final HBox locBox = new HBox(5);
            final Label locLabel = new Label("Location:");

            final AutoCompleteTextField locField = new AutoCompleteTextField(Lists.newArrayList(locations));
            final InteractiveBox wBox = new InteractiveBox("Wait %t seconds before typing.", Pos.CENTER);
            final InteractiveBox iBox = new InteractiveBox("Wait %t milliseconds between keystrokes.", Pos.CENTER);
            final InteractiveBox cBox = new InteractiveBox("I %B want to see a preview before typing.", Pos.CENTER);
            final TextField wField = wBox.getInteractiveChild(0, TextField.class);
            final TextField iField = iBox.getInteractiveChild(0, TextField.class);
            final ToggleButton cBtn = cBox.getInteractiveChild(0, ToggleButton.class);

            wField.setPrefColumnCount(2);
            wField.setAlignment(Pos.CENTER);
            wField.setText(Integer.toString(this.waitTimeProperty.get() / 1000));
            iField.setPrefColumnCount(2);
            iField.setAlignment(Pos.CENTER);
            iField.setText(Integer.toString(this.inputDelayProperty.get()));
            locField.setPrefColumnCount(32);
            locField.setPromptText(btnGroup.getMetaStringForSelected());
            cBtn.setSelected(this.doConfirm);

            locField.setTooltip(new TimedTooltip("Location from which data can be autotyped from.", 200));
            locBox.getChildren().addAll(locLabel, locField);

            FXGuiUtils.setMaxCharCount(wField, 2);
            FXGuiUtils.setMaxCharCount(iField, 3);
            FXGuiUtils.setToggleTextSwitch(cBtn, "do", "do not");

            btnGroup.selectedToggleProperty().addListener((obs, oldValue, newValue) -> {
                locField.setPromptText(btnGroup.getMetaString(newValue));
                FXAutotyperWindow.this.curRank = btnGroup.getMetaForSelected(META_RANK, Integer.class);
            });

            wField.textProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue.trim().isEmpty()) {
                    FXAutotyperWindow.this.waitTimeProperty.set(1000);
                } else if (!isValid(newValue)) {
                    wField.setText(oldValue);
                } else {
                    FXAutotyperWindow.this.waitTimeProperty.set(Integer.parseInt(newValue) * 1000);
                }
            });

            iField.textProperty().addListener((obs, ov, nv) -> {
                if (nv.trim().isEmpty()) {
                    FXAutotyperWindow.this.inputDelayProperty.set(com.mattc.autotyper.Parameters.MIN_DELAY);
                } else if (!isValid(nv)) {
                    iField.setText(ov);
                } else {
                    int value = Integer.parseInt(nv);
                    FXAutotyperWindow.this.inputDelayProperty.set(Math.max(value, com.mattc.autotyper.Parameters.MIN_DELAY));
                }
            });

            // When we lose foucs, check value to display at least the minimum delay
            iField.focusedProperty().addListener((obs, ov, nv) -> {
                if (nv || !isValid(iField.getText()))
                    return;

                int val = Integer.parseInt(iField.getText());
                if (val < com.mattc.autotyper.Parameters.MIN_DELAY) {
                    iField.setText(String.valueOf(com.mattc.autotyper.Parameters.MIN_DELAY));
                    inputDelayProperty.set(com.mattc.autotyper.Parameters.MIN_DELAY);
                }
            });

            cBtn.selectedProperty().addListener((obs, oldValue, newValue) -> FXAutotyperWindow.this.doConfirm = newValue);

            startBtn.setOnAction(new EventHandler<ActionEvent>() {
                Task<Boolean> typerTask;

                @Override
                public void handle(ActionEvent event) {
                    final String text = locField.getText().trim();

                    if (text.length() == 0) {
                        showError("Some Text Must be Entered!");
                    } else if ((typerTask != null) && typerTask.isRunning()) {
                        showError("Cannot run two simultaneous jobs! Please wait for the other to terminate...");
                    } else {
                        typerTask = makeTask();
                        btnGroup.getSelectedToggle();
                        LocationHandler handler;
                        Outcome outcome;

                        if (fileBtn.isSelected()) {
                            handler = LocationHandler.FILE;
                        } else if (urlBtn.isSelected()) {
                            handler = LocationHandler.URL;
                        } else if (pasteBtn.isSelected()) {
                            handler = LocationHandler.PASTEBIN;
                        } else {
                            try {
                                handler = LocationHandler.detect(text);
                            } catch (final Exception e1) {
                                Console.debug(e1);
                                showError(String.format("Could Not Auto-Detect Location:%n%s", e1.getMessage()));
                                return;
                            }
                        }

                        // Isolate Useful URI
                        final String textNoTag;
                        if (text.startsWith(handler.tag()))
                            textNoTag = text.substring(text.indexOf(':') + 1).trim();
                        else
                            textNoTag = text;

                        outcome = handler.canHandle(textNoTag);

                        if (outcome.isFailure()) {
                            showError(outcome.reason);
                        } else {
                            Path file = handler.handle(textNoTag);
                            try {
                                if (minifyProperty.get()) {
                                    boolean success = false;
                                    try {
                                        file = Minifier.minifyFileToCopy(file);
                                        success = true;
                                    } catch (Exception e) {
                                        Console.exception(e);
                                    }

                                    if (!success && !promptContinue("Minification Failed! Continue without minification?"))
                                        return;
                                }

                                if (FXAutotyperWindow.this.doConfirm) if (!approve(file)) return;

                                fileProperty.set(file);

                                int seconds = (FXAutotyperWindow.this.waitTimeProperty.get() / 1000);
                                ButtonType[] types = new ButtonType[]{ButtonType.YES, ButtonType.NO};
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Start Autotyping in " + seconds + " seconds?\n\nAll windows will be hidden until the task is complete...", types);
                                alert.initOwner(FXAutotyperWindow.this.primaryStage);
                                alert.initModality(Modality.APPLICATION_MODAL);
                                alert.initStyle(StageStyle.DECORATED);

                                alert.getDialogPane().setPrefSize(420, 200);

                                alert.setOnHidden((e) -> {
                                    if (alert.getResult() == ButtonType.YES) {
                                        Platform.runLater(typerTask);
                                        if (saveToHistory(textNoTag, handler.tag()))
                                            locField.addData(handler.tag() + textNoTag);
                                    }
                                });

                                alert.show();
                            } catch (final Exception e1) {
                                Console.exception(e1);
                            }
                        }
                    }

                }

                private void prestart() {
                    setInputDisabled(true);
                    primaryStage.hide();
                }

                private void onSuccess(WorkerStateEvent e) {
                    setInputDisabled(false);
                    primaryStage.show();
                    showMessage("Autotyping Complete!");
                }

                private void onError(WorkerStateEvent e) {
                    setInputDisabled(false);
                    primaryStage.show();

                    Preconditions.checkNotNull(e.getSource());

                    Throwable ex = e.getSource().getException();
                    if (ex != null) {
                        Console.exception(ex);
                        showError("Autotyping Failed!: " + ex.getMessage());
                    } else {
                        Console.bigWarning("Autotyping Failed: Unknown Reason! Source: " + e.getSource());
                        showError("Autotyping Failed!: Unknown Reason!");
                    }
                }

                private void setInputDisabled(boolean state) {
                    locField.setDisable(state);
                    wField.setDisable(state);
                    iField.setDisable(state);
                    cBtn.setDisable(state);
                }

                private Task<Boolean> makeTask() {
                    Task<Boolean> task = new FXAutoTypingTask(keys, fileProperty, waitTimeProperty, inputDelayProperty, this::prestart);
                    task.setOnSucceeded(this::onSuccess);
                    task.setOnFailed(this::onError);

                    return task;
                }

            });

            // Put it all together

            // I have to admit all those constant numbers just look reallllly annoying.
            grid.add(wBox, 0, 0, 1, 1);
            grid.add(iBox, 0, 1, 1, 1);
            grid.add(cBox, 0, 2, 1, 1);
            grid.add(locBox, 0, 3, 1, 1);
            grid.setAlignment(Pos.CENTER);

            root.setTop(getMenuBar());
            root.setBottom(buttonStack);
            root.setCenter(gridStack);

            final Scene scene;

            if (OS.get() == OS.WINDOWS) {
                scene = new Scene(root, 450, 250);
            } else {
                scene = new Scene(root, 600, 250);
            }

            scene.getStylesheets().add(Resources.getCSS("AutotyperWindow").url().toExternalForm());

            primaryStage.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
                if (!locField.contains(locField.sceneToLocal(event.getSceneX(), event.getSceneY()))) {
                    locField.hidePopup();
                }
            });

            this.doSave = true;
            locField.installXYChangeListeners(primaryStage);

            primaryStage.setTitle(Ref.APP_NAME + " " + Ref.VERSION);
            primaryStage.setResizable(true);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            Alert alert = FXGuiUtils.buildException(e);
            alert.showAndWait();
            Console.exception(e);
        }
    }

    @Override
    public void stop() {
        if (this.doSave) {
            savePrefs(this.waitTimeProperty.get(), this.inputDelayProperty.get(), this.curRank, this.locations);
        }
        this.keys.destroy();
    }

    // FXAutotyperWindow Specific Image Loading. We only expect 4
    private void addIcons(Stage stage) {
        Resources.setAppIcons(stage);
    }

    private HBox getMenuBar() {
        final Image infoIcon = new Image(Resources.getImage("about_icon.png").stream(), 30, 30, false, true);
        final Image copyIcon = new Image(Resources.getImage("copyright_icon.png").stream(), 30, 30, false, true);

        final HBox bar = new HBox(5);
        bar.setId("menu-bar");
        bar.setPadding(new Insets(5, 5, 8, 5));

        final Button infoBtn = new Button("", new ImageView(infoIcon));
        final Button copyBtn = new Button("", new ImageView(copyIcon));
        infoBtn.setTooltip(new TimedTooltip("Get Info (Opens in Browser)", 200));
        copyBtn.setTooltip(new TimedTooltip("See Copyright Statement", 200));

        final KeyCodeCombination infoAcc = new KeyCodeCombination(KeyCode.I, UP, DOWN, UP, UP, UP);
        final KeyCodeCombination copyAcc = new KeyCodeCombination(KeyCode.C, UP, DOWN, UP, UP, UP);

        infoBtn.setId("glass-button");
        copyBtn.setId("glass-button");

        infoBtn.sceneProperty().addListener((obs, ov, nv) -> {
            if (nv != null)
                nv.getAccelerators().put(infoAcc, FXAutotyperWindow.this::showGithubPage);

            if (ov != null)
                ov.getAccelerators().remove(infoAcc);
        });

        copyBtn.sceneProperty().addListener((obs, ov, nv) -> {
            if (nv != null)
                nv.getAccelerators().put(copyAcc, FXAutotyperWindow.this::showCopyrightInfo);

            if (ov != null)
                ov.getAccelerators().remove(copyAcc);
        });

        infoBtn.setOnAction((e) -> getHostServices().showDocument(Strings.GITHUB_URL));
        copyBtn.setOnAction((e) -> Autotyper.printCopyrightStatement(true));

        bar.getChildren().addAll(infoBtn, copyBtn);

        return bar;
    }

    private void showGithubPage() {
        getHostServices().showDocument(Strings.GITHUB_URL);
    }

    private void showCopyrightInfo() {
        Autotyper.printCopyrightStatement(true);
    }

    private void savePrefs(int waitTime, int delay, int selected, EvictingQueue<String> locations) {
        Console.empty();
        Console.info("SAVING AS VERSION: " + Ref.VERSION);
        this.prefs.put(Strings.PREFS_GUI_VERSION, Ref.VERSION);

        this.prefs.putInt(Strings.PREFS_GUI_WAIT, waitTime);
        this.prefs.putInt(Strings.PREFS_GUI_INPUTDELAY, delay);
        this.prefs.putInt(Strings.PREFS_GUI_SELECTED, selected);
        this.prefs.putBoolean(Strings.PREFS_GUI_MINIFY, minifyProperty.get());
        this.prefs.putBoolean(Strings.PREFS_GUI_CONFIRM, this.doConfirm);

        Console.info("\tWAIT TIME: " + waitTime);
        Console.info("\tIN. DELAY: " + delay);
        Console.info("\tSEL. BTN.: " + selected);
        Console.info("\tMINIFY?  : " + minifyProperty.get());
        Console.info("\tCON. DLG.: " + this.doConfirm);
        Console.empty();

        for (int i = 0; i < 50; i++) {
            final String text = locations.poll();
            this.prefs.put(Strings.PREFS_GUI_MEMORY + i, String.valueOf(text));
            if (text != null)
                Console.info("\tTO HISTORY: " + text);
        }

        Console.empty();
    }

    private void loadPrefs() {
        String version = this.prefs.get(Strings.PREFS_GUI_VERSION, "0.0.0");
        Console.empty();
        Console.info("LOADING VERSION: " + version);

        if (AppVersion.compareTo(version) != 0) {
            try {
                prefs.clear();
                Console.info("WARNING: Current Version is " + Ref.VERSION + "... Preferences will be erased to maintain compatibility.");
            } catch (BackingStoreException e) {
                Console.exception(e);
            }
        }

        this.waitTimeProperty.set(this.prefs.getInt(Strings.PREFS_GUI_WAIT, 5000));
        this.inputDelayProperty.set(this.prefs.getInt(Strings.PREFS_GUI_INPUTDELAY, 40));
        this.minifyProperty.set(this.prefs.getBoolean(Strings.PREFS_GUI_MINIFY, true));
        this.curRank = this.prefs.getInt(Strings.PREFS_GUI_SELECTED, 3);
        this.doConfirm = this.prefs.getBoolean(Strings.PREFS_GUI_CONFIRM, true);

        Console.info("\tWAIT TIME: " + waitTimeProperty.get());
        Console.info("\tIN. DELAY: " + inputDelayProperty.get());
        Console.info("\tSEL. BTN.: " + curRank);
        Console.info("\tMINIFY?  : " + minifyProperty.get());
        Console.info("\tCON. DLG.: " + doConfirm);
        Console.empty();

        this.curRank = Math.max(1, Math.min(4, this.curRank));

        saveToHistory("JCR8YTww", LocationHandler.PASTEBIN.tag());
        saveToHistory("6gyLvm4K", LocationHandler.PASTEBIN.tag());
        saveToHistory("nAinUn1h", LocationHandler.PASTEBIN.tag());

        for (int i = 0; i < 50; i++) {
            final String s = this.prefs.get(Strings.PREFS_GUI_MEMORY + i, "null");

            if (s != null && !s.equals("null") && s.indexOf(':') != -1) {
                int index = s.indexOf(':') + 1;
                String tag = s.substring(0, index);
                String main = s.substring(index);
                saveToHistory(main, tag);
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText("AutoTyper Error");
        alert.setTitle("Error!");

        alert.getDialogPane().setPrefSize(600, 400);

        alert.show();
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText("AutoTyper Message");
        alert.setTitle("Message");

        alert.getDialogPane().setPrefSize(600, 400);

        alert.show();
    }

    private static final HashFunction hf = Hashing.murmur3_32();
    private Set<HashCode> locationHashes = Sets.newHashSet();

    private boolean saveToHistory(String loc, String tag) {
        HashCode hash = hf.newHasher().putString(loc, Charsets.UTF_8).hash();
        Console.debug("Location '" + loc + "' hashed to '" + hash.toString() + "', New? = " + !locationHashes.contains(hash));

        if (!this.locationHashes.contains(hash)) {
            this.locations.add(tag + loc);
            this.locationHashes.add(hash);
            return true;
        }

        return false;
    }

    private void obtainKeyboard() {
        this.keys = Keyboard.retrieveKeyboard(KeyboardMethodology.TYPING);
    }

    private boolean isValid(String input) {
        try {
            return Integer.valueOf(input) != null;
        } catch (final NumberFormatException e) {
            // Ignored, just a check
        }

        return false;
    }

    private boolean approve(Path code) throws IOException {
        if (FXConfirmDialog.isAvailable())
            return FXConfirmDialog.confirm(this.primaryStage, code);
        else
            return new ConfirmFileDialog(null, code.toFile()).isApproved();
    }

    private boolean promptContinue(String msg) {
        Alert prompt = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        prompt.setHeaderText("Continue Task?");
        prompt.initModality(Modality.APPLICATION_MODAL);

        Optional<ButtonType> selection = prompt.showAndWait();
        return selection.isPresent() && selection.get() == ButtonType.YES;
    }

    public static void launch() {
        Application.launch(FXAutotyperWindow.class);
    }

    private static final GuiAccessor FX_ACCESSOR = new GuiAccessor() {
        @Override
        public void doShow() {
            if (FXAutotyperWindow.INSTANCE == null) {
                FXAutotyperWindow.launch();
            } else {
                FXAutotyperWindow.INSTANCE.primaryStage.show();
            }
        }

        @Override
        public void doHide() {
            FXAutotyperWindow.INSTANCE.primaryStage.hide();
        }

        @Override
        public void openSite(String url) {
            FXAutotyperWindow.INSTANCE.getHostServices().showDocument(url);
        }
    };

    public static GuiAccessor getAccessor() {
        return FX_ACCESSOR;
    }

}
