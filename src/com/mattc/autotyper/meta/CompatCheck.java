package com.mattc.autotyper.meta;

import java.awt.Component;
import java.awt.EventQueue;

import javafx.application.Platform;
import javafx.scene.Node;

public class CompatCheck {

	public static final boolean isFXCompatible(Class<? extends Component> klass) {
		return klass.getAnnotation(FXCompatible.class) != null;
	}

	public static final boolean isSwingCompatible(Class<? extends Node> klass) {
		return klass.getAnnotation(SwingCompatible.class) != null;
	}

	public static final boolean shouldRunInFX(Component comp) {
		return isFXCompatible(comp.getClass()) && !EventQueue.isDispatchThread();
	}

	public static final boolean shouldRunInEDT(Node node) {
		return isSwingCompatible(node.getClass()) && !Platform.isFxApplicationThread();
	}

}
