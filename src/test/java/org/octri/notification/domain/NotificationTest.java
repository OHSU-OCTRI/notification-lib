package org.octri.notification.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.octri.notification.ProgressionTrackerMetadataExample;
import org.octri.notification.metadata.EmptyMetadata;

import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;

/**
 * Tests written during upgrade to Jackson 3, to validate the changed defaults and updated code.
 */
public class NotificationTest {

	private final static LocalDate START_DATE = LocalDate.of(2025, 1, 1);

	/**
	 * The default behavior for this feature has flipped, so verify that the default settings allow empty beans to be
	 * serialized/deserialized.
	 */
	@Test
	void testEmptyMetadata() {
		var mapper = new Notification().getObjectMapper();
		assertFalse(mapper.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS),
				"The metadata mapper must not fail on empty beans");

		var notification = new Notification();
		notification.setNotificationMetadata(new EmptyMetadata());
		assertEquals("{}", notification.getNotificationMetadata(), "Empty metadata is serialized as an empty object");
		assertNotNull(notification.getNotificationMetadata(EmptyMetadata.class),
				"Empty metadata can be deserialized");
	}

	/**
	 * Jackson 3's JsonMapper class is immutable, so the {@link Notification} class has updated how its mapper is
	 * constructed, to accomodate mixins.
	 */
	@Test
	void testProgressionTrackerMixinApplied() {
		var notification = new Notification();
		notification.setNotificationMetadata(new ProgressionTrackerMetadataExample(START_DATE, List.of(0, 7)));
		var json = notification.getNotificationMetadata();
		assertFalse(json.contains("\"currentDay\""), "currentDay should not be serialized");
		assertFalse(json.contains("\"currentDate\""), "currentDate should not be serialized");
		assertFalse(json.contains("\"final\""), "final should not be serialized");

		// Sanity check that fields without @JsonIgnore correctly survive a round trip
		var tracker = notification.getNotificationMetadata(ProgressionTrackerMetadataExample.class);
		assertEquals(START_DATE, tracker.getStartDate(), "startDate should be deserialized");
	}

	/**
	 * Previously, the {@link Notification} class set `mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)`.
	 * This feature has been moved to {@link DateTimeFeatures} and the default behavior has been flipped.
	 */
	@Test
	void testDateSerialization() {
		var notification = new Notification();
		assertFalse(notification.getObjectMapper().isEnabled(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS),
				"The metadata mapper should not write dates as timestamps");

		notification.setNotificationMetadata(new ProgressionTrackerMetadataExample(START_DATE, List.of(0, 7)));
		assertTrue(notification.getNotificationMetadata().contains("\"startDate\":\"2025-01-01\""),
				"startDate should be serialized as an ISO date string");
	}

}
