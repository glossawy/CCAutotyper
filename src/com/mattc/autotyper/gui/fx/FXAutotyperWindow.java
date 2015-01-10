package com.mattc.autotyper.gui.fx;

import static javafx.scene.input.KeyCombination.ModifierValue.DOWN;
import static javafx.scene.input.KeyCombination.ModifierValue.UP;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
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
import javafx.stage.Stage;

import com.google.common.collect.EvictingQueue;
import com.mattc.autotyper.Autotyper;
import com.mattc.autotyper.Ref;
import com.mattc.autotyper.Strings;
import com.mattc.autotyper.Strings.Resources;
import com.mattc.autotyper.Strings.Resources.Resource;
import com.mattc.autotyper.gui.ConfirmFileDialog;
import com.mattc.autotyper.gui.GuiAccessor;
import com.mattc.autotyper.gui.LocationHandler;
import com.mattc.autotyper.gui.fx.FXOptionPane.IconType;
import com.mattc.autotyper.meta.InformedOutcome;
import com.mattc.autotyper.robot.FXKeyboard;
import com.mattc.autotyper.robot.Keyboard;
import com.mattc.autotyper.robot.SwingKeyboard;
import com.mattc.autotyper.util.Console;
import com.mattc.autotyper.util.IOUtils;

public class FXAutotyperWindow extends Application {

	private static final String META_RANK = "RANK";

	private static FXAutotyperWindow INSTANCE;
	private final Preferences prefs = Preferences.userNodeForPackage(FXAutotyperWindow.class);
	private final EvictingQueue<String> locations = EvictingQueue.create(50);

	private Stage primaryStage;
	private Keyboard keys;
	private boolean doConfirm;
	private volatile boolean doSave = false;
	private int waitTime, inDelay, curRank;

	@Override
	public void start(final Stage primaryStage) throws Exception {
		if (INSTANCE != null) throw new IllegalStateException("Multiple Instances of FXAutotyperWindow!");

		Thread.currentThread().setName("FX_GUI");
		FXAutotyperWindow.INSTANCE = this;
		this.primaryStage = primaryStage;

		loadPrefs();
		obtainKeyboard();

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
		MetaToggleGroup.addTogglesToGroup(btnGroup, fileBtn, Strings.GHOST_TEXT_FSELECT, urlBtn, Strings.GHOST_TEXT_USELECT, pasteBtn, Strings.GHOST_TEXT_PSELECT, autoBtn, Strings.GHOST_TEXT_ASELECT);
		btnGroup.putProperty(fileBtn, META_RANK, 1);
		btnGroup.putProperty(urlBtn, META_RANK, 2);
		btnGroup.putProperty(pasteBtn, META_RANK, 3);
		btnGroup.putProperty(autoBtn, META_RANK, 4);
		btnGroup.setSelectedForProperty(META_RANK, this.curRank);

		buttonStack.setAlignment(Pos.BASELINE_LEFT);
		StackPane.setAlignment(startBtn, Pos.BASELINE_RIGHT);

		startBtn.setPrefSize(50, 20);
		buttonBox.getChildren().addAll(fileBtn, urlBtn, pasteBtn, autoBtn);
		buttonStack.setId("button-box");
		buttonStack.setPadding(new Insets(15, 25, 15, 25));
		buttonStack.getChildren().addAll(buttonBox, startBtn);
		startBtn.setDefaultButton(true);

		// Initialize the Grid

		final HBox locBox = new HBox(5);
		final Label locLabel = new Label("Location:");

		final TextField locField = new AutoCompleteTextField(FXCollections.observableArrayList(this.locations));
		final InteractiveBox wBox = new InteractiveBox("Wait %t seconds before typing.", Pos.CENTER);
		final InteractiveBox iBox = new InteractiveBox("Wait %t milliseconds between keystrokes.", Pos.CENTER);
		final InteractiveBox cBox = new InteractiveBox("I %B want to confirm before typing.", Pos.CENTER);
		final TextField wField = wBox.getInteractiveChild(0, TextField.class);
		final TextField iField = iBox.getInteractiveChild(0, TextField.class);
		final ToggleButton cBtn = cBox.getInteractiveChild(0, ToggleButton.class);

		wField.setPrefColumnCount(2);
		wField.setAlignment(Pos.CENTER);
		wField.setText(Integer.toString(this.waitTime / 1000));
		iField.setPrefColumnCount(2);
		iField.setAlignment(Pos.CENTER);
		iField.setText(Integer.toString(this.inDelay));
		locField.setPrefColumnCount(32);
		locField.setPromptText(btnGroup.getMetaStringForSelected());
		cBtn.setSelected(this.doConfirm);

		locBox.getChildren().addAll(locLabel, locField);

		FXGuiUtils.setMaxCharCount(wField, 2);
		FXGuiUtils.setMaxCharCount(iField, 2);
		FXGuiUtils.setToggleTextSwitch(cBtn, "do", "do not");

		btnGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				locField.setPromptText(btnGroup.getMetaString(newValue));
				FXAutotyperWindow.this.curRank = btnGroup.getMetaForSelected(META_RANK, Integer.class);
			}
		});

		wField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.trim().isEmpty()) {
					FXAutotyperWindow.this.waitTime = 1000;
				} else if (!isValid(newValue)) {
					wField.setText(oldValue);
				} else {
					FXAutotyperWindow.this.waitTime = Integer.parseInt(newValue) * 1000;
				}
			}
		});

		iField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				if (newValue.trim().isEmpty()) {
					FXAutotyperWindow.this.inDelay = 1;
				} else if (!isValid(newValue)) {
					iField.setText(oldValue);
				} else {
					FXAutotyperWindow.this.inDelay = Integer.parseInt(newValue);
				}
			}
		});

		cBtn.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				FXAutotyperWindow.this.doConfirm = newValue.booleanValue();
			}
		});

		startBtn.setOnAction(new EventHandler<ActionEvent>() {

			// private Thread prevThread;
			private Task<Boolean> prevTask;

			@Override
			public void handle(ActionEvent event) {
				final String text = locField.getText().trim();

				if (text.length() == 0) {
					showError("Some Text Must be Entered!");
					return;
				} else if ((this.prevTask != null) && this.prevTask.isRunning()) {
					showError("Cannot run two simultaneous jobs! Please wait for the other to terminate...");
					return;
				} else {
					btnGroup.getSelectedToggle();
					LocationHandler handler;
					InformedOutcome outcome;

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

					outcome = handler.canHandle(text);

					if (outcome.isFailure()) {
						showError(outcome.reason);
					} else {
						final File file = handler.handle(text);
						try {
							if (FXAutotyperWindow.this.doConfirm) if (!approve(file)) return;

							// Porting over the Swing way was not as easy with the
							// custom
							// FXOptionPane. So this is built customly.
							FXOptionPane.builder("Start Autotyping in " + (FXAutotyperWindow.this.waitTime / 1000) + " seconds?").setTitle("Autotyper Prompt").setOwner(FXAutotyperWindow.this.primaryStage).makeYesButton(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									((Node) event.getSource()).getScene().getWindow().hide();
									setInputDisabled(true);
									// prevThread = makeExecutionThread(file);
									// prevThread.setDaemon(true);
									// prevThread.start();
									// TODO Make Asynchronous
									prevTask = makeExecutionTask(file);
									Platform.runLater(prevTask);
									primaryStage.toBack();
									saveToHistory(text);
									((AutoCompleteTextField) locField).addData(text);
								}
							}).makeNoButton(FXOptionPane.DEFAULT_CLOSE_ACTION).makeBlocking().build();
						} catch (final Exception e1) {
							Console.exception(e1);
						}
					}
				}

			}

			private Task<Boolean> makeExecutionTask(final File file) {
				return new Task<Boolean>() {
					@Override
					protected Boolean call() throws Exception {
						try {
							FXAutotyperWindow.this.keys.setInputDelay(FXAutotyperWindow.this.inDelay);
							IOUtils.sleep(FXAutotyperWindow.this.waitTime);
							FXAutotyperWindow.this.keys.typeFile(file);
							setInputDisabled(false);
							showMessage("Finished typing " + locField.getText() + "!");
							return true;
						} catch (final IOException ex) {
							Console.exception(ex);
							showError("Failure to Autotype, Exception of type " + ex.getClass() + " occurred...");
						}

						return false;
					}
				};
			}

			//TODO
			@SuppressWarnings("unused")
			private Thread makeExecutionThread(final File file) {
				return new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							FXAutotyperWindow.this.keys.setInputDelay(FXAutotyperWindow.this.inDelay);
							IOUtils.sleep(FXAutotyperWindow.this.waitTime);
							FXAutotyperWindow.this.keys.typeFile(file);
							setInputDisabled(false);
							showMessage("Finished typing " + file.getName() + "!");
						} catch (final IOException e) {
							Console.exception(e);
						}
					}
				}, "Typer");
			}

			private void setInputDisabled(boolean state) {
				locField.setDisable(state);
				wField.setDisable(state);
				iField.setDisable(state);
				cBtn.setDisable(state);
			}

		});

		// Put it all together

		grid.add(wBox, 0, 0, 1, 1);
		grid.add(iBox, 0, 1, 1, 1);
		grid.add(cBox, 0, 2, 1, 1);
		grid.add(locBox, 0, 3, 1, 1);
		grid.setAlignment(Pos.CENTER);

		root.setTop(getMenuBar());
		root.setBottom(buttonStack);
		root.setCenter(gridStack);

		final Scene scene = new Scene(root, 450, 250);
		scene.getStylesheets().add(Resources.getCSS("AutotyperWindow").url().toExternalForm());

		primaryStage.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!locField.contains(locField.sceneToLocal(event.getSceneX(), event.getSceneY()))) {
					((AutoCompleteTextField) locField).hidePopup();
				}
			}
		});

		this.doSave = true;
		((AutoCompleteTextField) locField).installXYChangeListners(primaryStage);

		addIcons(primaryStage);
		primaryStage.setTitle(Ref.TITLE + " " + Ref.VERSION);
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void stop() {
		if (this.doSave) {
			savePrefs(this.waitTime, this.inDelay, this.curRank, this.locations);
		}
		this.keys.destroy();
	}

	private void addIcons(Stage stage) {
		final Image[] img = new Image[4];

		for (int i = 0, size = 32; (i < img.length) && (size <= 128); size += 16) {
			final Resource res = Resources.getImage("icon" + size + ".png");

			if ((res != null) && (res.stream() != null)) {
				Console.debug("Found icon" + size + ".png");
				img[i++] = new Image(res.stream());
			} else if ((((size % 32) == 0) || (size == 48)) && (size != 96)) {
				Console.error("Could not find icon" + size + ".png!");
			}
		}

		stage.getIcons().clear();
		for (final Image i : img)
			if (i != null) {
				stage.getIcons().add(i);
			}
	}

	private HBox getMenuBar() {
		final Image infoIcon = new Image(Resources.getImage("about_icon.png").stream(), 30, 30, false, true);
		final Image copyIcon = new Image(Resources.getImage("copyright_icon.png").stream(), 30, 30, false, true);

		final HBox bar = new HBox(5);
		bar.setId("menu-bar");
		bar.setPadding(new Insets(5, 5, 8, 5));

		final Button infoBtn = new Button("", new ImageView(infoIcon));
		final Button copyBtn = new Button("", new ImageView(copyIcon));

		infoBtn.setId("glass-button");
		copyBtn.setId("glass-button");

		infoBtn.sceneProperty().addListener(new ChangeListener<Scene>() {

			@Override
			public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
				if (newValue != null) {
					newValue.getAccelerators().put(new KeyCodeCombination(KeyCode.I, UP, UP, DOWN, UP, UP), new Runnable() {
						@Override
						public void run() {
							getHostServices().showDocument(Strings.GITHUB_URL);
						}
					});
				}
			}
		});

		copyBtn.sceneProperty().addListener(new ChangeListener<Scene>() {

			@Override
			public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
				if (newValue != null) {
					newValue.getAccelerators().put(new KeyCodeCombination(KeyCode.C, UP, UP, DOWN, UP, UP), new Runnable() {
						@Override
						public void run() {
							Autotyper.printCopyrightStatement(true);
						}
					});
				}
			}
		});

		infoBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				getHostServices().showDocument(Strings.GITHUB_URL);
			}

		});

		copyBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Autotyper.printCopyrightStatement(true);
			}

		});

		bar.getChildren().addAll(infoBtn, copyBtn);

		return bar;
	}

	private void savePrefs(int waitTime, int delay, int selected, EvictingQueue<String> locations) {
		this.prefs.putInt(Strings.PREFS_GUI_WAIT, waitTime);
		this.prefs.putInt(Strings.PREFS_GUI_INPUTDELAY, delay);
		this.prefs.putInt(Strings.PREFS_GUI_SELECTED, selected);
		this.prefs.putBoolean(Strings.PREFS_GUI_CONFIRM, this.doConfirm);

		for (int i = 0; i < 50; i++) {
			final String text = locations.peek() == null ? "null" : locations.poll();
			this.prefs.put(Strings.PREFS_GUI_MEMORY + i, text);
		}
	}

	private void loadPrefs() {
		this.waitTime = this.prefs.getInt(Strings.PREFS_GUI_WAIT, 5000);
		this.inDelay = this.prefs.getInt(Strings.PREFS_GUI_INPUTDELAY, 40);
		this.curRank = this.prefs.getInt(Strings.PREFS_GUI_SELECTED, 3);
		this.doConfirm = this.prefs.getBoolean(Strings.PREFS_GUI_CONFIRM, true);

		this.curRank = Math.max(1, Math.min(4, this.curRank));

		for (int i = 0; i < 50; i++) {
			final String s = this.prefs.get(Strings.PREFS_GUI_MEMORY + i, "null");

			if (!s.equals("null")) {
				this.locations.add(s);
			}
		}
	}

	private void showError(String message) {
		FXOptionPane.showMessage(this.primaryStage, "Autotyper Error", message, IconType.ERROR);
	}

	private void showMessage(String message) {
		FXOptionPane.showMessage(this.primaryStage, "Autotyper Message", message, IconType.INFO);
	}

	private void saveToHistory(String loc) {
		if (this.locations.contains(loc)) return;

		this.locations.add(loc);
	}

	private void obtainKeyboard() {
		if (FXGuiUtils.isFXRobotAvailable()) {
			this.keys = new FXKeyboard(this.inDelay);
		} else {
			this.keys = new SwingKeyboard(this.inDelay);
		}
		// this.keys = new SwingKeyboard(this.inDelay);
	}

	private boolean isValid(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (final NumberFormatException e) {
		}

		return false;
	}

	private boolean approve(File code) throws IOException {
		if (FXConfirmDialog.isAvailable())
			return FXConfirmDialog.confirm(this.primaryStage, code);
		else
			return new ConfirmFileDialog(null, code).isApproved();
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
	};

	public static GuiAccessor getAccessor() {
		return FX_ACCESSOR;
	}

}
