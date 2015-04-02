package com.mattc.autotyper.meta;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mattc.autotyper.Strings.Resources;
import com.mattc.autotyper.util.Console;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class ModeParser {

	private static final Properties props = new Properties();
	private static final Splitter pipeSplit = Splitter.on('|').trimResults().omitEmptyStrings();

	private static final Map<String, Mode> modeMap;

	static {
		try {
			props.load(Resources.getRootFile("modes.properties").stream());
			final Map<String, Mode> tempMap = Maps.newHashMap();

			for (final Entry<Object, Object> entry : props.entrySet()) {
				final Mode m = new Mode(new Pair<String, String>((String) entry.getKey(), (String) entry.getValue()));
				tempMap.put(m.displayName, m);
				Console.info("Installed Mode " + m.mimeType + " for '" + m.displayName + "'...");
			}

			modeMap = ImmutableMap.copyOf(tempMap);
		} catch (final IOException e) {
			throw new IllegalStateException("Could not load Modes!", e);
		}
	}

	public static List<String> getPossibleModes() {
		final List<String> modes = Lists.newArrayList(modeMap.keySet());
		Collections.sort(modes, String.CASE_INSENSITIVE_ORDER);

		return modes;
	}

	public static Mode getModeFor(String language) {
		if (!modeMap.containsKey(language)) throw new IllegalArgumentException("No Mode for " + language);

		return modeMap.get(language);
	}

	public static class Mode {
		public final String name;
		public final String displayName;
		public final String mimeType;

		public Mode(Pair<String, String> pair) {
			final String root = pair.getKey();
			this.mimeType = pair.getValue();

			if (root.contains("|")) {
				final List<String> list = pipeSplit.splitToList(root);

				this.name = list.get(0);
				this.displayName = list.get(1).replace('_', ' ');
			} else {
				this.name = root;
				this.displayName = root;
			}
		}

		@Override
		public String toString() {
			return String.format("{%s: \"%s\" - %s}", this.name, this.displayName, this.mimeType.toString());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (prime * result) + ((this.displayName == null) ? 0 : this.displayName.hashCode());
			result = (prime * result) + ((this.mimeType == null) ? 0 : this.mimeType.hashCode());
			result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			final Mode other = (Mode) obj;
			if (this.displayName == null) {
				if (other.displayName != null) return false;
			} else if (!this.displayName.equals(other.displayName)) return false;
			if (this.mimeType == null) {
				if (other.mimeType != null) return false;
			} else if (!this.mimeType.equals(other.mimeType)) return false;
			if (this.name == null) {
				if (other.name != null) return false;
			} else if (!this.name.equals(other.name)) return false;
			return true;
		}

	}

}
