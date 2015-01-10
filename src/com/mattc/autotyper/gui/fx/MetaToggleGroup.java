package com.mattc.autotyper.gui.fx;

import java.util.Map;
import java.util.Map.Entry;

import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import com.google.common.collect.Maps;
import com.mattc.autotyper.meta.FXCompatible;

@FXCompatible
public class MetaToggleGroup extends ToggleGroup {

	private static final String DEFAULT_PROPERTY = "_DEF_PROPERTY_";
	private final Map<Toggle, Map<String, Object>> metaMap = Maps.newHashMap();

	public void add(Toggle button, Object meta) {
		final Map<String, Object> property = Maps.newHashMap();
		property.put(DEFAULT_PROPERTY, meta);
		this.add(button, property);
	}

	public void add(Toggle button, Object... metaValues) {
		this.add(button, toMap(metaValues));
	}

	public void add(Toggle button, Map<String, Object> metaValues) {
		this.metaMap.put(button, metaValues);
		super.getToggles().add(button);
	}

	public void putProperty(Toggle button, String key, Object val) {
		if (!this.metaMap.containsKey(button)) throw new IllegalArgumentException("Button Never Registered!");

		this.metaMap.get(button).put(key, val);
	}

	public <T> T getMeta(Toggle button, Class<T> clazz) {
		return getMeta(button, DEFAULT_PROPERTY, clazz);
	}

	public <T> T getMeta(Toggle button, String property, Class<T> clazz) {
		return clazz.cast(this.metaMap.get(button).get(property));
	}

	public <T> T getMetaForSelected(Class<T> clazz) {
		return getMetaForSelected(DEFAULT_PROPERTY, clazz);
	}

	public <T> T getMetaForSelected(String property, Class<T> clazz) {
		return clazz.cast(this.metaMap.get(getSelectedToggle()).get(property));
	}

	public String getMetaString(Toggle button) {
		return getMetaString(button, DEFAULT_PROPERTY);
	}

	public String getMetaString(Toggle button, String property) {
		return getMeta(button, property, String.class);
	}

	public String getMetaStringForSelected() {
		return getMetaStringForSelected(DEFAULT_PROPERTY);
	}

	public String getMetaStringForSelected(String property) {
		return getMetaForSelected(property, String.class);
	}

	public void setSelectedForProperty(String key, Object val) {

		for (final Toggle b : getToggles()) {
			if (this.metaMap.get(b).get(key).equals(val)) {
				selectToggle(b);
				return;
			}
		}
	}

	private Map<String, Object> toMap(Object... values) {
		final Map<String, Object> map = Maps.newHashMap();

		for (int i = 0; i < values.length; i += 2) {
			map.put((String) values[i], values[i + 1]);
		}

		return map;
	}

	private Map<Toggle, Object> toToggleMap(Object... values) {
		final Map<Toggle, Object> map = Maps.newHashMap();

		for (int i = 0; i < values.length; i += 2) {
			map.put((Toggle) values[i], values[i + 1]);
		}

		return map;
	}

	public static void addTogglesToGroup(MetaToggleGroup group, Map<Toggle, Object> defaultProps) {
		for (final Entry<Toggle, Object> tProp : defaultProps.entrySet()) {
			group.add(tProp.getKey(), tProp.getValue());
		}
	}

	public static void addTogglesToGroup(MetaToggleGroup group, Object... defaultProps) {
		addTogglesToGroup(group, group.toToggleMap(defaultProps));
	}
}
