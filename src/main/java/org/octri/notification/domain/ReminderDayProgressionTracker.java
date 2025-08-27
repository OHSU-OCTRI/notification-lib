package org.octri.notification.domain;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for tracking progression through a series of reminder days.
 */
public interface ReminderDayProgressionTracker {

	/**
	 * @return The anchor date that followup days will be calculated from
	 */
	public LocalDate getStartDate();

	/**
	 * The series of reminder days this tracker follows (e.g., [1, 2, 4, 7]).
	 *
	 * @return an immutable list of integers representing the reminder days
	 */
	public List<Integer> getReminderDays();

	/**
	 * @return The current index in the progression list
	 */
	public int getCurrentIndex();

	/**
	 * Update the current index.
	 * 
	 * @param index
	 *            the new index
	 */
	public void setCurrentIndex(int index);

	/**
	 * @return The current day in the series.
	 */
	public default int getCurrentDay() {
		return getReminderDays().get(getCurrentIndex());
	}

	/**
	 * @return the date corresponding to the current index calculated from the start date of the series
	 */
	public default LocalDate getCurrentDate() {
		return getStartDate().plusDays(getCurrentDay());
	}

	/**
	 * @return Whether the tracker is currently at the last item in the series.
	 */
	public default boolean isFinal() {
		return getCurrentIndex() >= getReminderDays().size() - 1;
	}

	/**
	 * Advances to the next index in the series if possible.
	 */
	public default void advance() {
		if (!isFinal()) {
			setCurrentIndex(getCurrentIndex() + 1);
		}
	}
}
