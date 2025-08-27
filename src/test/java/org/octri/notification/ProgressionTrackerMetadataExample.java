package org.octri.notification;

import java.time.LocalDate;
import java.util.List;

import org.octri.notification.domain.AbstractReminderDayProgressionTracker;

public class ProgressionTrackerMetadataExample extends AbstractReminderDayProgressionTracker {

	public ProgressionTrackerMetadataExample() {

	}

	public ProgressionTrackerMetadataExample(LocalDate startDate, List<Integer> reminderDays) {
		super(startDate, reminderDays);
	}
}
