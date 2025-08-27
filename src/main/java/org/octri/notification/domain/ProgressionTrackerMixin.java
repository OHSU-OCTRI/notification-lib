package org.octri.notification.domain;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Used to avoid unnecessary mapping of some ReminderDayProgressionTracker fields when persisting metadata to the
 * database
 */
public abstract class ProgressionTrackerMixin {

	/**
	 * 
	 * @return isFinal
	 */
	@JsonIgnore
	public abstract boolean isFinal();

	/**
	 * 
	 * @return currentDay
	 */
	@JsonIgnore
	public abstract int getCurrentDay();

	/**
	 * 
	 * @return currentDate
	 */
	@JsonIgnore
	public abstract LocalDate getCurrentDate();

}