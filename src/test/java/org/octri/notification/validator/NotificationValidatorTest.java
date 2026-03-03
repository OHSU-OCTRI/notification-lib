package org.octri.notification.validator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.octri.notification.domain.Notification;
import org.octri.notification.domain.ValidationResult;

/**
 * Test class for the NotificationValidator functional interface.
 */
public class NotificationValidatorTest {

	@Test
	public void testNoopValidator() {
		NotificationValidator validator = NotificationValidator.NOOP;
		var result = validator.validate(null);
		assertTrue(result.successful(), "The noop validator should return a successful validation result.");
		assertTrue(result.invalidReason() == null, "The noop validator should return a null invalid reason.");
	}

	@Test
	public void testCustomValidator() {
		NotificationValidator validator = notification -> notification.getId() == 1L ? new ValidationResult(true, null)
				: new ValidationResult(false, "Invalid notification");

		Notification testNotification = new Notification();
		testNotification.setId(1L);
		var result = validator.validate(testNotification);
		assertTrue(result.successful(), "The custom validator should return a successful validation result.");
		assertTrue(result.invalidReason() == null, "The custom validator should return a null invalid reason.");

		testNotification.setId(2L);
		result = validator.validate(testNotification);
		assertTrue(!result.successful(), "The custom validator should return an unsuccessful validation result.");
		assertTrue(result.invalidReason() != null, "The custom validator should return a non-null invalid reason.");
	}

}
