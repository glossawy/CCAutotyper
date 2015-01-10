package com.mattc.autotyper.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.text.JTextComponent;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rtextarea.RTextArea;

import com.google.common.collect.Lists;
import com.mattc.autotyper.Autotyper;
import com.mattc.autotyper.Parameters;
import com.mattc.autotyper.Ref;
import com.mattc.autotyper.Strings;
import com.mattc.autotyper.Strings.Resources;
import com.mattc.autotyper.Strings.Resources.Resource;
import com.mattc.autotyper.meta.InformedOutcome;
import com.mattc.autotyper.robot.SwingKeyboard;
import com.mattc.autotyper.util.Console;
import com.mattc.autotyper.util.IOUtils;

/**
 * Main Window for a Swing Autotyper GUI <br />
 * <br />
 * Handles such things as auto-completion and launching the application. Attempts to
 * be slick... using Swing...
 * 
 * @author Matthew
 *
 */
public class AutotyperWindow extends JFrame implements GuiAccessor {

	private static final long serialVersionUID = -776172984918939880L;

	private static final String RANK = "rank";

	private final SwingKeyboard keys;
	private final Preferences prefs;
	private final Timer timer = new Timer(true);
	private final String[] locations = new String[50];
	private int pointer = 0;

	private boolean doConfirm;
	private int waitTime, inDelay;
	private MetaButtonGroup group;

	public AutotyperWindow() {
		super(Ref.TITLE + " | " + Ref.VERSION);

		// Initialize Preferences and Swing based Keyboard (we know FX won't work
		this.keys = new SwingKeyboard(Parameters.DEFAULT_DELAY);
		this.prefs = Preferences.userNodeForPackage(AutotyperWindow.class);

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setName("GUI");
				setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				setIcons();

				init();
				initMenuBar();
				setSize(500, 280);
				setResizable(false);
				setLocationRelativeTo(null);
				setVisible(false);
			}
		});
	}

	/**
	 * Create and Layout the various components.
	 */
	private void init() {
		final Container cont = getContentPane();
		cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));

		loadPrefs();
		this.group = new MetaButtonGroup();

		// Auto Completion set up
		final DefaultCompletionProvider provider = new DefaultCompletionProvider();
		final AutoCompletion locationCompleter = new AutoCompletion(updateLocationCompletionProvider(provider));

		// TODO NodeParser Equivalent/InteractiveBox Equivalent
		final JPanel inputPanel = new JPanel();
		final JPanel radioPanel = new JPanel();
		final JLabel wLabel1 = new JLabel("Wait");
		final JLabel wLabel2 = new JLabel("seconds before typing.");
		final JLabel iLabel1 = new JLabel("Wait");
		final JLabel iLabel2 = new JLabel("milliseconds between keystrokes.");
		final JLabel cLabel1 = new JLabel("I");
		final JLabel cLabel2 = new JLabel("want to confirm the file.");
		final JTextField wField = new JTextField(2);
		final JTextField iField = new JTextField(2);
		final RTextArea lField = new RTextArea(1, 50);
		final JToggleButton cButton = new JToggleButton(this.doConfirm ? "do" : "do not");
		final JRadioButton file = new JRadioButton("Local File");
		final JRadioButton url = new JRadioButton("Website File");
		final JRadioButton paste = new JRadioButton("Pastebin Code");
		final JRadioButton auto = new JRadioButton("Auto Detect");
		final JButton type = new JButton("Start");

		radioPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

		cButton.setSelected(this.doConfirm);
		wField.setDocument(new TextLimitDocument(2));
		iField.setDocument(new TextLimitDocument(2));
		wField.setText(Integer.toString(this.waitTime / 1000));
		iField.setText(Integer.toString(this.inDelay));
		wField.setHorizontalAlignment(SwingConstants.CENTER);
		iField.setHorizontalAlignment(SwingConstants.CENTER);
		lField.setMargin(new Insets(0, 5, 0, 5));
		lField.setHighlightCurrentLine(false);
		locationCompleter.install(lField);

		final JPanel s1 = new JPanel();
		final JPanel s2 = new JPanel();
		final JPanel s3 = new JPanel();
		s1.add(wLabel1);
		s1.add(wField);
		s1.add(wLabel2);
		s2.add(iLabel1);
		s2.add(iField);
		s2.add(iLabel2);
		s3.add(cLabel1);
		s3.add(cButton);
		s3.add(cLabel2);

		// Initialize Meta Button Properties
		this.group.add(auto, Strings.GHOST_TEXT_ASELECT);
		this.group.add(url, Strings.GHOST_TEXT_USELECT);
		this.group.add(file, Strings.GHOST_TEXT_FSELECT);
		this.group.add(paste, Strings.GHOST_TEXT_PSELECT);
		this.group.putProperty(file, RANK, 1);
		this.group.putProperty(url, RANK, 2);
		this.group.putProperty(paste, RANK, 3);
		this.group.putProperty(auto, RANK, 4);
		this.group.setSelectedForProperty(RANK, this.prefs.getInt(Strings.PREFS_GUI_SELECTED, 3));
		addGhostText(lField, this.group.getMetaStringForSelected());

		radioPanel.add(file);
		radioPanel.add(url);
		radioPanel.add(paste);
		radioPanel.add(auto);
		radioPanel.add(type);

		inputPanel.add(lField);
		inputPanel.add(radioPanel);

		cont.add(s1);
		cont.add(s2);
		cont.add(s3);
		cont.add(inputPanel);

		locationCompleter.setAutoCompleteEnabled(true);
		locationCompleter.setAutoActivationEnabled(true);
		locationCompleter.setAutoCompleteSingleChoices(false);
		locationCompleter.setShowDescWindow(false);

		wField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				final JTextField field = (JTextField) e.getSource();

				if (isValid(field.getText())) {
					AutotyperWindow.this.waitTime = Integer.parseInt(field.getText()) * 1000;
				} else {
					field.setText(Integer.toString(AutotyperWindow.this.waitTime / 1000));
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		});

		iField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				final JTextField field = (JTextField) e.getSource();

				if (isValid(field.getText())) {
					AutotyperWindow.this.inDelay = Integer.parseInt(field.getText());
				} else {
					field.setText(Integer.toString(AutotyperWindow.this.inDelay));
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		});

		lField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				final RTextArea field = (RTextArea) e.getSource();

				if (isGhostText(field)) {
					removeGhostText(field);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				final RTextArea field = (RTextArea) e.getSource();

				if (field.getText().trim().isEmpty()) {
					addGhostText(field, AutotyperWindow.this.group.getMetaStringForSelected());
				}
			}
		});

		type.addActionListener(new ActionListener() {
			private Thread prevThread;

			@Override
			public void actionPerformed(ActionEvent e) {
				final String text = lField.getText().trim();

				if (isGhostText(lField) || (text.length() == 0)) {
					showError("You must provide some kind of location!");
					return;
				} else if ((this.prevThread != null) && this.prevThread.isAlive()) {
					showError("Can Only Autotype one File at a Time!");
					return;
				} else {
					LocationHandler handler;
					InformedOutcome outcome;
					if (file.isSelected()) {
						handler = LocationHandler.FILE;
					} else if (url.isSelected()) {
						handler = LocationHandler.URL;
					} else if (paste.isSelected()) {
						handler = LocationHandler.PASTEBIN;
					} else if (auto.isSelected()) {
						try {
							handler = LocationHandler.detect(text);
						} catch (final Exception e1) {
							Console.debug(e1);
							showError(String.format("Could Not Auto-Detect Location:%n%s", e1.getMessage()));
							return;
						}
					} else
						return;

					Console.debug("Using " + handler.name() + " LocationHandler...");
					outcome = handler.canHandle(text);

					if (outcome.isFailure()) {
						Console.debug(outcome);
						showError(outcome.reason);
					} else {
						final File file = handler.handle(text);
						try {

							// Confirmation Check, Uses Swing
							if (AutotyperWindow.this.doConfirm) {
								final ConfirmFileDialog dialog = new ConfirmFileDialog(AutotyperWindow.this, file);
								if (!dialog.isApproved()) return;
							}

							// Pre-Typing Prompt
							final boolean ok = showPrompt("Start Autotyping in " + (AutotyperWindow.this.waitTime / 1000) + " seconds?");
							if (ok) {
								// Block Input and Execute Typing on Separate Thread
								setInput(false);
								this.prevThread = makeExecutionThread(file);
								this.prevThread.start();
								toBack();
								saveToHistory(text);
								updateLocationCompletionProvider((DefaultCompletionProvider) locationCompleter.getCompletionProvider());
							} else
								return;

						} catch (final IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}

			/**
			 * Handles a Singular Autotyping Operation
			 * 
			 * @param file
			 * @return
			 */
			private Thread makeExecutionThread(final File file) {
				return new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							AutotyperWindow.this.keys.setInputDelay(AutotyperWindow.this.inDelay);
							IOUtils.sleep(AutotyperWindow.this.waitTime);
							AutotyperWindow.this.keys.typeFile(file);
							setInput(true);
							showMessage("Finished typing " + lField.getText() + "!");
						} catch (final IOException ex) {
							Console.exception(ex);
							showError("Failure to Autotype, Exception of type " + ex.getClass() + " occurred...");
						}
					}
				}, "TYPER");
			}

			/**
			 * Enable or Disabled Input Fields
			 * 
			 * @param state
			 */
			private void setInput(boolean state) {
				lField.setEnabled(state);
				wField.setEnabled(state);
				iField.setEnabled(state);
				cButton.setEnabled(state);
			}
		});

		/*
		 * Auto Update Ghost Text for Location Field every 200 ms.
		 */
		this.timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (lField.hasFocus() || (!lField.getText().trim().isEmpty() && !isGhostText(lField))) return;

				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						lField.setText(AutotyperWindow.this.group.getMetaStringForSelected());
					}
				});
			}
		}, 0, 200);

		/*
		 * When clicked, switch between "do" and "don't" confirm.
		 */
		cButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				AutotyperWindow.this.doConfirm = e.getStateChange() == ItemEvent.SELECTED;

				cButton.setText(AutotyperWindow.this.doConfirm ? "do" : "do not");
			}
		});
	}

	// Create and Add Init Menu Bar
	private void initMenuBar() {
		try {
			// Create Pseudo JMenu's that are images and act as buttons
			final JMenuBar bar = new JMenuBar();
			final Image icoImg = ImageIO.read(ClassLoader.getSystemClassLoader().getResource("res/about_icon.png"));
			final Image cpyImg = ImageIO.read(ClassLoader.getSystemClassLoader().getResource("res/copyright_icon.png"));
			final JMenu about = new JMenu();
			final JMenu copy = new JMenu();

			about.setIcon(new ImageIcon(icoImg.getScaledInstance(12, 12, Image.SCALE_SMOOTH)));
			copy.setIcon(new ImageIcon(cpyImg.getScaledInstance(12, 12, Image.SCALE_SMOOTH)));

			about.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent evt) {
					try {
						Desktop.getDesktop().browse(new URL(Strings.GITHUB_URL).toURI());
					} catch (IOException | URISyntaxException e) {
						Console.exception(e);
					}
				}
			});

			copy.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent evt) {
					if (evt.getButton() == MouseEvent.BUTTON1) {
						Autotyper.printCopyrightStatement(true);
					}
				}
			});

			bar.add(about);
			bar.add(copy);
			setJMenuBar(bar);
		} catch (final IOException e) {
			Console.exception(e);
			showError("A Non-Fatal Error Has Occurred!: " + e.getMessage());
		}
	}

	/**
	 * Check for and add Icons if possible. If this fails it may indicate corruption
	 * or a bad transfer as the images are included in the JAR File.
	 */
	private void setIcons() {
		final List<Image> icons = Lists.newArrayList();

		for (int size = 32; size <= 128; size += 16) {
			final Resource res = Resources.getImage("icon" + size + ".png");

			if ((res != null) && (res.stream() != null)) {
				Console.debug("Found icon" + size + ".png");
				try {
					icons.add(ImageIO.read(res.stream()));
				} catch (final IOException e) {
					Console.exception(e);
				}
			} else if ((((size % 32) == 0) || (size == 48)) && (size != 96)) {
				Console.error("Could not find icon" + size + ".png!");
			}
		}

		setIconImages(icons);
	}

	private void savePrefs(int waitTime, int delay, int selected, String[] locations) {
		this.prefs.putInt(Strings.PREFS_GUI_WAIT, waitTime);
		this.prefs.putInt(Strings.PREFS_GUI_INPUTDELAY, delay);
		this.prefs.putInt(Strings.PREFS_GUI_SELECTED, selected);
		this.prefs.putBoolean(Strings.PREFS_GUI_CONFIRM, this.doConfirm);

		for (int i = 0; i < locations.length; i++) {
			this.prefs.put(Strings.PREFS_GUI_MEMORY + i, locations[i] == null ? "null" : locations[i]);
		}
	}

	private void loadPrefs() {
		this.waitTime = this.prefs.getInt(Strings.PREFS_GUI_WAIT, 5000);
		this.inDelay = this.prefs.getInt(Strings.PREFS_GUI_INPUTDELAY, 40);
		this.doConfirm = this.prefs.getBoolean(Strings.PREFS_GUI_CONFIRM, true);

		for (int i = 0; i < this.locations.length; i++) {
			final String s = this.prefs.get(Strings.PREFS_GUI_MEMORY + i, "null");

			if (!s.equals("null")) {
				this.locations[i] = s;
			}
		}

		this.pointer = getPointer(this.locations);
	}

	// Make sure we execute on the EDT
	@Override
	public void setVisible(final boolean visible) {
		if (!EventQueue.isDispatchThread()) {
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					AutotyperWindow.super.setVisible(visible);
				}
			});
		} else {
			super.setVisible(visible);
		}
	}

	@Override
	public void dispose() {
		savePrefs(this.waitTime, this.inDelay, this.group.getMetaForSelected(RANK, Integer.class), this.locations);
		super.dispose();
		this.timer.cancel();
		this.keys.destroy();
		Autotyper.exit();
	}

	private boolean isValid(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (final NumberFormatException e) {
		}

		return false;
	}

	private boolean isGhostText(JTextComponent comp) {
		return comp.getFont().equals(GHOST_FONT);
	}

	private static final Font NORMAL_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);
	private static final Font GHOST_FONT = new Font(Font.MONOSPACED, Font.ITALIC, 14);

	private void addGhostText(JTextComponent comp, String text) {
		comp.setForeground(Color.LIGHT_GRAY);
		comp.setText(text);
		comp.setFont(GHOST_FONT);
	}

	private void removeGhostText(JTextComponent comp) {
		comp.setForeground(Color.BLACK);
		comp.setText("");
		comp.setFont(NORMAL_FONT);
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Autotyper Error", JOptionPane.ERROR_MESSAGE);
	}

	private boolean showPrompt(String message) {
		final int val = JOptionPane.showConfirmDialog(this, message, "Autotyper Prompt", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		return val == JOptionPane.YES_OPTION;
	}

	private void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Autotyper Message", JOptionPane.INFORMATION_MESSAGE);
	}

	private void saveToHistory(String loc) {
		if (Arrays.asList(this.locations).contains(loc)) return;

		if (this.pointer < this.locations.length) {
			this.locations[this.pointer++] = loc;
		} else {
			for (int i = 1; i < (this.locations.length - 1); i++) {
				this.locations[i - 1] = this.locations[i];
			}

			this.locations[this.pointer - 1] = loc;
		}
	}

	private int getPointer(Object[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == null) return i;
		}

		return arr.length;
	}

	private CompletionProvider updateLocationCompletionProvider(DefaultCompletionProvider provider) {
		provider.clear();

		if (this.pointer == 0) {
			this.locations[0] = "JCR8YTww";
			this.locations[1] = "6gyLvm4K";
			this.locations[2] = "nAinUn1h";
			this.pointer = 3;
		}

		for (int i = 0; i < this.locations.length; i++) {
			if (this.locations[i] == null) {
				break;
			}

			provider.addCompletion(new BasicCompletion(provider, this.locations[i]));
		}

		checkDefaultCompletions(provider);
		return provider;
	}

	private void checkDefaultCompletions(DefaultCompletionProvider provider) {

		final List<Completion> bubbles = Lists.newArrayList();
		final List<Completion> milkshake = Lists.newArrayList();
		final List<Completion> calc = Lists.newArrayList();

		if (provider.getCompletionByInputText("JCR8YTww") != null) {
			bubbles.addAll(provider.getCompletionByInputText("JCR8YTww"));
		}

		if (provider.getCompletionByInputText("6gyLvm4K") != null) {
			milkshake.addAll(provider.getCompletionByInputText("6gyLvm4K"));
		}

		if (provider.getCompletionByInputText("nAinUn1h") != null) {
			calc.addAll(provider.getCompletionByInputText("nAinUn1h"));
		}

		if (bubbles.size() != 0) {
			provider.removeCompletion(bubbles.get(0));
			provider.addCompletion(new BasicCompletion(provider, "JCR8YTww", "Bubbles! by KingofGamesYami"));
		}
		if (milkshake.size() != 0) {
			provider.removeCompletion(milkshake.get(0));
			provider.addCompletion(new BasicCompletion(provider, this.locations[1], "Milkshake GUI by lednerg"));
		}
		if (calc.size() != 0) {
			provider.removeCompletion(calc.get(0));
			provider.addCompletion(new BasicCompletion(provider, this.locations[2], "Advanced Calculator by Cranium"));
		}

	}

	@Override
	public void doShow() {
		setVisible(true);
	}

	@Override
	public void doHide() {
		setVisible(false);
	}

}
