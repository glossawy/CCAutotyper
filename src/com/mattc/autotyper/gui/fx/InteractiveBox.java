package com.mattc.autotyper.gui.fx;

import java.util.ArrayList;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import com.google.common.collect.Lists;
import com.mattc.autotyper.meta.FXCompatible;
import com.mattc.autotyper.meta.InDev;
import com.mattc.autotyper.meta.NodeParser;

@FXCompatible
@InDev(sinceVersion = 2.0, lastUpdate = 2.0, author = "Matthew Crocco")
public class InteractiveBox extends HBox {

	private final ArrayList<Node> interactiveNodes = Lists.newArrayList();

	public InteractiveBox(String format, Pos pos) {
		getChildren().addAll(new NodeParser().parse(format));

		for (final Node n : getChildren()) {
			if (n instanceof Label) {
				continue;
			}

			this.interactiveNodes.add(n);
		}

		setAlignment(pos);
	}

	public <T extends Node> T getInteractiveChild(int index, Class<T> klass) {
		return klass.cast(this.interactiveNodes.get(index));
	}

}
