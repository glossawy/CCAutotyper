package com.mattc.autotyper.gui;

import com.mattc.autotyper.meta.SwingCompatible;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * A Document designed to set a character limit on Text Input Components (e.g.
 * JTextField, JTextArea, etc.) using the JTextComponent.setDocument method.
 * 
 * @author Matthew
 */
@SwingCompatible
public class TextLimitDocument extends PlainDocument {

	private static final long serialVersionUID = 2050611792142151267L;

	private int limit;

	public TextLimitDocument(int limit) {
		this.limit = limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getLimit() {
		return this.limit;
	}

	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		if (str == null) return;

		if ((getLength() + str.length()) <= this.limit) {
			super.insertString(offs, str, a);
		}
	}

}
