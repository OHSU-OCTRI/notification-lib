package org.octri.notification.batch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.octri.notification.NotificationTestUtil;
import org.octri.notification.ProgressionTrackerMetadataExample;
import org.octri.notification.RecipientExample;
import org.octri.notification.domain.DefaultNotificationStatus;
import org.octri.notification.domain.DispatchResult;
import org.octri.notification.domain.Notification;
import org.octri.notification.domain.Notification.NotificationStatusMetadata;
import org.octri.notification.domain.ValidationResult;

import com.fasterxml.jackson.core.JsonProcessingException;

public class NotificationItemWriterTest {

	private final static LocalDate TODAY = LocalDate.now();
	private final static LocalDate LAST_WEEK = TODAY.minusDays(7);
	private final static String recipientUuid1 = UUID.randomUUID().toString();

	@Test
	void testNextNotificationIsFinal() throws JsonProcessingException {
		ProgressionTrackerMetadataExample metadata = new ProgressionTrackerMetadataExample(TODAY, List.of(0));
		var notification = NotificationTestUtil.createNotification("example", new RecipientExample(recipientUuid1),
				metadata, TODAY);
		var nextNotification = NotificationItemWriter.nextNotification(notification, metadata);
		assertTrue(nextNotification.isEmpty(), "A final notification has no next notification.");
	}

	@Test
	void testNextNotificationIsPast() throws JsonProcessingException {
		ProgressionTrackerMetadataExample metadata = new ProgressionTrackerMetadataExample(LAST_WEEK,
				List.of(0, 1, 2, 3));
		var notification = NotificationTestUtil.createNotification("example", new RecipientExample(recipientUuid1),
				metadata, TODAY);
		var nextNotification = NotificationItemWriter.nextNotification(notification, metadata);
		assertTrue(nextNotification.isEmpty(), "All followup notifications are in the past.");
	}

	@Test
	void testNextNotificationIsFirstFuture() throws JsonProcessingException {
		ProgressionTrackerMetadataExample metadata = new ProgressionTrackerMetadataExample(LAST_WEEK,
				List.of(0, 6, 7, 8, 10));
		var notification = NotificationTestUtil.createNotification("example", new RecipientExample(recipientUuid1),
				metadata, TODAY);
		var nextNotification = NotificationItemWriter.nextNotification(notification, metadata);
		assertTrue(nextNotification.isPresent(), "A next notification was returned.");
		assertTrue(nextNotification.get().getDateScheduled().isAfter(TODAY), "The scheduled date is after today");
		var newMetadata = nextNotification.get().getNotificationMetadata(ProgressionTrackerMetadataExample.class);
		assertTrue(newMetadata.getCurrentIndex() == 3,
				"Days 6 and 7 where skipped because they are not in the future.");
	}

	@Test
	void testIsNotificationValid() {
		var validMetadata = new NotificationStatusMetadata(new ValidationResult(true, null), null);
		assertTrue(NotificationItemWriter.isNotificationValid(validMetadata),
				"Notification is valid if validation result is successful");

		var invalidMetadata = new NotificationStatusMetadata(new ValidationResult(false, null), null);
		assertFalse(NotificationItemWriter.isNotificationValid(invalidMetadata),
				"Notification is invalid if validation result is not successful");
	}

	@Test
	void testUpdateDispatchedNotification() {
		var validDispatch = new DispatchResult(true, null, null, null, null);
		var notification = NotificationItemWriter.updateDispatchedNotification(new Notification(),
				new ValidationResult(true, null), validDispatch);
		assertTrue(notification.getNotificationStatus().equals(DefaultNotificationStatus.SENT),
				"Notification status is updated to SENT");

		var invalidDispatch = new DispatchResult(false, null, null, null, null);
		notification = NotificationItemWriter.updateDispatchedNotification(new Notification(),
				new ValidationResult(true, null), invalidDispatch);
		assertTrue(notification.getNotificationStatus().equals(DefaultNotificationStatus.INACTIVE),
				"Notification status is updated to INACTIVE");
	}

	@Test
	void testUpdateInvalidNotification() {
		var notification = NotificationItemWriter.updateInvalidNotification(new Notification());
		assertTrue(notification.getNotificationStatus().equals(DefaultNotificationStatus.INACTIVE),
				"Notification status is updated to INACTIVE");
	}
}
