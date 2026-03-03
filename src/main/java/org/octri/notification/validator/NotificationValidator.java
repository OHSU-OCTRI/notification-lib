package org.octri.notification.validator;

import org.octri.notification.domain.Notification;
import org.octri.notification.domain.ValidationResult;

/**
 * Functional interface for a component that can validate whether a Notification should be sent. Provides a NOOP
 * validator that always returns a successful validation result.
 */
@FunctionalInterface
public interface NotificationValidator {

	public static final NotificationValidator NOOP = notification -> new ValidationResult(true, null);

	/**
	 * 
	 * @param notification
	 *            the Notification to validate
	 * @return the result of the validation
	 */
	public ValidationResult validate(Notification notification);

}
