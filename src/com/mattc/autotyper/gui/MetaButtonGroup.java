package com.mattc.autotyper.gui;

import com.google.common.collect.Maps;
import com.mattc.autotyper.meta.SwingCompatible;

import java.util.Enumeration;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

// TODO Rewrite and Deprecate. Delegate this role to a more effective design.
/**
 * A ButtonGroup designed to hold arbitrary Metadata on Button's. The data is put and
 * received in a similar way to Properties. In a series of Key-Value Pairs. To ensure
 * casting, any non-string Value must be given the Class to cast to.
 * 
 * @author Matthew
 */
@SwingCompatible
public class MetaButtonGroup extends ButtonGroup {

	private static final long serialVersionUID = 748697940184112105L;

	private static final String DEFAULT_PROPERTY = "_DEF_PROPERTY_";
	private final Map<ButtonModel, Map<String, Object>> metaMap = Maps.newHashMap();

	public MetaButtonGroup() {
		super();
	}

	public void add(AbstractButton button, String meta) {
		final Map<String, Object> property = Maps.newHashMap();
		property.put(DEFAULT_PROPERTY, meta);
		add(button, property);
	}

	public void add(AbstractButton button, Object... metaValues) {
		add(button, toMap(metaValues));
	}

	public void add(AbstractButton button, Map<String, Object> metaValues) {
		this.metaMap.put(button.getModel(), metaValues);
		add(button);
	}

	public void putProperty(AbstractButton button, String key, Object val) {
		if (!this.metaMap.containsKey(button.getModel())) throw new IllegalArgumentException("Button Never Registered!");

		this.metaMap.get(button.getModel()).put(key, val);
	}

	public <T> T getMeta(AbstractButton button, Class<T> clazz) {
		return getMeta(button, DEFAULT_PROPERTY, clazz);
	}

	public <T> T getMeta(AbstractButton button, String property, Class<T> clazz) {
		return clazz.cast(this.metaMap.get(button.getModel()).get(property));
	}

	public <T> T getMetaForSelected(Class<T> clazz) {
		return getMetaForSelected(DEFAULT_PROPERTY, clazz);
	}

	public <T> T getMetaForSelected(String property, Class<T> clazz) {
		return clazz.cast(this.metaMap.get(getSelection()).get(property));
	}

	public String getMetaString(AbstractButton button) {
		return getMetaString(button, DEFAULT_PROPERTY);
	}

	public String getMetaString(AbstractButton button, String property) {
		return getMeta(button, property, String.class);
	}

	public String getMetaStringForSelected() {
		return getMetaStringForSelected(DEFAULT_PROPERTY);
	}

	public String getMetaStringForSelected(String property) {
		return getMetaForSelected(property, String.class);
	}

	public void setSelectedForProperty(String key, Object val) {
		final Enumeration<AbstractButton> buttons = getElements();

		while (buttons.hasMoreElements()) {
			final AbstractButton b = buttons.nextElement();

			if (this.metaMap.get(b.getModel()).get(key).equals(val)) {
				b.setSelected(true);
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

}
