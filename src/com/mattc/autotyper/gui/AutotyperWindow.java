package com.mattc.autotyper.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

import com.mattc.autotyper.Keyboard;
import com.mattc.autotyper.Ref;

public class AutotyperWindow extends JFrame {

	private static final long serialVersionUID = -776172984918939880L;

	public AutotyperWindow(Keyboard keys) {
		super(Ref.TITLE + " | " + Ref.VERSION);

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setName("GUI");
				setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

				init();

				setSize(600, 500);
				setLocationRelativeTo(null);
				setVisible(false);
			}
		});
	}

	private void init() {
		getContentPane();

		final ButtonGroup group = new ButtonGroup();
		new JPanel(new BorderLayout());
		final JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		final JRadioButton file = new JRadioButton("Local File");
		final JRadioButton url = new JRadioButton("Website File");
		final JRadioButton paste = new JRadioButton("Pastebin Code");
		final JRadioButton auto = new JRadioButton("Auto Detect");
		new JButton("Type!");

		group.add(auto);
		group.add(url);
		group.add(file);
		group.add(paste);

		radioPanel.add(file);
		radioPanel.add(url);
		radioPanel.add(paste);
		radioPanel.add(auto);

	}

	@Override
	public void setVisible(boolean visible) {
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
		super.dispose();
	}
}
