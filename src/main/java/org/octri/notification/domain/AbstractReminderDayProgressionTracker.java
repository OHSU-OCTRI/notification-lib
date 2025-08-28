package org.octri.notification.domain;

import java.time.LocalDate;
import java.util.List;

import org.octri.notification.metadata.NotificationMetadata;

/**
 * An implementation of a {@link ReminderDayProgressionTracker} and {@link NotificationMetadata} to be used when you
 * want to configure the batch job to create the next {@link Notification} in the series at the time the first
 * Notification is processed. Create this object once with the set of reminder days and the initial index of 0, and
 * persist a Notification with this metadata. The batch job will create subsequent Notifications with the same
 * characteristics, advancing the scheduled date to the next in the series.
 */
public abstract class AbstractReminderDayProgressionTracker
		implements ReminderDayProgressionTracker, NotificationMetadata {

	private List<Integer> reminderDays;
	private int currentIndex;
	private LocalDate startDate;

	/**
	 * Default constructor for serialization.
	 */
	public AbstractReminderDayProgressionTracker() {

	}

	/**
	 * 
	 * @param startDate
	 *            the date from which to calculate subsequent reminder days
	 * @param reminderDays
	 *            the series of days on which to send reminders, relative to the start date
	 */
	public AbstractReminderDayProgressionTracker(LocalDate startDate, List<Integer> reminderDays) {
		this.startDate = startDate;
		this.reminderDays = reminderDays;
		this.currentIndex = 0;
	}

	@Override
	public LocalDate getStartDate() {
		return startDate;
	}

	@Override
	public List<Integer> getReminderDays() {
		return reminderDays;
	}

	@Override
	public int getCurrentIndex() {
		return currentIndex;
	}

	@Override
	public void setCurrentIndex(int index) {
		this.currentIndex = index;
	}
}
