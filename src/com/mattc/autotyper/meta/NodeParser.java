package com.mattc.autotyper.meta;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.mattc.autotyper.util.Console;

@InDev(sinceVersion = 2.0, lastUpdate = 2.0, author = "Matthew Crocco")
@FXCompatible
public class NodeParser {

	private static final Map<String, Class<? extends Node>> nodeMap = Maps.newHashMap();

	static {

		try {
			findParseableNodes(nodeMap);
		} catch (final ClassNotFoundException e) {
			Console.exception(e);
		}

		// Non-API Parseables
		nodeMap.put("%b", Button.class);
		nodeMap.put("%t", TextField.class);
		nodeMap.put("%c", ComboBox.class);
		nodeMap.put("%B", ToggleButton.class);
	}

	public List<Node> parse(String format) {
		final Scanner scan = new Scanner(format);
		final List<Node> nodes = Lists.newArrayList();
		final StringBuilder sb = new StringBuilder();

		while (scan.hasNext()) {
			final String temp = scan.next();

			if (nodeMap.containsKey(temp)) {
				try {
					nodes.add(new Label(sb.append(" ").toString()));
					nodes.add(createInstanceOf(nodeMap.get(temp)));
					nodes.add(new Label(" "));
					sb.setLength(0);
					sb.trimToSize();
				} catch (InstantiationException | IllegalAccessException e) {
					Console.exception(e);
				}
			} else if (temp.startsWith("%")) {
				scan.close();
				throw new IllegalStateException("Tag is in Tag Format but is not a VALID Tag! -- " + temp);
			} else {
				sb.append(temp).append(" ");
			}
		}

		nodes.add(new Label(sb.toString()));

		scan.close();
		return nodes;
	}

	private Node createInstanceOf(Class<? extends Node> klass) throws InstantiationException, IllegalAccessException {
		try {
			boolean unlocked = false;
			final Constructor<? extends Node> con = klass.getDeclaredConstructor();
			if ((unlocked = !con.isAccessible())) {
				con.setAccessible(true);
			}
			con.setAccessible(true);
			final Node val = con.newInstance();
			if (unlocked) {
				con.setAccessible(false);
			}

			return val;
		} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException("No Private/Protected/Public No-Arg Constructor to Initialize!", e);
		}
	}

	@SuppressWarnings("unchecked")
	private static final void findParseableNodes(Map<String, Class<? extends Node>> map) throws ClassNotFoundException {

		try {
			final ImmutableSet<ClassInfo> klasses = ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClassesRecursive("com.mattc");

			for (final ClassInfo ci : klasses) {
				final Class<?> klass = Class.forName(ci.getName());

				final FXParseable anno = klass.getAnnotation(FXParseable.class);
				if ((anno != null) && !Node.class.isAssignableFrom(klass)) throw new IllegalStateException("Non-Parseable Class tagged with @FXParseable! A Parseable Class is one that inherits from Node...");
				if ((anno == null) || !Node.class.isAssignableFrom(klass)) {
					continue;
				}

				String tag = anno.value();

				if (tag.equals(FXParseable.NO_VAL)) {
					tag = NodeParser.createTagFor(klass);
				} else if (!tag.startsWith("%"))
					throw new IllegalArgumentException("Attempted to set tag to " + tag + " but a tag must start with a '%'!");
				else if (map.containsKey(tag)) throw new IllegalArgumentException("Attempt to set tag to " + tag + " for " + klass.getSimpleName() + " but it conflicts with another tag!");

				Console.info("Add FXParseable " + ci.getName() + " as " + tag);
				map.put(tag, (Class<? extends Node>) klass);
			}

		} catch (final IOException e) {
			Console.exception(e);
		}

	}

	public static final String createTagFor(Class<?> klass) {
		final char[] name = klass.getSimpleName().toCharArray();
		final StringBuilder sb = new StringBuilder("%");

		for (final char c : name)
			if (Character.isUpperCase(c)) {
				sb.append(c);
			}

		return sb.toString().toLowerCase();
	}

}
