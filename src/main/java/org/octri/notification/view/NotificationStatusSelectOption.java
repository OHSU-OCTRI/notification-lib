package org.octri.notification.view;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.octri.common.view.SelectOption;
import org.octri.notification.domain.NotificationStatus;

/**
 * A select option for NotificationStatus values
 *
 * @param <T>
 *            a type implementing NotificationStatus
 */
public class NotificationStatusSelectOption<T extends NotificationStatus> extends SelectOption<T> {

	/**
	 * Constructor
	 *
	 * @param choice
	 *            - item
	 * @param selected
	 *            - The selected item; may be null
	 */
	public NotificationStatusSelectOption(T choice, T selected) {
		super(choice, selected);
		this.setValue(choice.name());
		this.setLabel(choice.getLabel());
	}

	/**
	 * Given a collection of NotificationStatuses and the selected status, provides a list of objects that can be used
	 * directly by mustachejs for rendering.
	 *
	 * @param <T>
	 *            a type extending NotificationStatus
	 * @param iter
	 *            an iterable collection of NotificationStatus values
	 * @param selected
	 *            the current selection
	 * @return a list of select options for the values in the collection
	 */
	public static <T extends NotificationStatus> List<NotificationStatusSelectOption<T>> fromStatuses(Iterable<T> iter,
			T selected) {
		return StreamSupport.stream(iter.spliterator(), false)
				.map(item -> new NotificationStatusSelectOption<T>(item, selected))
				.collect(Collectors.toList());
	}
}