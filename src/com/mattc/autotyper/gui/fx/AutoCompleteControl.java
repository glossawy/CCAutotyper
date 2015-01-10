package com.mattc.autotyper.gui.fx;

import java.util.List;

import javafx.scene.control.ListView;

import com.mattc.autotyper.meta.FXCompatible;
import com.mattc.autotyper.meta.SwingCompatible;

/**
 * A JavaFX Control that can handle AutoCompletion input. Typically a Text Control.
 * 
 * @author Matthew
 * @param <T>
 */
@FXCompatible
@SwingCompatible
public interface AutoCompleteControl<T> {

	/**
	 * Set the Auto Completion data for this Auto Complete Control.
	 * 
	 * @param data
	 */
	void setData(List<T> data);

	/**
	 * Get the Auto Completion data for this Auto Complete Control.
	 * 
	 * @return
	 */
	List<T> getData();

	/**
	 * Get the List View associated with the Auto Completion data and this Auto
	 * Complete Control.
	 * 
	 * @return
	 */
	ListView<T> getListView();

	/**
	 * Set the Max Size of the List of Auto Complete Data
	 * 
	 * @param max
	 */
	void setMaxLength(int max);

	/**
	 * Get Max Auto Complete Data Count
	 * 
	 * @return
	 */
	int getMaxLength();

}
