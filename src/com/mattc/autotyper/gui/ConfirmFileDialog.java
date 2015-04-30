package com.mattc.autotyper.gui;

import com.google.common.io.Files;
import com.mattc.autotyper.meta.FXCompatible;
import com.mattc.autotyper.meta.SwingCompatible;
import com.mattc.autotyper.util.Console;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * Takes in Code or a Code File and displays the text in a TextArea with a
 * highlighted syntax using RSyntaxTextArea lib. <br />
 * <br />
 * Accept or Reject Options with multiple syntaxes.
 * 
 * @author Matthew
 */
@FXCompatible
@SwingCompatible
public class ConfirmFileDialog extends JDialog {

	private static final long serialVersionUID = -5039462697501272483L;

	private final String curSyntax = SyntaxConstants.SYNTAX_STYLE_LUA;
	private final String text;
	private boolean approved = false;

	public ConfirmFileDialog(JFrame parent, File code) throws IOException {
		super(parent, "Code Confirmation", ModalityType.APPLICATION_MODAL);

		this.text = Files.readLines(code, StandardCharsets.UTF_8, new SingleStringProcessor());

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		initComponents();
		setSize(960, 650);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public boolean isApproved() {
		return this.approved;
	}

	private void initComponents() {
		final JPanel cont = new JPanel();
		final JPanel options = new JPanel(new GridLayout(1, 6));

		final RSyntaxTextArea codeArea = new RSyntaxTextArea(30, 110);
		final RTextScrollPane scroll = new RTextScrollPane(codeArea);
		final JComboBox<SyntaxInfo> languageList = new JComboBox<SyntaxInfo>(getLanguageChoices());
		final JButton approveButton = new JButton("Approve");
		final JButton rejectButton = new JButton("Reject");

		cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));

		codeArea.setText(this.text);
		codeArea.setSyntaxEditingStyle(this.curSyntax);
		codeArea.setEditable(false);
		codeArea.setCaretPosition(0);
		codeArea.setAntiAliasingEnabled(true);
		codeArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, codeArea.getFont().getSize()));

		scroll.setLineNumbersEnabled(true);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		((JLabel) languageList.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		languageList.setSelectedItem(new SyntaxInfo("text/lua"));

		options.add(approveButton);
		options.add(rejectButton);
		options.add(languageList);

		cont.add(scroll);
		cont.add(options);

		approveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConfirmFileDialog.this.approved = true;
				dispose();
			}
		});

		rejectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ConfirmFileDialog.this.approved = false;
				dispose();
			}
		});

		languageList.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					final int pos = codeArea.getCaretPosition();
					final SyntaxInfo info = (SyntaxInfo) e.getItem();
					codeArea.setSyntaxEditingStyle(info.getSyntax());
					codeArea.setText(codeArea.getText());
					codeArea.setCaretPosition(pos);
				}
			}
		});

		setContentPane(cont);
	}

	private SyntaxInfo[] getLanguageChoices() {
		final Field[] allFields = SyntaxConstants.class.getFields();
		final SyntaxInfo[] langs = new SyntaxInfo[allFields.length];

		for (int i = 0; i < allFields.length; i++) {
			try {
				langs[i] = new SyntaxInfo((String) allFields[i].get(null));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				Console.exception(e);
			}
		}

		return langs;
	}

	private static class SyntaxInfo {

		public final String syntaxCode;
		public final String displayName;

		private SyntaxInfo(String syntax) {
			this.syntaxCode = syntax;
			this.displayName = syntax.split("/")[1];
		}

		public String getSyntax() {
			return this.syntaxCode;
		}

		@Override
		public String toString() {
			return this.displayName;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (prime * result) + ((this.displayName == null) ? 0 : this.displayName.hashCode());
			result = (prime * result) + ((this.syntaxCode == null) ? 0 : this.syntaxCode.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			final SyntaxInfo other = (SyntaxInfo) obj;
			if (this.displayName == null) {
				if (other.displayName != null) return false;
			} else if (!this.displayName.equals(other.displayName)) return false;
			if (this.syntaxCode == null) {
				if (other.syntaxCode != null) return false;
			} else if (!this.syntaxCode.equals(other.syntaxCode)) return false;
			return true;
		}

	}

}
