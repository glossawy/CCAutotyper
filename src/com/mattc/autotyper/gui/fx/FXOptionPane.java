package com.mattc.autotyper.gui.fx;

import com.google.common.annotations.Beta;
import com.mattc.autotyper.Ref;
import com.mattc.autotyper.Strings.Resources;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.concurrent.atomic.AtomicBoolean;

@Beta
public class FXOptionPane extends Stage {

	public static final int MESSAGE_MIN_WIDTH = 180;
	public static final int MESSAGE_MAX_WIDTH = 800;
	public static final int BUTTON_WIDTH = 60;
	public static final int MARGIN = 10;

	private FXOptionPane(Builder builder) {
		super(StageStyle.UTILITY);

		if (builder.owner != null) {
			initOwner(builder.owner);
		}

		initModality(builder.modality);
		setTitle(builder.title);
		setResizable(builder.resizable);

		toFront();
		centerOnScreen();
		FXGuiUtils.makeAlwaysOnTop(this);


        if(builder.width != -1 && builder.height != -1)
		    setScene(new Scene(builder.root, builder.width, builder.height));
        else
            setScene(new Scene(builder.root));

        if (builder.blocking) {
			showAndWait();
		} else {
			show();
		}
	}

	public static Builder builder() {
		return Builder.create("");
	}

	public static Builder builder(String message) {
		return Builder.create(message);
	}

	public static void showMessage(Window owner, String title, String message, IconType icon) {
		Builder.create(message).setImage(icon).makeOkButton(DEFAULT_CLOSE_ACTION).setOwner(owner).setModality(Modality.APPLICATION_MODAL).setTitle(title == null ? Ref.TITLE + " Message" : title).build();
	}

	public static void showMessage(String title, String message, IconType icon) {
		showMessage(null, title, message, icon);
	}

	public static void showMessage(String title, String message) {
		showMessage(null, title, message, IconType.INFO);
	}

	public static void showMessage(String message) {
		Builder.create(message).toInfo().makeOkButton(DEFAULT_CLOSE_ACTION).setModality(Modality.APPLICATION_MODAL).build();
	}

	public static boolean showConfirmation(Window owner, String title, String message, OptionType options, IconType icon) {
		final AtomicBoolean confirmed = new AtomicBoolean(false);
		final Builder builder = Builder.create(message).setTitle(title).setImage(icon).setOwner(null).makeBlocking().setModality(Modality.APPLICATION_MODAL);

		options.install(builder, confirmed);
		builder.build();
		return confirmed.get();
	}

	public static boolean showConfirmation(String title, String message, OptionType options, IconType icon) {
		return showConfirmation(null, title, message, options, icon);
	}

	public static boolean showConfirmation(String title, String message, OptionType options) {
		return showConfirmation(title, message, options, IconType.CONFIRM);
	}

	public static boolean showConfirmation(String message, OptionType options) {
		return showConfirmation(Ref.TITLE + " Prompt", message, options);
	}

	public static class Builder {

		private Window owner = null;
		private Modality modality = Modality.APPLICATION_MODAL;
		private String title = Ref.TITLE + " Prompt";

		private final BorderPane root = new BorderPane();
		private final ImageView icon = new ImageView();

        private double width = -1, height = -1;

		private final HBox msgBox = new HBox();
		private Button accept;
		private Button reject;
		private final Label label;

		private final HBox btnBox = new HBox();

		private boolean resizable = true;
		private boolean blocking = false;

		private Builder(String message) {
			this.label = new Label(message);
			this.label.setWrapText(true);
			this.label.setMinWidth(MESSAGE_MIN_WIDTH);
			this.label.setMaxWidth(MESSAGE_MAX_WIDTH);

			toInfo();

			this.msgBox.setAlignment(Pos.CENTER_LEFT);
			this.msgBox.getChildren().add(this.label);

			this.btnBox.setSpacing(MARGIN);
			this.btnBox.setAlignment(Pos.BOTTOM_CENTER);

			BorderPane.setAlignment(this.msgBox, Pos.CENTER);
			BorderPane.setMargin(this.msgBox, new Insets(MARGIN, MARGIN, MARGIN, 2 * MARGIN));
			BorderPane.setMargin(this.btnBox, new Insets(0, 0, 1.5 * MARGIN, 0));
			BorderPane.setMargin(this.icon, new Insets(MARGIN));

			this.root.setLeft(this.icon);
			this.root.setCenter(this.msgBox);
			this.root.setBottom(this.btnBox);

			this.label.setId("optionpane-label");
			this.icon.setId("optionpane-icon");

			this.icon.setEffect(new DropShadow(this.icon.getImage().getWidth(), Color.LIGHTGRAY));
		}

		public static Builder create(String message) {
			final Builder builder = new Builder(message);

			return builder;
		}

		public Builder setResizable(boolean resize) {
			this.resizable = resize;

			return this;
		}

		public Builder setOwner(Window owner) {
			this.owner = owner;

			return this;
		}

		public Builder setModality(Modality modality) {
			this.modality = modality;

			return this;
		}

		public Builder setTitle(String title) {
			this.title = title;

			return this;
		}

		public Builder setMessage(String message) {
			this.label.setText(message);

			return this;
		}

		public Builder setImage(Image ico) {
			this.icon.setImage(ico);

			return this;
		}

		public Builder setImageFromResource(String resource) {
			return setImage(new Image(Resources.getImage(resource).stream()));
		}

		public Builder setImage(IconType ico) {
			return setImageFromResource(ico.imageName);
		}

		public Builder toWarning() {
			return setImage(IconType.WARNING);
		}

		public Builder toError() {
			return setImage(IconType.ERROR);
		}

		public Builder toInfo() {
			return setImage(IconType.INFO);
		}

		public Builder toConfirmation() {
			return setImage(IconType.CONFIRM);
		}

		public Builder makeBlocking() {
			this.blocking = true;

			return this;
		}

		public Builder makeOkButton(EventHandler<ActionEvent> onClick) {
			this.accept = createButton("Ok", onClick);

			return this;
		}

		public Builder makeCancelButton(EventHandler<ActionEvent> onClick) {
			this.reject = createButton("Cancel", onClick);

			return this;
		}

		public Builder makeYesButton(EventHandler<ActionEvent> onClick) {
			this.accept = createButton("Yes", onClick);

			return this;
		}

		public Builder makeNoButton(EventHandler<ActionEvent> onClick) {
			this.reject = createButton("No", onClick);

			return this;
		}

        public Builder setSize(double width, double height) {
            this.width = width;
            this.height = height;

            return this;
        }

		public void build() {

			if ((this.accept == null) && (this.reject == null)) throw new IllegalStateException("An FXOptionPane MUST have a Button!");

			if (this.accept != null) {
				this.btnBox.getChildren().add(this.accept);
				this.accept.setId("optionpane-button");
			}

			if (this.reject != null) {
				this.btnBox.getChildren().add(this.reject);
				this.reject.setId("optionpane-button");
			}

			final Runnable r = new Runnable() {
				@Override
				public void run() {
					new FXOptionPane(Builder.this);
				}
			};

			if (Platform.isFxApplicationThread()) {
				r.run();
			} else {
				Platform.runLater(r);
			}

		}

		private Button createButton(String lbl, EventHandler<ActionEvent> onClick) {
			final Button b = new Button(lbl);
			b.setPrefWidth(BUTTON_WIDTH);
			b.setOnAction(onClick);

			return b;
		}
	}

	public enum IconType {
		INFO("infoIcon.png"), WARNING("warningIcon.png"), ERROR("errorIcon.png"), CONFIRM("confirmationIcon.png");

		public final String imageName;

		private IconType(String imageName) {
			this.imageName = imageName;
		}
	}

	public enum OptionType {
		OK {
			@Override
			public void install(Builder builder, final AtomicBoolean state) {
				builder.makeOkButton(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent evt) {
						state.set(true);
						((Button) evt.getSource()).getScene().getWindow().hide();
					}
				});
			}
		},
		YES_NO {
			@Override
			public void install(Builder builder, final AtomicBoolean state) {
				builder.makeYesButton(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent evt) {
						state.set(true);
						((Button) evt.getSource()).getScene().getWindow().hide();
					}
				});

				builder.makeNoButton(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent evt) {
						state.set(false);
						((Button) evt.getSource()).getScene().getWindow().hide();
					}
				});
			}
		},
		OK_CANCEL {
			@Override
			public void install(Builder builder, final AtomicBoolean state) {
				builder.makeOkButton(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent evt) {
						state.set(true);
						((Button) evt.getSource()).getScene().getWindow().hide();
					}
				});

				builder.makeCancelButton(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent evt) {
						state.set(false);
						((Button) evt.getSource()).getScene().getWindow().hide();
					}
				});
			}
		};

		public void install(Builder builder, final AtomicBoolean stateSwitch) {
			throw new AbstractMethodError();
		}
	}

	static final EventHandler<ActionEvent> DEFAULT_CLOSE_ACTION = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent event) {
			((Node) event.getSource()).getScene().getWindow().hide();
		}

	};

}
